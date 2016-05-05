package de.bstreit.java.oscr.business.export.consumption;

import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculator;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculatorFactory;
import de.bstreit.java.oscr.business.bill.calculator.WhatToCount;
import de.bstreit.java.oscr.business.bill.dao.IBillRepository;
import de.bstreit.java.oscr.business.staff.User;
import de.bstreit.java.oscr.business.util.DateFactory;


@Scope("prototype")
@Service
public class ConsumptionExporter {

  private static final DateFormat DATE_FORMAT = SimpleDateFormat
      .getDateInstance(SimpleDateFormat.SHORT);

  @Inject
  private IBillRepository billRepository;

  @Inject
  private Currency currency;

  @Inject
  private IMultipleBillsCalculatorFactory multipleBillsCalculatorFactory;

  @Value("${staffConsumption.breakfast.price}")
  private String breakfastPriceStr;

  private Money breakfastPrice;

  @Value("${staffConsumption.lunch.price}")
  private String lunchPriceStr;

  private Money lunchPrice;

  @Value("${staffConsumption.dinner.price}")
  private String dinnerPriceStr;

  @Value("${staffConsumption.managementUser}")
  private String managementUser;

  private Money dinnerPrice;

  private transient Map<User, ConsumptionCounter> consumption = Maps
      .newHashMap();

  private transient Map<User, List<Bill>> customerConsumption = Maps
      .newHashMap();

  private transient List<Bill> managementConsumption = Lists.newArrayList();

  private Appendable appendable = System.out;
  private boolean printManagementConsumption = true;
  private boolean printCustomerBills = true;

  /**
   * thisMonth == true: export current month; thisMonth == false: export
   * previous month (default).
   */
  private boolean thisMonth = false;


  @PostConstruct
  public void init() {
    breakfastPrice = new Money(breakfastPriceStr, currency);
    lunchPrice = new Money(lunchPriceStr, currency);
    dinnerPrice = new Money(dinnerPriceStr, currency);
  }

  @Transactional
  public void export() {
    try {

      final Collection<Bill> billsForStaff = getBillsForStaffLastMonth();

      printHeader();

      countBills(billsForStaff);
      printResults();

      if (printManagementConsumption) {
        printManagementConsumption();
      }

      if (printCustomerBills) {
        printCustomerBills();
      }

    } catch (IOException e) {
      System.err.println("Caught IOException! Aborting!");
      e.printStackTrace(System.err);

    } finally {
      consumption.clear();
      managementConsumption.clear();
      customerConsumption.clear();
    }
  }

  private void printCustomerBills() throws IOException {

    appendable.append("Customer Consumption (for billing):\n"
        + "===================================\n" + "\n");

    countCustomerBills(getBillsForCustomersLastMonth());

    printCustomerResults();

    appendable.append("\n" + "\n");
  }

  private void printManagementConsumption() throws IOException {
    final IMultipleBillsCalculator multipleBillsCalculator = multipleBillsCalculatorFactory
        .create(managementConsumption, WhatToCount.TOTAL);

    Money totalNet = new Money("0", currency);

    for (final VATClass vatClass : multipleBillsCalculator
        .getAllVatClasses()) {
      totalNet = totalNet.add(multipleBillsCalculator
          .getTotalNetFor(vatClass));
    }

    appendable.append(MessageFormat.format(
        "\nManagement consumption: GROS {0} - NET {1}",
        multipleBillsCalculator.getTotalGross(), totalNet) + "\n");

    for (final VATClass vatClass : multipleBillsCalculator
        .getAllVatClasses()) {
      appendable.append(" -> " + vatClass + " of "
          + multipleBillsCalculator.getTotalGrossFor(vatClass)
          + " (gros) -> "
          + multipleBillsCalculator.getTotalVATFor(vatClass) + "\n");
    }

    appendable.append("\n" + "\n");
  }

  private void printResults() throws IOException {
    for (final User staffMember : consumption.keySet()) {
      appendable.append(staffMember.getFullname() + "\n");

      final ConsumptionCounter consumptionCounter = consumption
          .get(staffMember);

      printoutMeal(breakfastPrice, consumptionCounter.getBreakfast(),
          "Breakfast");
      printoutMeal(lunchPrice, consumptionCounter.getLunch(), "Lunch");
      printoutMeal(dinnerPrice, consumptionCounter.getDinner(), "Dinner");

      Money total = new Money("0", currency);
      total = total.add(breakfastPrice.multiply(consumptionCounter
          .getBreakfast()));
      total = total
          .add(lunchPrice.multiply(consumptionCounter.getLunch()));
      total = total.add(dinnerPrice.multiply(consumptionCounter
          .getDinner()));

      appendable.append("TOTAL: " + total + "\n");
      appendable.append("" + "\n");
    }
  }

