/*
 * Open Source Cash Register
 * 
 * Copyright (C) 2013 Bernhard Streit
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
 * See /licenses/gpl-3.txt for a copy of the GNU GPL.
 * See /README.txt for more information about the software and the author(s).
 * 
 */
package de.bstreit.java.oscr.storage;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.bstreit.java.oscr.AbstractSpringTestWithContext;
import de.bstreit.java.oscr.offers.ProductOffer;
import de.bstreit.java.oscr.productconfiguration.products.Product;

public class KasseDataStorageTest extends AbstractSpringTestWithContext {

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

		Assert.assertEquals("Espresso<BR>Cup 100 ml<BR>1,00 €", offers.get(0).getLabel());
		Assert.assertEquals("Cappuccino<BR>Cup 200 ml<BR>1,80 €", offers.get(1).getLabel());
		Assert.assertEquals("Latte Macchiato<BR>Cup 200 ml<BR>2,30 €", offers.get(2).getLabel());
	}


}
