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
package de.bstreit.java.oscr.business.storage;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.bstreit.java.oscr.business.AbstractSpringTestWithContext;
import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.products.Product;

public class DataStorageTest extends AbstractSpringTestWithContext {

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
	public void testDataStorage() {

		// -INIT
		final StorageService service = context.getBean(StorageService.class);
		service.saveSomeProductsAndOffers();

		// -RUN
		final List<Product> products = service.getProducts();
		final List<ProductOffer> offers = service.getOffers();

		// -ASSERT
		Assert.assertEquals(3, products.size());
		Assert.assertEquals(3, offers.size());

		Assert.assertTrue(products.contains(Products.ESPRESSO));
		Assert.assertTrue(products.contains(Products.CAPPUCCINO));
		Assert.assertTrue(products.contains(Products.LATTE_MACCHIATO));

		Assert.assertTrue(offers.contains(ProductOffers.ESPRESSO));
		Assert.assertTrue(offers.contains(ProductOffers.CAPPUCCINO));
		Assert.assertTrue(offers.contains(ProductOffers.LATTE_MACCHIATO));

		Assert.assertEquals(
				"<html><center>Espresso<BR>[Cup 100 ml]<BR><BR>1,00 €</center></html>",
				offers.get(0).getLabel());
		Assert.assertEquals(
				"<html><center>Cappuccino<BR>[Cup 200 ml]<BR><BR>1,80 €</center></html>",
				offers.get(1).getLabel());
		Assert.assertEquals(
				"<html><center>Latte Macchiato<BR>[Cup 200 ml]<BR><BR>2,30 €</center></html>",
				offers.get(2).getLabel());
	}

	@Test
	public void testBilling() {

		// -INIT
		final StorageService service = context.getBean(StorageService.class);
		service.saveSomeProductsAndOffers();
		service.saveSomeBills();

		// -RUN
		final Collection<Bill> billsOfToday = service.getBillsOfToday();

		// -ASSERT
		assertEquals(2, billsOfToday.size());
		final Date today = new Date();
		final int day = today.getDate();
		for (final Bill bill : billsOfToday) {
			assertEquals(day, bill.getBillOpened().getDate());
		}
	}

	@Test
	public void testBilling_openBills() {

		// -INIT
		final StorageService service = context.getBean(StorageService.class);
		service.saveSomeProductsAndOffers();
		service.saveSomeBills();

		final Collection<Bill> closedBillsOfToday = service.getBillsOfToday();
		assertEquals(2, closedBillsOfToday.size());

		service.saveSomeOpenBills();

		// -RUN
		Collection<Bill> billsOfToday = service.getBillsOfToday();
		assertEquals(3, billsOfToday.size());

		Collection<Bill> openBillsOfToday = service.getOpenBillsOfToday();
		assertEquals(1, openBillsOfToday.size());

		service.saveSomeBills();

		billsOfToday = service.getBillsOfToday();
		assertEquals(5, billsOfToday.size());

		openBillsOfToday = service.getOpenBillsOfToday();
		assertEquals(1, openBillsOfToday.size());

		// -ASSERT
		// final Date today = new Date();
		// final int day = today.getDate();
		// for (final Bill bill : billsOfToday) {
		// assertEquals(day, bill.getBillOpened().getDate());
		// }
	}

}
