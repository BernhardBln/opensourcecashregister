package de.bstreit.java.oscr.gui.noswing.admin.logic;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Scanner;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;

import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillService;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculator;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculatorFactory;
import de.bstreit.java.oscr.business.bill.calculator.WhatToCount;
import de.bstreit.java.oscr.business.util.DateFactory;
import de.bstreit.java.oscr.text.formatting.BillFormatter;

@Named
public class TotalsPrinter implements IAdminBean {

  @Inject
  private BillService billService;

  @Inject
  private BillFormatter billFormatter;

  @Inject
  private IMultipleBillsCalculatorFactory multipleBillsCalculatorFactory;

  private Scanner scanner;


  @Override
  @Transactional
  public void performTask() {
    System.out.println("Which month (format MM.YYYY)? (blank for current Month): ");
    String monthAsStr = scanner.nextLine().trim();

    if (StringUtils.isBlank(monthAsStr)) {
      Calendar c = Calendar.getInstance();
      monthAsStr = (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR);
    }

    Date firstOfMonth;
    try {
      firstOfMonth = DateFormat.getInstance().parse("01." + monthAsStr + " 00:00");
    } catch (ParseException e1) {
      e1.printStackTrace();
      return;
    }

    Calendar lastOfMonth = DateFactory.getFirstOfNextMonthAtMidnight(firstOfMonth);
    lastOfMonth.add(Calendar.DAY_OF_MONTH, -1);

    for (int i = 1; i <= lastOfMonth.get(Calendar.DAY_OF_MONTH); i++) {
      final String dateAsStr = i + "." + monthAsStr;

      final Date day;

      try {
        day = DateFormat.getInstance().parse(dateAsStr + " 00:00");
      } catch (ParseException e) {
        e.printStackTrace();
        return;
      }

      printForOneDay(dateAsStr, day);
      System.out.println("");
    }
  }

  private void printForOneDay(String dateAsStr, final Date day) {


    System.out.println(dateAsStr);

    Collection<Bill> bills = billService.getBillsForAllDay(day);

    print("Payment (money that was actually spent)",
        multipleBillsCalculatorFactory.create(bills, WhatToCount.PAYMENT));
    print("Promo total", multipleBillsCalculatorFactory.create(bills, WhatToCount.PROMO_TOTAL));
    print("Total (payments + promo)", multipleBillsCalculatorFactory.create(bills, WhatToCount.TOTAL));
  }

  private void print(String label, IMultipleBillsCalculator calc) {
    System.out.println("Summary for " + label);
    System.out.println("  - Total: " + calc.getTotalGross());
    for (VATClass c : calc.getAllVatClasses()) {
      System.out.println("  - VAT " + c + ": " + calc.getTotalGrossFor(c));
    }

  }


  @Override
  public void setScanner(Scanner scanner) {
    this.scanner = scanner;
  }

  @Override
  public String toString() {
    return "Print Totals for a Month";
  }

}
