package de.bstreit.java.oscr.gui.noswing.admin.logic;

import com.google.common.base.Preconditions;
import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillItem;
import de.bstreit.java.oscr.business.bill.BillService;
import de.bstreit.java.oscr.business.bill.IBillProcessor;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculator;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculatorFactory;
import de.bstreit.java.oscr.business.bill.calculator.WhatToCount;
import de.bstreit.java.oscr.business.bill.dao.IBillRepository;
import de.bstreit.java.oscr.business.offers.AbstractOffer;
import de.bstreit.java.oscr.business.offers.PromoOffer;
import de.bstreit.java.oscr.business.products.AbstractSalesItem;
import de.bstreit.java.oscr.business.products.dao.ISalesItemRepository;
import de.bstreit.java.oscr.business.util.DateFactory;
import de.bstreit.java.oscr.business.util.TxService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Named
public class TaxOfficeBillPrinter implements IAdminBean {

  @Inject
  private BillService billService;

  @Inject
  private BillCSVFormatter billFormatter;

  @Inject
  private IBillRepository billRepository;

  @Inject
  private ISalesItemRepository salesItemRepository;

  @Inject
  private IMultipleBillsCalculatorFactory multipleBillsCalculatorFactory;

  @Inject
  private TxService txService;


  @Value("${taxexport.outdir}")
  private String taxexportOutputDir;


  @Inject
  private Locale locale;

  private Scanner scanner;

  private DateFormat dateFormat;

  @Inject
  private Currency defaultCurrency;


