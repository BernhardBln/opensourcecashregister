package de.bstreit.java.oscr.gui.swing.cashregister.ui;

import com.google.common.base.Optional;
import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.*;
import de.bstreit.java.oscr.business.eventbroadcasting.BillChangeListener;
import de.bstreit.java.oscr.business.eventbroadcasting.EventBroadcaster;
import de.bstreit.java.oscr.business.offers.ExtraOffer;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.offers.PromoOffer;
import de.bstreit.java.oscr.business.offers.VariationOffer;
import de.bstreit.java.oscr.business.staff.IUserService;
import de.bstreit.java.oscr.business.staff.User;
import de.bstreit.java.oscr.business.taxation.TaxInfo;
import de.bstreit.java.oscr.business.taxation.dao.ITaxInfoRepository;
import de.bstreit.java.oscr.text.formatting.BillFormatter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

@Profile("UI")
@Named
public class MainWindowController implements BillChangeListener {

  private final DateFormat df = SimpleDateFormat.getInstance();

  private String NEWLIN = "\n";

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

  private JFrame openBillsFrame;


  @PostConstruct
  private void initController() {
    eventBroadcaster.addBillChangeListener(this);
    toGoTaxInfo = taxInfoRepository
      .findByDenotationAndValidToIsNull("to go");
    inHouseTaxInfo = taxInfoRepository
      .findByDenotationAndValidToIsNull("inhouse");

    openBillsFrame = new JFrame("Open Bills");
    openBillsFrame
      .getContentPane()
      .setLayout(
        new FlowLayout(FlowLayout.LEFT));
    openBillsFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    openBillsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

  }

  public void guiLaunched() {
    eventBroadcaster.notifyApplicationLaunched(this);
  }

  public void addToBill(final ProductOffer offer) {
    try {
      billService.addProductOffer(offer);

    } catch (final CannotAddItemException e) {
      billDisplay.showError(e.getMessage());
    }
  }

  public void setVariationOffer(final VariationOffer variationOffer) {
    billService.setVariationOffer(variationOffer);
  }

  public void setPromoOffer(final PromoOffer offer) {
    billService.setPromoOffer(offer);
  }

  public void addExtraOffer(final ExtraOffer offer) {
    billService.addExtraOffer(offer);
  }

  public void showMainwindow() {
    billDisplay.show();
  }

  public void closeBill() {
    billService.closeBill();
  }

  public void printTodaysTotal() {
    // in order to avoid confusion, remove last bill as current (it will still be shown as open)
    // so when after printing the total, somebody pushes a product button, it appears in a new bill
    billService.newBill();


    final StringBuilder sb = new StringBuilder();

    addBills(billService.getTotalForToday(), "today", sb);

    final IMultipleBillsCalculator freePomotionTotalForToday = billService
      .getFreePomotionTotalForToday();
    if (freePomotionTotalForToday.isFilled()) {
      addBills(freePomotionTotalForToday, "promotion expenses for today",
        sb);
    }
    sb.append("\n\nAll bills for today:\n" + "====================\n\n");

    billService.processTodaysBills(bill -> sb
      .append(
        billFormatter.formatBill(bill))
      .append("\n\n\n"));

    sb
      .append("\n\n")
      .append(StringUtils.repeat("-", 80))
      .append("\n\n");
    addBills(billService.getTotalForYesterday(), "yesterday", sb);

    final IMultipleBillsCalculator freePomotionTotalForYesterday = billService
      .getFreePomotionTotalForYesterday();
    if (freePomotionTotalForYesterday.isFilled()) {
      addBills(freePomotionTotalForYesterday,
        "promotion expenses for yesterday", sb);
    }

    billDisplay.printBill(sb.toString());

    billDisplay.scrollToBeginning();
  }

  /**
   * @param totalForToday
   * @param sb
   */
  private void addBills(final IMultipleBillsCalculator totalForToday,
                        final String date, final StringBuilder sb) {
    sb.append("Bill for " + date + "\n==============\n\n");

    Money totalNet = null;
    for (final VATClass vatClass : totalForToday.getAllVatClasses()) {
      if (totalNet == null) {
        totalNet = totalForToday.getTotalNetFor(vatClass);
      } else {
        totalNet = totalNet.add(totalForToday.getTotalNetFor(vatClass));
      }
    }

    sb
      .append("Total (gross): ")
      .append(totalForToday.getTotalGross())
      .append(";\t\t")
      .append("Total (net): ")
      .append(totalNet)
      .append("\n\n");

    sb.append("VAT classes:\n\n");
    for (final VATClass vatClass : totalForToday.getAllVatClasses()) {
      sb
        .append(vatClass + " \tgross: ")
        .append(totalForToday.getTotalGrossFor(vatClass))
        .append("; vat: ")
        .append(totalForToday.getTotalVATFor(vatClass))
        .append("; net: ")
        .append(totalForToday.getTotalNetFor(vatClass))
        .append("\n");
    }
    sb.append("\n\n");
  }

  public void setBillToGo(final boolean togo) {
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
    openBillsFrame.dispose();
  }

  public void editWeeklyOffers() {
    // TODO Auto-generated method stub
    System.out.println("EDIT WEEKLY OFFERS");
  }

  public void setStaffConsumption() {
    setStaffConsumption(userService.getCurrentUser());
  }

  public void setStaffConsumption(final User staffMember) {
    billService.setStaffConsumer(staffMember);
  }

  public void clearStaffConsumption() {
    billService.clearStaffConsumer();
  }

  @Override
  public void billUpdated(final Optional<Bill> newBill) {
    if (newBill.isPresent()) {
      billDisplay.printBill(billFormatter.formatBill(newBill.get()));
    } else {
      billDisplay.clear();
    }

    if (billService.hasWarnings()) {
      billDisplay.showWarnings(billService.warnings());
    }

  }

  public void setFreePromotion() {
    billService.setFreePromotion();
  }

  public void clearFreePromotion() {
    billService.clearFreePromotion();

  }

  public void toggleReduction() {
    billService.toggleReduction();
  }

  public void showOpenBills() {
    final Collection<Bill> openBills = billService.getOpenBills();

    final Container contentPane = openBillsFrame.getContentPane();
    contentPane.removeAll();

    for (final Bill bill : openBills) {
      contentPane.add(new JButton(createBillButtonAction(bill)));
    }

    openBillsFrame.setVisible(true);
    openBillsFrame.pack();

  }

  private Action createBillButtonAction(final Bill bill) {
    final StringBuilder sb = new StringBuilder();

    sb.append("<html><body>");
    sb
      .append("<b>Bill opened ")
      .append(df.format(bill.getBillOpened()))
      .append("</b><BR><br>");

    for (final BillItem bi : bill) {
      sb
        .append(bi.getName())
        .append("<BR>");
    }

    sb.append("</body></html>");

    final String label = sb.toString();

    return new AbstractAction(label) {

      @Override
      public void actionPerformed(final ActionEvent e) {
        billService.loadBill(bill);
        openBillsFrame.setVisible(false);
      }
    };
  }

  public void newBill() {
    billService.newBill();
  }

  public int getNumberOfOpenBills() {
    return billService
      .getOpenBills()
      .size();
  }

  public boolean hasOpenBills() {
    return !billService
      .getOpenBills()
      .isEmpty();
  }

}
