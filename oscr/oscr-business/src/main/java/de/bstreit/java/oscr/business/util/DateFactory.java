/*
 * Open Source Cash Register
 * 
 * Copyright (C) 2013, 2014 Bernhard Streit
 * 
 * This file is part of the Open Source Cash Register program.
 * 
 * Open Source Cash Register is free software: you can redistribute it 
 * and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of 
 * the License, or (at your option) any later version.
 * 
 * Open Source Cash Register is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied 
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *  
 * --------------------------------------------------------------------------
 *  
 * See oscr/licenses/gpl-3.txt for a copy of the GNU GPL.
 * See oscr/README.txt for more information about the software and the author(s).
 * 
 */
package de.bstreit.java.oscr.business.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFactory {

  int x = 07;


  /**
   * Creates a date. For example,
   * 
   * <pre>
   * getDateWithTimeMidnight(2009, 1, 9)
   * </pre>
   * 
   * returns a Date with the following date and time fields:
   * 
   * <pre>
   *    2009-01-09 00:00:00.000
   * </pre>
   * 
   * @param year
   *          the year
   * @param month
   *          the month (1 = Jan, ...)
   * @param day
   *          the day (do not write single-digit days with leading zeros!)
   * @return the following Date: year-month-day 00:00:00.000
   */
  public static Date getDateWithTimeMidnight(int year, int month, int day) {
    final Calendar c = Calendar.getInstance();
    c.clear();

    c.set(year, month - 1, day);

    return c.getTime();
  }

  public static Calendar getCalendarWithTimeMidnight(int year, int month, int day) {
    final Calendar c = Calendar.getInstance();
    c.clear();

    c.set(year, month - 1, day);

    return c;
  }

  public static Calendar getFirstOfNextMonthAtMidnight(Date firstOfThisMonth) {

    DateFormat i = SimpleDateFormat.getInstance();

    Calendar firstOfNextMonthCalendar = Calendar.getInstance();
    firstOfNextMonthCalendar.setTime(firstOfThisMonth);
    firstOfNextMonthCalendar.set(Calendar.DAY_OF_MONTH, 1);
    firstOfNextMonthCalendar.add(Calendar.MONTH, 1);
    // Should be next month, at midnight
    Date firstOfNextMonth = firstOfNextMonthCalendar.getTime();
    return getCalendarWithTimeMidnight(firstOfNextMonth.getYear(), firstOfNextMonth.getMonth() + 1,
        firstOfNextMonth.getDate());
  }
}