  @PostConstruct
  public void init() {

    dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT,
      DateFormat.SHORT, locale);

  }


  @Override
  public void performTask() throws Exception {
    System.out.println("Which year [YYYY]? (blank for cancel): ");

    performSanityCheck();

    final String yearAsStr = scanner
      .nextLine()
      .trim();

    if (StringUtils.isBlank(yearAsStr)) {
      return;
    }

    final int yearAsInt = Integer.parseInt(yearAsStr);

    List<Calendar> months = getMonths(yearAsInt);

    File yearExportFolder = new File(taxexportOutputDir, yearAsStr);
    FileUtils.forceMkdir(yearExportFolder);

    File yearExportFile = new File(yearExportFolder, yearAsStr + ".tsv");
    File yearExportFileSingle = new File(yearExportFolder, yearAsStr + "_single.tsv");

    try (PrintStream yearPrintStream = new PrintStream(yearExportFile);
         PrintStream yearPrintStreamSingle = new PrintStream(yearExportFileSingle)) {
      yearPrintStream.print(billFormatter.getHeader());
      yearPrintStreamSingle.print(billFormatter.getHeader());

      for (Calendar firstOfMonth : months) {

        String monthAsString = String.valueOf(firstOfMonth.get(Calendar.MONTH) + 1);

        File monthExportFile = new File(yearExportFolder, monthAsString + ".tsv");
        File monthExportFileSingle = new File(yearExportFolder, monthAsString + "_single.tsv");
        File monthExportFolder = new File(yearExportFolder, monthAsString);

        FileUtils.forceMkdir(monthExportFolder);

        Calendar current = (Calendar) firstOfMonth.clone();

        System.out.println("Exporting " + dateFormat.format(current.getTime()));

        try (PrintStream monthPrintStream = new PrintStream(monthExportFile);
             PrintStream monthPrintStreamSingle = new PrintStream(monthExportFileSingle)) {

          monthPrintStream.print(billFormatter.getHeader());
          monthPrintStreamSingle.print(billFormatter.getHeader());

          while (current.get(Calendar.MONTH) == firstOfMonth.get(Calendar.MONTH)) {

            String dayAsString = String.valueOf(current.get(Calendar.DAY_OF_MONTH));

            File dayExportFile = new File(monthExportFolder, dayAsString + ".tsv");
            File dayExportFileSingle = new File(monthExportFolder, dayAsString + "_single.tsv");

            try (PrintStream dayPrintStream = new PrintStream(dayExportFile);
                 PrintStream dayPrintStreamSingle = new PrintStream(dayExportFileSingle)) {

              dayPrintStream.print(billFormatter.getHeader());
              dayPrintStreamSingle.print(billFormatter.getHeader());

              txService.doInTx(() -> processDay(current, yearPrintStream, yearPrintStreamSingle,
                monthPrintStream, monthPrintStreamSingle,
                dayPrintStream, dayPrintStreamSingle));

              current.add(Calendar.DAY_OF_MONTH, 1);
            }

          }
        }
      }
    }
  }


  private List<Calendar> getMonths(int yearAsInt) {

    List<Calendar> months = newArrayList();

    for (int m = 1; m <= 12; m++) {
      months.add(DateFactory.getCalendarWithTimeMidnight(yearAsInt, m, 1));
    }

    return months;
  }

  private void processDay(Calendar day, PrintStream yearPrintStream,
                          PrintStream yearPrintStreamSingle,
                          PrintStream monthPrintStream, PrintStream monthPrintStreamSingle,
                          PrintStream dayPrintStream,
                          PrintStream dayPrintStreamSingle) {

    billService.processAllBillsAt(new IBillProcessor() {

      @Override
      public void processBill(Bill bill) {

        String billInOneLine = billFormatter.formatBill(bill);

        yearPrintStream.print(billInOneLine);
        monthPrintStream.print(billInOneLine);
        dayPrintStream.print(billInOneLine);

        // individual line items

        List<Bill> bills = splitBillInSingleLineItemBills(bill);

        bills.forEach(
          b -> {
            String splitBillInOneLine = billFormatter.formatBill(b);

            yearPrintStreamSingle.print(splitBillInOneLine);
            monthPrintStreamSingle.print(splitBillInOneLine);
            dayPrintStreamSingle.print(splitBillInOneLine);
          }
        );


      }

    }, day.getTime());


  }

  private List<Bill> splitBillInSingleLineItemBills(Bill bill) {
    List<Bill> bills = newArrayList();

    bill
      .getBillItems()
      .stream()
      .map(billItem -> toIndividualBill(billItem, bill))
      .forEach(bills::add);

    sanityCheck(bill, bills);

    return bills;
  }

  private void sanityCheck(Bill bill, List<Bill> bills) {

    for (WhatToCount w : WhatToCount.values()) {
      sanityCheck(bill, bills, w);
    }

  }

  private void sanityCheck(Bill bill, List<Bill> bills, WhatToCount w) {

    Collection<Bill> billWrapped = new ArrayList<>();
    billWrapped.add(bill);

    IMultipleBillsCalculator calculatorOriginalBill = multipleBillsCalculatorFactory.create
      (billWrapped, w);

    IMultipleBillsCalculator splittedBill = multipleBillsCalculatorFactory.create
      (bills, w);

    Preconditions.checkState(
      calculatorOriginalBill
        .getTotalGross()
        .equals(splittedBill.getTotalGross()),

      "Expected the same total gross on full and on union of all split bills. Bill: " + bill
        .getId() + "; counting: " + w +
        "; original: " + calculatorOriginalBill.getTotalGross() +
        "; union: " + splittedBill.getTotalGross());

    if (calculatorOriginalBill
      .getTotalGross()
      .equals(Money.NULL(defaultCurrency))) {
      // both null, nothing to check
      return;
    }

    Preconditions.checkState(
      calculatorOriginalBill
        .getAllVatClasses()
        .equals(splittedBill.getAllVatClasses()),

      "Expected the same VAT classes on full and on union of all split bills. Bill: " + bill
        .getId() + "; counting: " + w +
        "; original: " + calculatorOriginalBill.getAllVatClasses() +
        "; union: " + splittedBill.getAllVatClasses());

    calculatorOriginalBill
      .getAllVatClasses()
      .forEach(v ->
        Preconditions.checkState(
          calculatorOriginalBill
            .getTotalGrossFor(v)
            .equals(splittedBill.getTotalGrossFor(v)),

          "Expected the same total gross for " + v + " on full and on union of all split bills. " +
            "Bill: " + bill
            .getId() + "; counting: " + w +
            "; original: " + calculatorOriginalBill.getTotalGrossFor(v) +
            "; union: " + splittedBill.getTotalGrossFor(v)));

    calculatorOriginalBill
      .getAllVatClasses()
      .forEach(v ->
        Preconditions.checkState(
          calculatorOriginalBill
            .getTotalNetFor(v)
            .equals(splittedBill.getTotalNetFor(v)),

          "Expected the same total net for " + v + " on full and on union of all split bills. " +
            "Bill: " + bill
            .getId() + "; counting: " + w +
            "; original: " + calculatorOriginalBill.getTotalNetFor(v) +
            "; union: " + splittedBill.getTotalNetFor(v)));


    calculatorOriginalBill
      .getAllVatClasses()
      .forEach(v ->
        Preconditions.checkState(
          calculatorOriginalBill
            .getTotalVATFor(v)
            .equals(splittedBill.getTotalVATFor(v)),

          "Expected the same total vat for " + v + " full and on union of all split bills. Bill: " +
            "" + bill
            .getId() + "; counting: " + w +
            "; original: " + calculatorOriginalBill.getTotalVATFor(v) +
            "; union: " + splittedBill.getTotalVATFor(v)));


  }

  private Bill toIndividualBill(BillItem billItem, Bill bill) {

    Bill initialisedBill = Bill
      .builder()
      .billOpened(bill.getBillOpened())
      .billClosed(bill.getBillClosed())
      .freePromotionOffer(bill.isFreePromotionOffer())
      .globalTaxInfo(bill.getGlobalTaxInfo())
      .cashier(bill.getCashier())
      .internalConsumer(bill.getStaffConsumer())
      .reduction(bill.getReduction())
      // add bill item
      .billItems(newArrayList(billItem))
      .build();

    return initialisedBill;

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
    calendar.add(Calendar.DAY_OF_MONTH, -1);
    return calendar.getTime();
  }

  @Override
  public void setScanner(Scanner scanner) {
    this.scanner = scanner;
  }

  @Override
  public String toString() {
    return "Tax Office Bill export";
  }

  private void performSanityCheck() {
    txService.doInReadOnlyTx(this::verifyNoMixOfAbsoluteAndRelativeDiscount);
    txService.doInReadOnlyTx(this::verifyNoProductWithTabInDescription);
  }


  public void verifyNoMixOfAbsoluteAndRelativeDiscount() {
    List<Bill> all = billRepository.findAll();

    for (Bill bill : all) {
      if (!bill.hasReduction()) {
        // if there is no reduction, it's fine to have promo offers
        continue;
      }

      Collection<AbstractOffer<?>> allItems = bill.getOfferedItemsFlat();

      Optional<AbstractOffer<?>> promoOffer = allItems
        .stream()
        .filter(o -> o instanceof PromoOffer)
        .findFirst();

      if (promoOffer.isPresent()) {
        throw new RuntimeException("Bill " + bill.getId() + " has relative reduction (" +
          bill.getReduction() + "%) and absolute reduction (" + promoOffer.get() +
          ").");
      }

    }


  }

  private void verifyNoProductWithTabInDescription() {
    List<AbstractSalesItem> all = salesItemRepository.findAll();

    String allBroken = all
      .stream()
      .filter(s -> s
        .getName()
        .contains("\t"))
      .map(AbstractSalesItem::getName)
      .collect(Collectors.joining("; "));

    if (org.apache.commons.lang.StringUtils.isNotBlank(allBroken)) {
      System.out.println("Sales items with tab in description found:");
      System.out.println(allBroken);
      System.out.println("");
      System.out.println("Aborting!");
      throw new AbortedException();
    }

  }
}
