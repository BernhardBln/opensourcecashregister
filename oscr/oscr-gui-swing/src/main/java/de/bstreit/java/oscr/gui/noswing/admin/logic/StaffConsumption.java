package de.bstreit.java.oscr.gui.noswing.admin.logic;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;

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

@Named
public class StaffConsumption implements IAdminBean {

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


  @PostConstruct
  public void init() {
    breakfastPrice = new Money(breakfastPriceStr, currency);
    lunchPrice = new Money(lunchPriceStr, currency);
    dinnerPrice = new Money(dinnerPriceStr, currency);
  }

  @Transactional
  @Override
  public void performTask() {
    try {

      final Collection<Bill> billsForStaff = getBillsForStaffLastMonth();

      printHeader();

      countBills(billsForStaff);
      printResults();

      printCustomerBills();

      printManagementConsumption();

    } finally {
      consumption.clear();
      managementConsumption.clear();
      customerConsumption.clear();
    }
  }

  private void printCustomerBills() {

    System.out.println("Customer Consumption (for billing):\n"
        + "===================================\n");

    countCustomerBills(getBillsForCustomersLastMonth());

    printCustomerResults();

    System.out.println("\n");
  }

  private void printManagementConsumption() {
    final IMultipleBillsCalculator multipleBillsCalculator = multipleBillsCalculatorFactory
        .create(managementConsumption, WhatToCount.TOTAL);

    Money totalNet = new Money("0", currency);

    for (final VATClass vatClass : multipleBillsCalculator
        .getAllVatClasses()) {
      totalNet = totalNet.add(multipleBillsCalculator
          .getTotalNetFor(vatClass));
    }

    System.out.println(MessageFormat.format(
        "\nManagement consumption: GROS {0} - NET {1}",
        multipleBillsCalculator.getTotalGross(), totalNet));

    for (final VATClass vatClass : multipleBillsCalculator
        .getAllVatClasses()) {
      System.out.println(" -> " + vatClass + " of "
          + multipleBillsCalculator.getTotalGrossFor(vatClass)
          + " (gros) -> "
          + multipleBillsCalculator.getTotalVATFor(vatClass));
    }

    System.out.println("\n");
  }

  private void printResults() {
    for (final User staffMember : consumption.keySet()) {
      System.out.println(staffMember.getFullname());

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

      System.out.println("TOTAL: " + total);
      System.out.println("");
    }
  }

  private void printCustomerResults() {
    for (final User customer : customerConsumption.keySet()) {
      System.out.println(customer.getFullname());

      List<Bill> billsForCustomer = customerConsumption.get(customer);

      IMultipleBillsCalculator calculator = multipleBillsCalculatorFactory.create(billsForCustomer,
          WhatToCount.TOTAL);

      for (VATClass vatClass : calculator.getAllVatClasses()) {
        Money totalGross = calculator.getTotalGrossFor(vatClass);
        System.out.println("\t- Getränke zu " + vatClass.getRate() + "% MwSt.: \t"
            + totalGross.getNet(vatClass) + " netto\t"
            + totalGross + " brutto");
      }

      Money totalGross = calculator.getTotalGross();
      System.out.println("\n\t\tZu zahlender Gesamtbetrag:\t" + totalGross + "\n");

      System.out.println("\tEnthaltene Mehrwertsteuer(n):");
      for (VATClass vatClass : calculator.getAllVatClasses()) {
        Money totalGrossForVAT = calculator.getTotalGrossFor(vatClass);
        System.out.println("\t\t" + vatClass.getRate() + "%:\t" + totalGrossForVAT.getVAT(vatClass));
      }

      System.out.println("");
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

  private void printHeader() {
    System.out.println("Bills for staff - " + getMonth());
    System.out.println("===============");
    System.out.println("");
    System.out.println("Breakfast: until 11 (costs: " + breakfastPrice
        + ")");
    System.out.println("Lunch: 11 - 4 (costs: " + lunchPrice + ")");
    System.out.println("Dinner: from 4 (costs: " + dinnerPrice + ")");
    System.out.println("");
  }

  private void printoutMeal(Money mealPrice, int amount, String label) {
    final Money total = mealPrice.multiply(amount);
    System.out.println(" " + label + ": " + amount + " (" + total + ")");
  }

  private Collection<Bill> getBillsForStaffLastMonth() {
    final Date to = getTo().getTime();
    final Date from = getFrom().getTime();

    System.out.println("From " + from + " to " + to);

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

    // REVERT!
    // calendar.add(Calendar.MONTH, -1);

    return getFirstOfMonth(calendar);
  }

  private Calendar getTo() {
    final Calendar calendar = Calendar.getInstance();

    // REVERT!
    calendar.add(Calendar.MONTH, 1);

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

  @Override
  public void setScanner(Scanner scanner) {

  }

  @Override
  public String toString() {
    return "Staff Consumption for previous month";
  }
}
