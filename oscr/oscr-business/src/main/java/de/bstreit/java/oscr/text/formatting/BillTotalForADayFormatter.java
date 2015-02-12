package de.bstreit.java.oscr.text.formatting;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculator;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculatorFactory;

@Named
public class BillTotalForADayFormatter {

	@Inject
	private IMultipleBillsCalculatorFactory multipleBillsCalculatorFactory;

	@Inject
	private BillFormatter billFormatter;

	public String getBillTotalAsString(String dateLabel,
			Collection<Bill> bills, Collection<Bill> promotionBills) {
		final StringBuilder sb = new StringBuilder();

		addBills(multipleBillsCalculatorFactory.create(bills), dateLabel, sb);

		IMultipleBillsCalculator freePomotionTotal = multipleBillsCalculatorFactory
				.create(promotionBills);

		if (freePomotionTotal.isFilled()) {
			addBills(freePomotionTotal, "promotion expenses for " + dateLabel,
					sb);
		}

		String caption = "All bills for " + dateLabel + ":";
		sb.append("\n\n" + caption + "\n"
				+ StringUtils.repeat("=", caption.length()) + "\n\n");

		bills.stream().forEach(
				bill -> sb.append(billFormatter.formatBill(bill)).append(
						"\n\n\n"));

		return sb.toString();
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

}
