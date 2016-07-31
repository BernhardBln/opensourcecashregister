package de.bstreit.java.oscr.business.util;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;


public class DateFactoryTest {

  @Test
  public void testGetFirstOfNextMonthAtMidnight_endOfYear() throws Exception {

    // 27th of December 2016, 5:12
    Calendar result = DateFactory.getFirstOfNextMonthAtMidnight(new Date(2016, 12 - 1, 27, 5, 12));

    DateFormat formatter = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.LONG, SimpleDateFormat.LONG);

    assertEquals("1. Januar 2017 00:00:00 MEZ",
        formatter.format(result.getTime()));
  }

  @Test
  public void testGetFirstOfNextMonthAtMidnight() throws Exception {

    // 27th of December 2016, 5:12
    Calendar result = DateFactory.getFirstOfNextMonthAtMidnight(new Date(2016, 5, 1));

    DateFormat formatter = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.LONG, SimpleDateFormat.LONG);

    assertEquals("1. Juli 2016 00:00:00 MESZ",
        formatter.format(result.getTime()));
  }

}
