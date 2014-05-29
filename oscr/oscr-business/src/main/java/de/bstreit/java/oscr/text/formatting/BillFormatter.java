package de.bstreit.java.oscr.text.formatting;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.text.StrBuilder;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.base.Strings;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillItem;
import de.bstreit.java.oscr.business.bill.IBillCalculator;
import de.bstreit.java.oscr.business.bill.IBillCalculatorFactory;
import de.bstreit.java.oscr.business.staff.User;
import de.bstreit.java.oscr.business.taxation.TaxInfo;

/**
 * Format a bill for textual representation
 * 
 * @author Bernhard Streit
 */
@Named
public class BillFormatter {

	@Value("#{ systemProperties['line.separator'] }")
	private String NEWLINE;

	private static final int MAX_LINE_LENGTH = 44;

	private static final int MAX_PRODUCT_COLUMN_LENGTH = 20;

	@Inject
	private IBillCalculatorFactory billCalculatorFactory;

	@Inject
	private Locale locale;

	@Inject
	@Named("togoTaxInfo")
	private TaxInfo toGoTaxinfo;

	private BillItemWrapper billItemWrapper;
	private final MoneyFormatter moneyFormatter = new MoneyFormatter();
	private DateFormat dateFormat;

	private transient Bill _bill;
	private transient StrBuilder _builder;
	private transient IBillCalculator billCalculator;

	@PostConstruct
	private void init() {
		dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT,
				DateFormat.SHORT, locale);
		billItemWrapper = new BillItemWrapper(MAX_PRODUCT_COLUMN_LENGTH,
				NEWLINE);
	}

	public String formatBill(Bill bill) {
		if (bill == null) {
			return "";
		}

		this._bill = bill;

		try (IBillCalculator billCalculator = billCalculatorFactory
				.create(bill)) {
			this.billCalculator = billCalculator;

			return getBillAsText();

		} finally {
			this._bill = null;
			this._builder = null;
		}
	}

	private String getBillAsText() {
		_builder = new StrBuilder();

		appendBillHeader();
		appendBillContent();
		appendBillFooter();

		return _builder.toString();
	}

	private void appendBillHeader() {

		Date datum = _bill.getBillClosed();
		if (datum == null) {
			datum = new Date();
		}

		// Is "+" worse or better than creating another stringbuilder?
		_builder.insert(0,
				"Rechnung                    "
						+ dateFormat.format(datum)
						+ NEWLINE //
						+ createHR("=")
						+ NEWLINE //
						+ taxInformationIfApplies()
						+ "                     Mwst.  netto*    brutto"
						+ NEWLINE);
	}

	private String taxInformationIfApplies() {

		final boolean isToGo = _bill.getGlobalTaxInfo().equals(toGoTaxinfo);

		final boolean isStaff = _bill.isConsumedByStaff();
		final User user = _bill.getStaffConsumer();

		final String consumedBy = isStaff ? " consumed by "
				+ user.getFullname() : "";

		if (isToGo) {
			return "  To go" + consumedBy + NEWLINE;
		} else {
			return "  In-house" + consumedBy + NEWLINE;
		}

	}

	private int appendBillContent() {
		int maxLineWidth = 0;

		for (final BillItem billItem : _bill) {

			maxLineWidth = appendProduct(maxLineWidth, billItem);

		}

		return maxLineWidth;
	}

	private int appendProduct(int maxLineWidth, final BillItem billItem) {
		// TODO: add prices of extras and variations!
		final Money priceGross = billItem.getPriceGross();
		final String priceGrossFormatted = moneyFormatter.format(priceGross);

		final Money priceNet = billCalculator.getNetFor(billItem);
		final String priceNetFormatted = moneyFormatter.format(priceNet);

		billItemWrapper.wrapText(getOfferedItemName(billItem));

		final Object[] variables = new Object[] {
				billItemWrapper.getFirstLine(), //
				billCalculator.getVATClassAbbreviationFor(billItem),//
				priceNetFormatted, //
				priceGrossFormatted };

		final String lineFormatted = String.format(
				getProductNameVATPriceFormatString(), variables);
		_builder.append(lineFormatted).append(NEWLINE);

		if (billItemWrapper.hasFurtherLines()) {
			_builder.append(billItemWrapper.getFurtherLines()).append(NEWLINE);
		}

		maxLineWidth = Math.max(lineFormatted.length(), maxLineWidth);
		return maxLineWidth;
	}

	private String getProductNameVATPriceFormatString() {

		final String productNameFormat = "%-" + MAX_PRODUCT_COLUMN_LENGTH + "s";
		final String vatRate = "%s";
		final String netPrice = "%8s";
		final String grossPrice = "%8s";

		return productNameFormat + "   " + vatRate + "  " + netPrice + "  "
				+ grossPrice;
	}

	private String getOfferedItemName(BillItem billItem) {
		return billItem.getName();
	}

	private void appendBillFooter() {
		appendTotal();
		appendVATInfo();
		appendRoundedNetAndVATValuesInfo();
	}

	private void appendTotal() {
		final String totalGross = moneyFormatter.format(billCalculator
				.getTotalGross());

		// TODO check that secondColumnLength + len(Gesamtsumme:) <=
		// maxLineLength!
		final int secondColumnLength = totalGross.length();
		final int firstColumnLength = MAX_LINE_LENGTH - secondColumnLength;

		final String formatString = "%-" + firstColumnLength + "s%"
				+ secondColumnLength + "s" + NEWLINE;

		_builder.append(createHR("-")).append(NEWLINE);
		_builder.append(String.format(formatString, "Gesamtsumme (brutto):",
				totalGross));
		_builder.append(createHR("=")).append(NEWLINE).append(NEWLINE);
	}

	private String createHR(String symbol) {
		return Strings.repeat(symbol, MAX_LINE_LENGTH);
	}

	private void appendVATInfo() {

		for (final Character abbreviation : billCalculator
				.allFoundVATClassesAbbreviated()) {

			final VATClass vatClass = billCalculator
					.getVATClassForAbbreviation(abbreviation);
			final Money totalNetForVATClass = billCalculator
					.getTotalNetFor(vatClass);
			final Money totalVATForVATClass = billCalculator
					.getTotalVATFor(vatClass);
			final Money totalGrossForVATClass = billCalculator
					.getTotalGrossFor(vatClass);

			_builder.append(abbreviation)
					.append(" - ")
					.append(vatClass)
					.append(NEWLINE)
					.append("      netto* ")
					.append(String.format("%8s",
							moneyFormatter.format(totalNetForVATClass)))
					.append(NEWLINE)
					.append("      Mwst.* ")
					.append(String.format("%8s",
							moneyFormatter.format(totalVATForVATClass)))
					.append(NEWLINE)
					.append("      brutto ")
					.append(String.format("%8s",
							moneyFormatter.format(totalGrossForVATClass)))
					.append(NEWLINE).append(NEWLINE);

		}

	}

	private void appendRoundedNetAndVATValuesInfo() {
		_builder.append("* gerundete Beträge").append(NEWLINE);
	}

}
