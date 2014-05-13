package de.bstreit.java.oscr.gui.swing.cashregister.ui;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Sets;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillService;
import de.bstreit.java.oscr.business.bill.IBillChangedListener;
import de.bstreit.java.oscr.business.bill.IBillProcessor;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculator;
import de.bstreit.java.oscr.business.offers.ExtraOffer;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.offers.VariationOffer;
import de.bstreit.java.oscr.business.taxation.TaxInfo;
import de.bstreit.java.oscr.business.taxation.dao.ITaxInfoRepository;
import de.bstreit.java.oscr.text.formatting.BillFormatter;

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

	private final Collection<IResetListener> resetListeners = Sets.newHashSet();

	@PostConstruct
	private void initController() {
		billService.addBillChangedListener(this);
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
		resetGui();
	}

	@Override
	public void billChanged(Bill bill) {
		billDisplay.printBill(billFormatter.formatBill(bill));
	}

	public void closeBill() {
		billService.closeBill();
		resetGui();
	}

	private void resetGui() {
		for (final IResetListener resetListener : resetListeners) {
			resetListener.resetState();
		}
	}

	public void addGuiResetListener(IResetListener resetListener) {
		resetListeners.add(resetListener);
	}

	public void printTodaysTotal() {
		final StringBuilder sb = new StringBuilder();

		addBills(billService.getTotalForToday(), "today", sb);

		sb.append("\n\nAll bills for today:\n" + "====================\n\n");

		billService.processTodaysBills(new IBillProcessor() {

			@Override
			public void processBill(Bill bill) {
				sb.append(billFormatter.formatBill(bill)).append("\n\n\n");
			}

		});

		sb.append("\n\n").append(StringUtils.repeat("-", 80)).append("\n\n");
		addBills(billService.getTotalForYesterday(), "yesterday", sb);

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

}