  private void printCustomerResults() throws IOException {
    for (final User customer : customerConsumption.keySet()) {
      appendable.append(customer.getFullname() + "\n");

      List<Bill> billsForCustomer = customerConsumption.get(customer);

      IMultipleBillsCalculator calculator = multipleBillsCalculatorFactory.create(billsForCustomer,
          WhatToCount.TOTAL);

      for (VATClass vatClass : calculator.getAllVatClasses()) {
        Money totalGross = calculator.getTotalGrossFor(vatClass);
        appendable.append("\t- Getränke zu " + vatClass.getRate() + "% MwSt.: \t"
            + totalGross.getNet(vatClass) + " netto\t"
            + totalGross + " brutto" + "\n");
      }

      Money totalGross = calculator.getTotalGross();
      appendable.append("\n\t\tZu zahlender Gesamtbetrag:\t" + totalGross + "\n" + "\n");

      appendable.append("\tEnthaltene Mehrwertsteuer(n):" + "\n");
      for (VATClass vatClass : calculator.getAllVatClasses()) {
        Money totalGrossForVAT = calculator.getTotalGrossFor(vatClass);
        appendable.append("\t\t" + vatClass.getRate() + "%:\t" + totalGrossForVAT.getVAT(vatClass) + "\n");
      }

      appendable.append("" + "\n");
    }
  }

  private void countBills(final Collection<Bill> billsForStaff) {
    for (final Bill bill : billsForStaff) {
      if (!bill.isConsumedByStaff()) {
        continue;
      }

      final User staffMember = bill.getStaffConsumer();

      if (staffMember.getName().equals(managementUser)) {
        managementConsumption.add(bill);
      } else {
        if (!consumption.containsKey(staffMember)) {
          consumption.put(staffMember, new ConsumptionCounter(11, 4));
        }

        consumption.get(staffMember).countConsumption(bill);
      }
    }
  }

  private void countCustomerBills(final Collection<Bill> billsForCustomers) {
    for (final Bill bill : billsForCustomers) {
      if (!bill.isConsumedByStaff() || !bill.getStaffConsumer().isCustomer()) {
        continue;
      }

      final User customer = bill.getStaffConsumer();

      if (!customerConsumption.containsKey(customer)) {
        customerConsumption.put(customer, Lists.newArrayList());
      }

      customerConsumption.get(customer).add(bill);
    }
  }

  private void printHeader() throws IOException {

    final Date to = getTo().getTime();
    final Date from = getFrom().getTime();

    appendable.append("Bills for staff - " + getMonth() + "     [from " + DATE_FORMAT.format(from) + " to "
        + DATE_FORMAT.format(to) + "]\n");
    appendable.append("=================================================================" + "\n");
    appendable.append("" + "\n");
    appendable.append("Breakfast: until 11 (costs: " + breakfastPrice
        + ")" + "\n");
    appendable.append("Lunch: 11 - 4 (costs: " + lunchPrice + ")" + "\n");
    appendable.append("Dinner: from 4 (costs: " + dinnerPrice + ")" + "\n");
    appendable.append("" + "\n");
  }

  private void printoutMeal(Money mealPrice, int amount, String label) throws IOException {
    final Money total = mealPrice.multiply(amount);
    appendable.append(" " + label + ": " + amount + " (" + total + ")" + "\n");
  }

  private Collection<Bill> getBillsForStaffLastMonth() throws IOException {
    final Date to = getTo().getTime();
    final Date from = getFrom().getTime();

    final Collection<Bill> billsForStaff = billRepository.getBillsForStaff(
        from, to);
    return billsForStaff;
  }

  private Collection<Bill> getBillsForCustomersLastMonth() {
    final Date to = getTo().getTime();
    final Date from = getFrom().getTime();


    final Collection<Bill> billsForStaff = billRepository.getBillsForCustomers(
        from, to);
    return billsForStaff;
  }

  private Calendar getFrom() {
    final Calendar calendar = Calendar.getInstance();

    if (!thisMonth) {
      calendar.add(Calendar.MONTH, -1);
    }

    return getFirstOfMonth(calendar);
  }

  private Calendar getTo() {
    final Calendar calendar = Calendar.getInstance();

    if (thisMonth) {
      calendar.add(Calendar.MONTH, 1);
    }

    return getFirstOfMonth(calendar);
  }

  private String getMonth() {

    Calendar calendar = getFrom();

    final String monthName = calendar.getDisplayName(Calendar.MONTH,
        Calendar.LONG, Locale.getDefault());
    final int year = calendar.get(Calendar.YEAR);

    return monthName + " " + year;
  }

  private Calendar getFirstOfMonth(final Calendar calendar) {
    final int month = calendar.get(Calendar.MONTH) + 1;
    final int year = calendar.get(Calendar.YEAR);

    return DateFactory.getCalendarWithTimeMidnight(year, month, 1);
  }


  public void setAppendable(Appendable appendable) {
    this.appendable = appendable;
  }


  public void setPrintManagementConsumption(boolean printManagementConsumption) {
    this.printManagementConsumption = printManagementConsumption;
  }


  public void setPrintCustomerBills(boolean printCustomerBills) {
    this.printCustomerBills = printCustomerBills;
  }


  /**
   * 
   * @param thisMonth
   *          true, if the current month should be exported, or false, if the
   *          previous month should be exported.
   */
  public void setThisMonth(boolean thisMonth) {
    this.thisMonth = thisMonth;
  }

}
