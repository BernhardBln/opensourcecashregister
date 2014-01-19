package de.bstreit.java.oscr.gui.swing.ui;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillService;
import de.bstreit.java.oscr.business.bill.IBillChangedListener;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.gui.formatting.BillFormatter;

@Named
public class MainWindowController implements IBillChangedListener {

  @Inject
  private IBillDisplay billDisplay;

  @Inject
  private BillFormatter billFormatter;

  @Inject
  private BillService billService;


  @PostConstruct
  private void initController() {
    billService.addBillChangedListener(this);
  }


  void addToBill(ProductOffer offer) {
    billService.addProductOffer(offer);
  }

  public void showMainwindow() {
    billDisplay.show();
  }

  @Override
  public void billChanged(Bill bill) {
    billDisplay.printBill(billFormatter.formatBill(bill));
  }
}
