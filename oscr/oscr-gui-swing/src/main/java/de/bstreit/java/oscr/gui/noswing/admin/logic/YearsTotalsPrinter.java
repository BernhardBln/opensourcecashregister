package de.bstreit.java.oscr.gui.noswing.admin.logic;

import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillService;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculator;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculatorFactory;
import de.bstreit.java.oscr.business.bill.calculator.WhatToCount;
import de.bstreit.java.oscr.text.formatting.BillFormatter;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Collection;
import java.util.Scanner;

@Named
public class YearsTotalsPrinter implements IAdminBean {

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
    System.out.println("Which year to start with (format YYYY)? ");

    final String yearAsStr = scanner
      .nextLine()
      .trim();

    if (StringUtils.isBlank(yearAsStr)) {
      System.out.println("No year given. Aborting.");
      return;
    }

    final int startYear = Integer.parseInt(yearAsStr);
    final int currentYear = Calendar
      .getInstance()
      .get(Calendar.YEAR);

    for (int year = startYear; year <= currentYear; year++) {
      printTotalOfYear(year);
    }

  }


  private void printTotalOfYear(final int year) {

    System.out.println("Results for year " + year);

    final Collection<Bill> bills = billService.getBillsForYear(year);

    print("Payment (money that was actually spent)",
      multipleBillsCalculatorFactory.create(bills, WhatToCount.PAYMENT));
    print("Promo total", multipleBillsCalculatorFactory.create(bills, WhatToCount.PROMO_TOTAL));
    print("Total (payments + promo)", multipleBillsCalculatorFactory.create(bills, WhatToCount
      .TOTAL));

    System.out.println("\n");
  }

  private void print(final String label, final IMultipleBillsCalculator calc) {
    System.out.println("Summary for " + label);
    System.out.println("  - Total: " + calc.getTotalGross());
    for (final VATClass c : calc.getAllVatClasses()) {
      System.out.println("  - VAT " + c + ": " + calc.getTotalGrossFor(c));
    }

  }


  @Override
  public void setScanner(final Scanner scanner) {
    this.scanner = scanner;
  }

  @Override
  public String toString() {
    return "Print Totals for several years";
  }

}
