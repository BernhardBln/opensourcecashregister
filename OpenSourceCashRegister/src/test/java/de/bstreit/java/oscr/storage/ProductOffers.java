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

import java.util.Date;

import de.bstreit.java.oscr.base.finance.money.Money;
import de.bstreit.java.oscr.offers.ProductOffer;
import de.bstreit.java.oscr.productconfiguration.products.Product;

/**
 * @author streit
 * 
 */
public class ProductOffers {

	public static final ProductOffer ESPRESSO = addProductOffer(Products.ESPRESSO, new Money("1.00", "EUR"));

	public static final ProductOffer CAPPUCCINO = addProductOffer(Products.CAPPUCCINO, new Money("1.80",
			"EUR"));

	public static final ProductOffer LATTE_MACCHIATO = addProductOffer(Products.LATTE_MACCHIATO, new Money(
			"2.30", "EUR"));


	private static ProductOffer addProductOffer(Product product, Money price) {
		return new ProductOffer(product, price, new Date(), null);
	}

}
