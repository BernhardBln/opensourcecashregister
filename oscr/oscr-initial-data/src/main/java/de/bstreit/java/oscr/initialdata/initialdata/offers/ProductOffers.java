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
package de.bstreit.java.oscr.initialdata.initialdata.offers;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.initialdata.initialdata.ValidityDates;
import de.bstreit.java.oscr.initialdata.initialdata.products.Products;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.products.Product;

/**
 * @author streit
 * 
 */
public class ProductOffers {

	public static final ProductOffer ESPRESSO = addProductOffer(Products.ESPRESSO, new Money("1.10", "EUR"));
	public static final ProductOffer DOUBLE_ESPRESSO = addProductOffer(Products.DOUBLE_ESPRESSO, new Money(
			"1.40", "EUR"));

	public static final ProductOffer AMERICANO = addProductOffer(Products.AMERICANO, new Money("1.40", "EUR"));
	public static final ProductOffer DOUBLE_AMERICANO = addProductOffer(Products.DOUBLE_AMERICANO, new Money(
			"1.70", "EUR"));

	public static final ProductOffer SINGLE_ESPRESSO_MACCHIATO = addProductOffer(
			Products.SINGLE_ESPRESSO_MACCHIATO, new Money("1.40", "EUR"));
	public static final ProductOffer DOUBLE_ESPRESSO_MACCHIATO = addProductOffer(
			Products.DOUBLE_ESPRESSO_MACCHIATO, new Money("2.00", "EUR"));

	public static final ProductOffer CAPPUCCINO = addProductOffer(Products.CAPPUCCINO, new Money("1.80",
			"EUR"));
	public static final ProductOffer DOUBLE_CAPPUCCINO = addProductOffer(Products.DOUBLE_CAPPUCCINO,
			new Money("2.30", "EUR"));

	public static final ProductOffer LATTE_MACCHIATO = addProductOffer(Products.LATTE_MACCHIATO, new Money(
			"2.30", "EUR"));
	public static final ProductOffer DOUBLE_LATTE_MACCHIATO = addProductOffer(
			Products.DOUBLE_LATTE_MACCHIATO, new Money("2.80", "EUR"));

	public static final ProductOffer KAKAO = addProductOffer(Products.KAKAO, new Money("2", "EUR"));
	public static final ProductOffer SINGLE_SCHOKO_CAPPUCCINO = addProductOffer(
			Products.SINGLE_SCHOKO_CAPPUCCINO, new Money("2.60", "EUR"));
	public static final ProductOffer DOUBLE_SCHOKO_CAPPUCCINO = addProductOffer(
			Products.DOUBLE_SCHOKO_CAPPUCCINO, new Money("3.10", "EUR"));

	public static final ProductOffer VIETNAMESISCHER_EISKAFFEE = addProductOffer(
			Products.VIETNAMESISCHER_EISKAFFEE, new Money("2.00", "EUR"));
	public static final ProductOffer ICED_DOUBLE_ESPRESSO = addProductOffer(Products.ICED_DOUBLE_ESPRESSO,
			new Money("1.50", "EUR"));
	public static final ProductOffer ICED_DOUBLE_CAPPUCCINO = addProductOffer(
			Products.ICED_DOUBLE_CAPPUCCINO, new Money("1.70", "EUR"));
	public static final ProductOffer ICED_COFFEE = addProductOffer(Products.ICED_COFFEE, new Money("1.90",
			"EUR"));
	public static final ProductOffer ICED_COFFEE_MIT_MILCH = addProductOffer(Products.ICED_COFFEE_MIT_MILCH,
			new Money("2.20", "EUR"));

	public static final ProductOffer MINT_TEA = addProductOffer(Products.MINT_TEA, new Money("1.40", "EUR"));
	public static final ProductOffer BLACK_TEA = addProductOffer(Products.BLACK_TEA, new Money("1.60", "EUR"));
	public static final ProductOffer WHITE_TEA = addProductOffer(Products.WHITE_TEA, new Money("2.30", "EUR"));

	public static final ProductOffer HEISSE_BIO_ZITRONE = addProductOffer(Products.HEISSE_BIO_ZITRONE,
			new Money("2.30", "EUR"));

	public static final ProductOffer APFELSCHORLE = addProductOffer(Products.APFELSCHORLE, new Money("2.30",
			"EUR"));
	public static final ProductOffer ICED_TEA = addProductOffer(Products.ICED_TEA, new Money("2.30", "EUR"));

	private static ProductOffer addProductOffer(Product product, Money price) {
		return new ProductOffer(product, price, ValidityDates.VALID_FROM_DATE, ValidityDates.VALID_TO_DATE);
	}

}
