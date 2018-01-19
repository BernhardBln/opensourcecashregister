package de.bstreit.java.oscr.gui.noswing.admin.logic;

import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillService;
import de.bstreit.java.oscr.business.bill.IBillProcessor;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculator;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculatorFactory;
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
import java.util.Calendar;
import java.util.Collection;
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

    try (PrintStream yearPrintStream = new PrintStream(yearExportFile)) {
      yearPrintStream.print(billFormatter.getHeader());

      for (Calendar firstOfMonth : months) {

        String monthAsString = String.valueOf(firstOfMonth.get(Calendar.MONTH) + 1);

        File monthExportFile = new File(yearExportFolder, monthAsString + ".tsv");
        File monthExportFolder = new File(yearExportFolder, monthAsString);

        FileUtils.forceMkdir(monthExportFolder);

        Calendar current = (Calendar) firstOfMonth.clone();

        System.out.println("Exporting " + dateFormat.format(current.getTime()));

        try (PrintStream monthPrintStream = new PrintStream(monthExportFile)) {

          monthPrintStream.print(billFormatter.getHeader());

          while (current.get(Calendar.MONTH) == firstOfMonth.get(Calendar.MONTH)) {

            String dayAsString = String.valueOf(current.get(Calendar.DAY_OF_MONTH));

            File dayExportFile = new File(monthExportFolder, dayAsString + ".tsv");

            try (PrintStream dayPrintStream = new PrintStream(dayExportFile)) {
              dayPrintStream.print(billFormatter.getHeader());

              txService.doInTx(() -> processDay(current, yearPrintStream, monthPrintStream,
                dayPrintStream));

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
                          PrintStream monthPrintStream, PrintStream dayPrintStream) {

    billService.processAllBillsAt(new IBillProcessor() {

      @Override
      public void processBill(Bill bill) {

        String billInOneLine = billFormatter.formatBill(bill);

        yearPrintStream.print(billInOneLine);
        monthPrintStream.print(billInOneLine);
        dayPrintStream.print(billInOneLine);

      }

    }, day.getTime());


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
