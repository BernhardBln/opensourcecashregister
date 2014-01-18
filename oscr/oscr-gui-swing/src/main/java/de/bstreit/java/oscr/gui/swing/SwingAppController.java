package de.bstreit.java.oscr.gui.swing;

import javax.inject.Inject;
import javax.inject.Named;

import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillItem;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.gui.swing.ui.IBillDisplay;

@Named
public class SwingAppController {

  private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SwingAppController.class);


  @Inject
  private IBillDisplay mainWindow;

  private Bill bill = new Bill();


  public void addToBill(ProductOffer offer) {
    bill.addBillItem(new BillItem(offer));
    mainWindow.updateBill(bill);
  }

  public void showMainwindow() {
    mainWindow.show();
  }


}
