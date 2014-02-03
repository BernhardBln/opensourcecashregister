package de.bstreit.java.oscr.gui.formatting;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.base.Strings;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.base.finance.tax.dao.IVATClassRepository;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillCalculator;
import de.bstreit.java.oscr.business.bill.BillItem;

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
  private BillCalculator billCalculator;

  @Inject
  private IVATClassRepository vatClassRepository;

  @Inject
  private Locale locale;

  private BillItemWrapper billItemWrapper;
  private MoneyFormatter moneyFormatter = new MoneyFormatter();
  private NumberFormat vatRateFormatter;
  private DateFormat dateFormat;

  private transient Bill _bill;
  private transient StringBuilder _builder;


  @PostConstruct
  private void init() {
    vatRateFormatter = NumberFormat.getNumberInstance(locale);
    dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT, locale);
    billItemWrapper = new BillItemWrapper(MAX_PRODUCT_COLUMN_LENGTH, NEWLINE);
  }

  public String formatBill(Bill bill) {
    if (bill == null) {
      return "";
    }

    this._bill = bill;

    try {
      return getBillAsText();

    } finally {
      this._bill = null;
      this._builder = null;
      billCalculator.freeResults();
    }
  }

  private String getBillAsText() {
    _builder = new StringBuilder();

    billCalculator.analyse(_bill);

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
    _builder.insert(0, "Rechnung                    " + dateFormat.format(datum) + NEWLINE //
        + StringUtils.repeat("=", MAX_LINE_LENGTH) + NEWLINE //
        + "                     Mwst.  netto     brutto" + NEWLINE);
  }

  private int appendBillContent() {
    int maxLineWidth = 0;

    for (BillItem billItem : _bill) {

      // TODO: add prices of extras and variations!
      final Money priceGross = billItem.getOffer().getPriceGross();
      final String priceGrossFormatted = moneyFormatter.format(priceGross);

      final Money priceNet = billCalculator.getNetFor(billItem);
      final String priceNetFormatted = moneyFormatter.format(priceNet);

      billItemWrapper.wrapText(getOfferedItemName(billItem));

      final Object[] variables = new Object[] {
          billItemWrapper.getFirstLine(), //
          billCalculator.getVATClassAbbreviationFor(billItem),//
          priceNetFormatted, //
          priceGrossFormatted
      };

      final String lineFormatted = String.format(getProductNameVATPriceFormatString(), variables);
      _builder.append(lineFormatted).append(NEWLINE);

      if (billItemWrapper.hasFurtherLines()) {
        _builder.append(billItemWrapper.getFurtherLines()).append(NEWLINE);
      }

      maxLineWidth = Math.max(lineFormatted.length(), maxLineWidth);
    }

    return maxLineWidth;
  }


  private String getProductNameVATPriceFormatString() {

    final String productNameFormat = "%-" + MAX_PRODUCT_COLUMN_LENGTH + "s";
    final String vatRate = "%s";
    final String netPrice = "%8s";
    final String grossPrice = "%8s";

    return productNameFormat + "   " + vatRate + "  " + netPrice + "  " + grossPrice;
  }


  private String getOfferedItemName(BillItem billItem) {
    return billItem.getOffer().getOfferedItem().getName();
  }


  // private String getVATRateFormatted(BillItem billItem) {
  // final BigDecimal vatRate =
  // billCalculator.getVATClassFor(billItem).getRate();
  // final String vatRateFormatted = vatRateFormatter.format(vatRate);
  // return vatRateFormatted;
  // }


  private void appendBillFooter() {
    final String totalGross = moneyFormatter.format(billCalculator.getTotalGross());

    // TODO check that secondColumnLength + len(Gesamtsumme:) <= maxLineLength!
    final int secondColumnLength = totalGross.length();
    final int firstColumnLength = MAX_LINE_LENGTH - secondColumnLength;

    final String formatString = "%-" + firstColumnLength + "s%"
        + secondColumnLength + "s"
        + NEWLINE;

    _builder.append(Strings.repeat("-", MAX_LINE_LENGTH)).append(NEWLINE);
    _builder.append(String.format(formatString, "Gesamtsumme (brutto):", totalGross));
    _builder.append(Strings.repeat("=", MAX_LINE_LENGTH)).append(NEWLINE).append(NEWLINE);
    /*
     * A - Normaler Steuersatz (19%) netto 10,87 € Mwst. 2,06 € brutto 12,93 €
     */

    for (VATClass vatClass : billCalculator.allFoundVATClasses()) {
      final Money totalNetForVATClass = billCalculator.getTotalNetFor(vatClass);
      final Money totalGrossForVATClass = billCalculator.getTotalGrossFor(vatClass);
      final String abbreviation = billCalculator.getAbbreviationFor(vatClass);


      _builder
          .append(abbreviation)
          .append(" - ")
          .append(vatClass.getName())
          .append(" (")
          .append(vatClass.getRate())
          .append("%)")
          .append(NEWLINE)
          .append("   netto  ")
          .append(String.format("%8s", moneyFormatter.format(totalNetForVATClass)))
          .append(NEWLINE)
          .append("   Mwst.  ")
          .append(
              String.format("%8s", moneyFormatter.format(totalGrossForVATClass.subtract(totalNetForVATClass))))
          .append(NEWLINE)
          .append("   brutto ").append(String.format("%8s", moneyFormatter.format(totalGrossForVATClass)))
          .append(NEWLINE);
    }

  }
}
