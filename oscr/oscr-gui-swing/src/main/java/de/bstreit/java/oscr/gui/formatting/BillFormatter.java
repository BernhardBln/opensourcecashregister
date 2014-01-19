package de.bstreit.java.oscr.gui.formatting;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillItem;

/**
 * Format a bill for textual representation
 * 
 * @author Bernhard Streit
 */
@Named
public class BillFormatter {

  private static final String NEWLINE = System.getProperty("line.separator");

  private transient Bill bill;
  private transient StringBuilder builder;

  private MoneyFormatter moneyFormatter = new MoneyFormatter();


  public String formatBill(Bill bill) {
    if (bill == null) {
      return "";
    }

    this.bill = bill;

    try {

      return getBillAsText();

    } finally {
      this.bill = null;
      this.builder = null;
    }
  }

  private String getBillAsText() {
    builder = new StringBuilder();

    // this comes first, as it determines the maxLineLength
    int maxLineLength = appendBillContent();

    prependBillHeader(maxLineLength);
    appendBillFooter(maxLineLength);

    return builder.toString();
  }

  private void prependBillHeader(int maxLineLength) {
    final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT,
        DateFormat.SHORT);

    Date datum = bill.getBillClosed();
    if (datum == null) {
      datum = new Date();
    }

    builder.append("Rechnung\n")//
        .append(StringUtils.repeat("=", maxLineLength)).append(NEWLINE)//
        .append("Datum: ").append(df.format(datum)).append(NEWLINE
            + NEWLINE);
  }

  private int appendBillContent() {
    final List<BillItem> billItems = bill.getBillItems();
    final int maxProductNameLength = getMaxProductNameLength(billItems);
    int maxLineWidth = 0;

    for (BillItem billItem : billItems) {

      // TODO: add prices of extras and variations!
      final Money price = billItem.getOffer().getPrice();
      final String priceFormatted = moneyFormatter.format(price);

      final String formatString = "%-" + (maxProductNameLength + 5) + "s"
          + "%-8s";
      final Object[] variables = new Object[] { billItem.getOffer().getOfferedItem().getName(),
          priceFormatted };

      final String lineFormatted = String.format(formatString, variables);
      builder.append(lineFormatted).append(NEWLINE);

      maxLineWidth = Math.max(lineFormatted.length(), maxLineWidth);
    }

    builder.append(NEWLINE);

    return maxLineWidth;
  }

  private int getMaxProductNameLength(List<BillItem> billItems) {
    int maxProductnameLength = 0;

    for (BillItem billItem : billItems) {
      final int currentLength = billItem.getOffer().getOfferedItem().getName().length();

      maxProductnameLength = Math.max(currentLength, maxProductnameLength);
    }

    return maxProductnameLength;
  }

  private void appendBillFooter(int maxLineLength) {
    final int secondColumnLength = 5;
    final int firstColumnLength = maxLineLength - secondColumnLength;

    final String formatString = "%-" + firstColumnLength + "s%"
        + secondColumnLength + "s"
        + NEWLINE;
    // builder.append(String.format(formatString, "Gesamtsumme:",
    // bill.getTotalSum()));
  }

}
