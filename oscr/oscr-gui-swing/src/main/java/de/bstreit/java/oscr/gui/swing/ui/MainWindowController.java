package de.bstreit.java.oscr.gui.swing.ui;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillService;
import de.bstreit.java.oscr.business.bill.IBillChangedListener;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.taxation.TaxInfo;
import de.bstreit.java.oscr.business.taxation.dao.ITaxInfoRepository;
import de.bstreit.java.oscr.gui.formatting.BillFormatter;

@Named
public class MainWindowController implements IBillChangedListener {

  @Inject
  private IBillDisplay billDisplay;

  @Inject
  private BillFormatter billFormatter;

  @Inject
  private BillService billService;

  @Inject
  private ITaxInfoRepository taxInfoRepository;

  private TaxInfo toGoTaxInfo;

  private TaxInfo inHouseTaxInfo;


  @PostConstruct
  private void initController() {
    billService.addBillChangedListener(this);
    toGoTaxInfo = taxInfoRepository.findByDenotationAndValidToIsNull("to go");
    inHouseTaxInfo = taxInfoRepository.findByDenotationAndValidToIsNull("inhouse");
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


  public void closeBill() {
    billService.closeBill();
    billDisplay.resetGui();
  }


  public void setBillToGo(boolean togo) {
    if (togo) {
      billService.setGlobalTaxInfo(toGoTaxInfo);
    } else {
      billService.setGlobalTaxInfo(inHouseTaxInfo);
    }
  }
}
