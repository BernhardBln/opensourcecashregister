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
package de.bstreit.java.oscr.business.base.finance.money;

import java.util.Locale;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class MoneyFormatterTest {

  private static final Locale defaultLocale = Locale.getDefault();


  @BeforeClass
  public static void setDefaultLocale() {
    Locale.setDefault(Locale.GERMANY);
    Money.resetNumberFormatter();
  }

  @AfterClass
  public static void restoreLocale() {
    // reset locale for next test
    Locale.setDefault(defaultLocale);
    Money.resetNumberFormatter();
  }

  @Test
  public void formatterTest() {
    final Money m = new Money("1.26", "EUR");

    final String actualString = m.toString();
    final String expectedString = "1,26 €";

    Assert.assertEquals(expectedString, actualString);
  }

  @Test
  public void formatterTestHigherScale() {
    final Money m = new Money("1.269", "EUR");

    final String actualString = m.toString();
    final String expectedString = "1,27 €";

    Assert.assertEquals(expectedString, actualString);
  }

  @Test
  public void formatterTestSmallerScale() {
    final Money m = new Money("1.2", "EUR");

    final String actualString = m.toString();
    final String expectedString = "1,20 €";

    Assert.assertEquals(expectedString, actualString);
  }

  @Test
  public void formatterTestNoScale() {
    final Money m = new Money("1", "EUR");

    final String actualString = m.toString();
    final String expectedString = "1,00 €";

    Assert.assertEquals(expectedString, actualString);
  }
}
