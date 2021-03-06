package de.bstreit.java.oscr.business.export;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import com.google.common.base.Optional;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillService;
import de.bstreit.java.oscr.business.bill.IBillProcessor;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculator;
import de.bstreit.java.oscr.business.eventbroadcasting.BillChangeListener;
import de.bstreit.java.oscr.business.eventbroadcasting.EventBroadcaster;
import de.bstreit.java.oscr.text.formatting.BillFormatter;

@Named
public class HtmlExportService extends AbstractService implements BillChangeListener
{

  private static final Logger logger = LoggerFactory
      .getLogger(HtmlExportService.class);

  private static final long FIVE_MINUTES = 5 * 60 * 1_000;

  @Inject
  private BillService billService;

  @Inject
  private BillFormatter billFormatter;

  @Inject
  private EventBroadcaster eventBroadcaster;

  @Value("classpath:htmlexport_template.htm")
  private Resource htmlTemplateFile;

  @Value("${exportDir}")
  private String exportDir;

  private Boolean dataForExportAvailable = true;


  public HtmlExportService() {
    super(FIVE_MINUTES);
  }

  @Override
  protected void initService() {
    eventBroadcaster.addBillChangeListener(this);
  }

  /**
   * 
   * @return false, if the service cannot be started (e.g. missing
   *         configuration), or true otherwise.
   */
  @Override
  protected boolean canRun() {
    if (StringUtils.isBlank(exportDir)) {
      logger.info("Export dir not set, quitting export service");
      return false;
    }

    return true;
  }

  /**
   * Service gets executed
   */
  @Override
  protected void execute() {
    logger.info("Check if data for export available...");

    synchronized (dataForExportAvailable) {

      if (!dataForExportAvailable) {
        logger.info("Nothing, returning...");
        return;
      }

      dataForExportAvailable = false;

    }

    exportData();
  }

  private void exportData() {

    logger.info("Exporting...");

    String htmlFile = null;
    InputStream inputStream = null;

    try {
      // TODO: besser nur einmal lesen und im Speicher behalten?
      inputStream = htmlTemplateFile.getInputStream();
      htmlFile = IOUtils.toString(inputStream);
      enrichAndExportFile(htmlFile);

    } catch (final IOException e) {
      logger.error("Could not read html template file for export", e);

    } finally {
      IOUtils.closeQuietly(inputStream);
    }

  }

  private void enrichAndExportFile(String htmlFile) {
    final StringBuilder sb = new StringBuilder();

    addBills(billService.getTotalForToday(), "today", sb);
    addBills(billService.getFreePomotionTotalForToday(),
        "promotion expenses for today", sb);

    sb.append("\n\nAll bills for today:\n" + "====================\n\n");

    billService.processTodaysBills(new IBillProcessor() {

      @Override
      public void processBill(Bill bill) {
        sb.append(billFormatter.formatBill(bill)).append("\n\n\n");
      }

    });

    sb.append("\n\n").append(StringUtils.repeat("-", 80)).append("\n\n");
    addBills(billService.getTotalForYesterday(), "yesterday", sb);
    addBills(billService.getFreePomotionTotalForYesterday(),
        "promotion expenses for yesterday", sb);

    final String changedHtmlFile = htmlFile.replace("$CONTENT",
        sb.toString());

    FileOutputStream fileOutputStream = null;

    try {
      fileOutputStream = new FileOutputStream(exportDir + "/export.htm");
      IOUtils.write(changedHtmlFile, fileOutputStream,
          Charset.forName("UTF-8"));

    } catch (final IOException e) {
      logger.error("Could not export html file", e);

    } finally {
      IOUtils.closeQuietly(fileOutputStream);
    }

  }

  /**
   * @param totalForToday
   * @param sb
   */
  private void addBills(final IMultipleBillsCalculator totalForToday,
      String date, final StringBuilder sb) {
    sb.append("Bill for " + date + "\n==============\n\n");

    Money totalNet = null;
    for (final VATClass vatClass : totalForToday.getAllVatClasses()) {
      if (totalNet == null) {
        totalNet = totalForToday.getTotalNetFor(vatClass);
      } else {
        totalNet = totalNet.add(totalForToday.getTotalNetFor(vatClass));
      }
    }

    sb.append("Total (gross): ").append(totalForToday.getTotalGross())
        .append(";\t\t").append("Total (net): ").append(totalNet)
        .append("\n\n");

    sb.append("VAT classes:\n\n");
    for (final VATClass vatClass : totalForToday.getAllVatClasses()) {
      sb.append(vatClass + " \tgross: ")
          .append(totalForToday.getTotalGrossFor(vatClass))
          .append("; vat: ")
          .append(totalForToday.getTotalVATFor(vatClass))
          .append("; net: ")
          .append(totalForToday.getTotalNetFor(vatClass))
          .append("\n");
    }
    sb.append("\n\n");
  }

  @Override
  public void billUpdated(Optional<Bill> newBill) {

    synchronized (dataForExportAvailable) {
      dataForExportAvailable = true;
    }

  }

}
