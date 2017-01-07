package de.bstreit.java.oscr.gui.noswing.admin.logic;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;

import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillService;
import de.bstreit.java.oscr.business.bill.IBillProcessor;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculator;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculatorFactory;
import de.bstreit.java.oscr.business.bill.calculator.WhatToCount;
import de.bstreit.java.oscr.text.formatting.BillFormatter;

@Named
public class BillPrinter implements IAdminBean {

  @Inject
  private BillService billService;

  @Inject
  private BillFormatter billFormatter;

  @Inject
  private IMultipleBillsCalculatorFactory multipleBillsCalculatorFactory;


  @Inject
  private Locale locale;

  private Scanner scanner;


  @Override
  @Transactional
  public void performTask() {
    System.out.println("Which day [DD.MM.YYYY]? (blank for yesterday): ");
    final String dateAsStr = scanner.nextLine().trim();

    final Date day;

    if (StringUtils.isBlank(dateAsStr)) {
      day = getYesterday();
    } else {

      try {
        day = dateFormat().parse(dateAsStr);
      } catch (ParseException e) {
        e.printStackTrace();
        return;
      }

    }

    billService.processBillsAt(new IBillProcessor() {

      @Override
      public void processBill(Bill bill) {
        System.out.println(billFormatter.formatBill(bill));
        System.out.println("\n\n");
      }

    }, day);

    Collection<Bill> bills = billService.getBillsForAllDay(day);

    print("Payment (money that was actually spent)",
        multipleBillsCalculatorFactory.create(bills, WhatToCount.PAYMENT));
    print("Promo total", multipleBillsCalculatorFactory.create(bills, WhatToCount.PROMO_TOTAL));
    print("Total (payments + promo)", multipleBillsCalculatorFactory.create(bills, WhatToCount.TOTAL));
  }

  private DateFormat dateFormat() {
    return DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
  }

  private void print(String label, IMultipleBillsCalculator calc) {
    System.out.println("Summary for " + label);
    System.out.println("  - Total: " + calc.getTotalGross());
    for (VATClass c : calc.getAllVatClasses()) {
      System.out.println("  - VAT " + c + ": " + calc.getTotalGrossFor(c));
    }

  }

  private Date getYesterday() {
    Calendar calendar = Calendar.getInstance();
    calendar.roll(Calendar.DAY_OF_MONTH, false);
    return calendar.getTime();
  }

  @Override
  public void setScanner(Scanner scanner) {
    this.scanner = scanner;
  }

  @Override
  public String toString() {
    return "Print bills";
  }

}
