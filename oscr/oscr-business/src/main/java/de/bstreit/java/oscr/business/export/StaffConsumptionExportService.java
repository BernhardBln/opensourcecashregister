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

import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.eventbroadcasting.BillChangeListener;
import de.bstreit.java.oscr.business.eventbroadcasting.EventBroadcaster;
import de.bstreit.java.oscr.business.export.consumption.ConsumptionExporter;

@Named
public class StaffConsumptionExportService extends AbstractService implements BillChangeListener
{

  private static final Logger logger = LoggerFactory
      .getLogger(StaffConsumptionExportService.class);

  private static final long FIVE_MINUTES = 5/* * 60 */* 1_000;

  @Inject
  private EventBroadcaster eventBroadcaster;

  @Value("classpath:htmlstaffconsumptionexport_template.htm")
  private Resource htmlTemplateFile;

  @Value("${exportDir}")
  private String exportDir;

  private Boolean dataForExportAvailable = true;

  @Inject
  private ConsumptionExporter consumptionExporter;


  public StaffConsumptionExportService() {
    super(FIVE_MINUTES);
  }


  @Override
  protected void initService() {
    eventBroadcaster.addBillChangeListener(this);
    consumptionExporter.setPrintCustomerBills(false);
    consumptionExporter.setPrintManagementConsumption(false);
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

      // mark here already as exported, to prevent double invocation if export
      // takes too much time
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

    StringBuilder sb = new StringBuilder();

    consumptionExporter.setThisMonth(true);
    consumptionExporter.setAppendable(sb);
    consumptionExporter.export();

    sb.append("\n\n=================================================================\n\n\n\n");

    consumptionExporter.setThisMonth(false);
    consumptionExporter.setAppendable(sb);
    consumptionExporter.export();

    final String changedHtmlFile = htmlFile.replace("$CONTENT",
        sb.toString());

    FileOutputStream fileOutputStream = null;

    try {
      fileOutputStream = new FileOutputStream(exportDir + "/export_staff.htm");
      IOUtils.write(changedHtmlFile, fileOutputStream,
          Charset.forName("UTF-8"));

    } catch (final IOException e) {
      logger.error("Could not export html file", e);

    } finally {
      IOUtils.closeQuietly(fileOutputStream);
    }
  }


  @Override
  public void billUpdated(Optional<Bill> newBill) {

    if (newBill.isPresent() && newBill.get().isConsumedByStaff()) {
      synchronized (dataForExportAvailable) {
        dataForExportAvailable = true;
      }
    }

  }

}
