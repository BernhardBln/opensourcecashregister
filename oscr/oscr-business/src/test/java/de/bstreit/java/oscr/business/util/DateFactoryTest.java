package de.bstreit.java.oscr.business.util;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;


public class DateFactoryTest {

  private static final DateFormat DATE_FORMAT = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM,
      SimpleDateFormat.MEDIUM, Locale.GERMANY);


  @Test
  public void testGetFirstOfNextMonthAtMidnight_endOfYear() throws Exception {

    final Calendar cal = Calendar.getInstance();
    cal.set(2016, 12 - 1, 27, 5, 12, 0);
    assertDate("27.12.2016 05:12:00", cal);

    Date result = DateFactory.getFirstOfNextMonthAtMidnight(cal.getTime());

    assertDate("01.01.2017 00:00:00", result);
  }


  @Test
  public void testGetFirstOfNextMonthAtMidnight() throws Exception {

    final Calendar cal = Calendar.getInstance();
    cal.set(2016, 6 - 1, 1, 5, 12, 0);
    assertDate("01.06.2016 05:12:00", cal);

    Date result = DateFactory.getFirstOfNextMonthAtMidnight(cal.getTime());

    assertDate("01.07.2016 00:00:00", result);
  }

  @Test
  public void testGetCalendarWithTimeMidnight() throws Exception {
    Calendar c = DateFactory.getCalendarWithTimeMidnight(2016, 8, 31);

    assertEquals(2016, c.get(Calendar.YEAR));
    assertEquals(8 - 1 /* 0-based: August is 7 */, c.get(Calendar.MONTH));
    assertEquals(31, c.get(Calendar.DAY_OF_MONTH));
    assertEquals(0, c.get(Calendar.HOUR));
    assertEquals(0, c.get(Calendar.MINUTE));
    assertEquals(0, c.get(Calendar.SECOND));


    assertDate("31.08.2016 00:00:00", c);
  }

  @Test
  public void testGetDateWithTimeMidnight() {

    Date result = DateFactory.getDateWithTimeMidnight(2016, 12, 15);

    assertDate("15.12.2016 00:00:00", result);
  }

  /**
   * Check if a date is as expected
   * 
   * @param expected
   *          - in the form "31.12.2017 23:59:59", for example, for the last
   *          second before the first of January 2018
   * @param date
   */
  private void assertDate(String expected, Date date) {
    final String actual = DATE_FORMAT.format(date);
    assertEquals(expected, actual);
  }

  private void assertDate(String expected, Calendar cal) {
    assertDate(expected, cal.getTime());
  }

}
