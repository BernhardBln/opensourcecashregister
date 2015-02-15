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

import org.junit.Assert;
import org.junit.Test;

public class MoneyEqualsHashcodeTest {

	@Test
	public void testEqualsHashcode_equal_noScale() {
		final Money m1 = new Money("1", "EUR");
		final Money m2 = new Money("1.00", "EUR");

		Assert.assertEquals(m1, m2);
		Assert.assertEquals(m1.hashCode(), m2.hashCode());
	}

	@Test
	public void testEqualsHashcode_equal_LowerScale() {
		final Money m1 = new Money("1.0", "EUR");
		final Money m2 = new Money("1.00", "EUR");

		Assert.assertEquals(m1, m2);
		Assert.assertEquals(m1.hashCode(), m2.hashCode());
	}

	@Test
	public void testEqualsHashcode_equal_SameScale() {
		final Money m1 = new Money("1.00", "EUR");
		final Money m2 = new Money("1.00", "EUR");

		Assert.assertEquals(m1, m2);
		Assert.assertEquals(m1.hashCode(), m2.hashCode());
	}

	@Test
	public void testEqualsHashcode_equal_HigherScale() {
		final Money m1 = new Money("1.000", "EUR");
		final Money m2 = new Money("1.00", "EUR");

		Assert.assertEquals(m1, m2);
		Assert.assertEquals(m1.hashCode(), m2.hashCode());
	}

	@Test
	public void testEqualsHashcode_notEqual_value() {
		final Money m1 = new Money("1", "EUR");
		final Money m2 = new Money("1.26", "EUR");

		Assert.assertNotEquals(m1, m2);
		// Theoretically, the following is not equal.
		Assert.assertNotEquals(m1.hashCode(), m2.hashCode());
	}

	@Test
	public void testEqualsHashcode_notEqual_currency() {
		final Money m1 = new Money("1", "EUR");
		final Money m2 = new Money("1", "USD");

		Assert.assertNotEquals(m1, m2);
		// Theoretically, the following is not equal.
		Assert.assertNotEquals(m1.hashCode(), m2.hashCode());
	}

	@Test
	public void testEqualsHashcode_notEqual_valueAndCurrency() {
		final Money m1 = new Money("1", "EUR");
		final Money m2 = new Money("2", "USD");

		Assert.assertNotEquals(m1, m2);
		// Theoretically, the following is not equal.
		Assert.assertNotEquals(m1.hashCode(), m2.hashCode());
	}

}
