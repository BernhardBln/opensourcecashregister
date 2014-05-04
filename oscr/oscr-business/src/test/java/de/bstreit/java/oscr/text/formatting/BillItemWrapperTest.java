package de.bstreit.java.oscr.text.formatting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.bstreit.java.oscr.text.formatting.BillItemWrapper;


public class BillItemWrapperTest {

  private static final BillItemWrapper billItemWrapper = new BillItemWrapper(4, "\n");


  @Test
  public void wayTooShort() {
    // INIT
    final String text = "12";
    // RUN
    billItemWrapper.wrapText(text);
    // ASSERT
    assertEquals("12", billItemWrapper.getFirstLine());
    assertNull(billItemWrapper.getFurtherLines());
  }

  @Test
  public void tooShort() {
    // INIT
    final String text = "1234";
    // RUN
    billItemWrapper.wrapText(text);
    // ASSERT
    assertEquals("1234", billItemWrapper.getFirstLine());
    assertNull(billItemWrapper.getFurtherLines());
  }

  @Test
  public void tooLong_oneWord() {
    // INIT
    final String text = "12345";
    // RUN
    billItemWrapper.wrapText(text);
    // ASSERT
    assertEquals("1234", billItemWrapper.getFirstLine());
    assertEquals("  5", billItemWrapper.getFurtherLines());
  }

  @Test
  public void tooLong_severalWords() {
    // INIT
    final String text = "Hey ya what's up?";
    // RUN
    billItemWrapper.wrapText(text);
    // ASSERT
    assertEquals("Hey", billItemWrapper.getFirstLine());
    assertEquals("  ya\n  wh\n  at\n  's\n  up\n  ?", billItemWrapper.getFurtherLines());
  }

  @Test
  public void tooLong_severalWordsShort() {
    // INIT
    final String text = "x xy z zu";
    // RUN
    billItemWrapper.wrapText(text);
    // ASSERT
    assertEquals("x xy", billItemWrapper.getFirstLine());
    assertEquals("  z\n  zu", billItemWrapper.getFurtherLines());
  }
}
