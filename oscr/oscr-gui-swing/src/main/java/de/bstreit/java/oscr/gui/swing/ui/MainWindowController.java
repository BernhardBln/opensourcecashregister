package de.bstreit.java.oscr.gui.swing.ui;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillService;
import de.bstreit.java.oscr.business.bill.IBillChangedListener;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculator;
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
		toGoTaxInfo = taxInfoRepository
				.findByDenotationAndValidToIsNull("to go");
		inHouseTaxInfo = taxInfoRepository
				.findByDenotationAndValidToIsNull("inhouse");
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

	public void printTodaysTotal() {
		final IMultipleBillsCalculator totalForToday = billService
				.getTotalForToday();

		final StringBuilder sb = new StringBuilder();

		sb.append("Bill for today\n==============\n\n");
		sb.append("Total (gross)\t").append(totalForToday.getTotalGross())
				.append("\n");

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

		billDisplay.printBill(sb.toString());
	}

	public void setBillToGo(boolean togo) {
		if (togo) {
			billService.setGlobalTaxInfo(toGoTaxInfo);
		} else {
			billService.setGlobalTaxInfo(inHouseTaxInfo);
		}
	}
}
