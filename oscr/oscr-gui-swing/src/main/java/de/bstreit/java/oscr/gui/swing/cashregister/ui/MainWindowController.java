package de.bstreit.java.oscr.gui.swing.cashregister.ui;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Optional;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillService;
import de.bstreit.java.oscr.business.bill.IBillProcessor;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculator;
import de.bstreit.java.oscr.business.eventbroadcasting.BillChangeListener;
import de.bstreit.java.oscr.business.eventbroadcasting.EventBroadcaster;
import de.bstreit.java.oscr.business.offers.ExtraOffer;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.offers.VariationOffer;
import de.bstreit.java.oscr.business.staff.IUserService;
import de.bstreit.java.oscr.business.staff.User;
import de.bstreit.java.oscr.business.taxation.TaxInfo;
import de.bstreit.java.oscr.business.taxation.dao.ITaxInfoRepository;
import de.bstreit.java.oscr.text.formatting.BillFormatter;

@Named
public class MainWindowController implements BillChangeListener {

  @Inject
  private IBillDisplay billDisplay;

  @Inject
  private BillFormatter billFormatter;

  @Inject
  private BillService billService;

  @Inject
  private ITaxInfoRepository taxInfoRepository;

  @Inject
  private IUserService userService;

  @Inject
  private EventBroadcaster eventBroadcaster;

  private TaxInfo toGoTaxInfo;

  private TaxInfo inHouseTaxInfo;


  @PostConstruct
  private void initController() {
    eventBroadcaster.addBillChangeListener(this);
    toGoTaxInfo = taxInfoRepository
        .findByDenotationAndValidToIsNull("to go");
    inHouseTaxInfo = taxInfoRepository
        .findByDenotationAndValidToIsNull("inhouse");
  }

  public void addToBill(ProductOffer offer) {
    billService.addProductOffer(offer);
  }

  public void setVariationOffer(VariationOffer variationOffer) {
    billService.setVariationOffer(variationOffer);
  }

  public void addExtraOffer(ExtraOffer offer) {
    billService.addExtraOffer(offer);
  }

  public void showMainwindow() {
    billDisplay.show();
  }

  public void closeBill() {
    billService.closeBill();
  }

  public void printTodaysTotal() {
    final StringBuilder sb = new StringBuilder();

    addBills(billService.getTotalForToday(), "today", sb);

    IMultipleBillsCalculator freePomotionTotalForToday = billService.getFreePomotionTotalForToday();
    if (freePomotionTotalForToday.isFilled()) {
      addBills(freePomotionTotalForToday,
          "promotion expenses for today", sb);
    }
    sb.append("\n\nAll bills for today:\n" + "====================\n\n");

    billService.processTodaysBills(new IBillProcessor() {

      @Override
      public void processBill(Bill bill) {
        sb.append(billFormatter.formatBill(bill)).append("\n\n\n");
      }

    });

    sb.append("\n\n").append(StringUtils.repeat("-", 80)).append("\n\n");
    addBills(billService.getTotalForYesterday(), "yesterday", sb);

    IMultipleBillsCalculator freePomotionTotalForYesterday = billService.getFreePomotionTotalForYesterday();
    if (freePomotionTotalForYesterday.isFilled()) {
      addBills(freePomotionTotalForYesterday, "promotion expenses for yesterday", sb);
    }

    billDisplay.printBill(sb.toString());

    billDisplay.scrollToBeginning();
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

  public void setBillToGo(boolean togo) {
    if (togo) {
      billService.setGlobalTaxInfo(toGoTaxInfo);
    } else {
      billService.setGlobalTaxInfo(inHouseTaxInfo);
    }
  }

  public boolean isBillToGo() {
    return toGoTaxInfo.equals(billService.getGlobalTaxInfo());
  }

  public void undoLastAction() {
    billService.undoLastAction();
  }

  /**
   * Notify that the app is supposed to shut down
   */
  public void notifyShutdown() {
    billService.notifyShutdown();
  }

  public void editWeeklyOffers() {
    // TODO Auto-generated method stub
    System.out.println("EDIT WEEKLY OFFERS");
  }

  public void setStaffConsumption() {
    setStaffConsumption(userService.getCurrentUser());
  }

  public void setStaffConsumption(User staffMember) {
    billService.setStaffConsumer(staffMember);
  }

  public void clearStaffConsumption() {
    billService.clearStaffConsumer();
  }

  @Override
  public void billUpdated(Optional<Bill> newBill) {
    if (newBill.isPresent()) {
      billDisplay.printBill(billFormatter.formatBill(newBill.get()));
    } else {
      billDisplay.clear();
    }
  }

  public void setFreePromotion() {
    billService.setFreePromotion();
  }

  public void clearFreePromotion() {
    billService.clearFreePromotion();

  }
}
