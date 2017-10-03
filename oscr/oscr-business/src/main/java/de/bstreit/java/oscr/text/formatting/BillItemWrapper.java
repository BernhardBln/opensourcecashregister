package de.bstreit.java.oscr.text.formatting;

import org.apache.commons.lang3.text.WordUtils;

import static com.google.common.base.Preconditions.checkArgument;

public class BillItemWrapper {

  private final int maxLen;
  private final String newline;

  private String firstLine;
  private String furtherLines;


  public BillItemWrapper(final int maxLen, final String newline) {
    checkArgument(maxLen > 2);
    this.maxLen = maxLen;
    this.newline = newline;
  }

  public void wrapText(final String originalText) {

    if (originalText.length() <= maxLen) {
      firstLine = originalText;
      furtherLines = null;
      return;
    }

    final String wrappedLinesLong = WordUtils.wrap(originalText, maxLen, newline, true);

    final int firstLineBreakIdx = wrappedLinesLong.indexOf(newline);

    firstLine = wrappedLinesLong.substring(0, firstLineBreakIdx);

    final String rest = originalText.substring(firstLineBreakIdx);
    furtherLines = "  " + WordUtils.wrap(rest, maxLen - 2, newline + "  ", true);

  }


  /**
   * @return the {@link #firstLine}
   */
  public String getFirstLine() {
    return firstLine;
  }


  /**
   * @return the {@link #furtherLines}
   */
  public String getFurtherLines() {
    return furtherLines;
  }

  public boolean hasFurtherLines() {
    return furtherLines != null;
  }


}
