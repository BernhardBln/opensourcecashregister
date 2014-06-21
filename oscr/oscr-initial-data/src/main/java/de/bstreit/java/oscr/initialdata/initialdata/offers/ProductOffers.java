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
package de.bstreit.java.oscr.initialdata.initialdata.offers;

import javax.inject.Named;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.products.Product;
import de.bstreit.java.oscr.initialdata.AbstractDataContainer;
import de.bstreit.java.oscr.initialdata.initialdata.ValidityDates;
import de.bstreit.java.oscr.initialdata.initialdata.products.Products;

/**
 * @author streit
 * 
 */
@Named
public class ProductOffers extends AbstractDataContainer<ProductOffer> {

	public static final ProductOffer ESPRESSO = addProductOffer(
			Products.ESPRESSO, new Money("1.30", "EUR"));
	public static final ProductOffer DOUBLE_ESPRESSO = addProductOffer(
			Products.DOUBLE_ESPRESSO, new Money("1.80", "EUR"));
	public static final ProductOffer HarioCoffee = addProductOffer(
			Products.HarioCoffee, new Money("2.70", "EUR"));
	public static final ProductOffer AeropressCoffee = addProductOffer(
			Products.AeropressCoffee, new Money("2.70", "EUR"));

	public static final ProductOffer AMERICANO = addProductOffer(
			Products.AMERICANO, new Money("1.90", "EUR"));

	public static final ProductOffer SINGLE_ESPRESSO_MACCHIATO = addProductOffer(
			Products.SINGLE_ESPRESSO_MACCHIATO, new Money("1.50", "EUR"));
	public static final ProductOffer DOUBLE_ESPRESSO_MACCHIATO = addProductOffer(
			Products.DOUBLE_ESPRESSO_MACCHIATO, new Money("2.00", "EUR"));

	public static final ProductOffer CAPPUCCINO = addProductOffer(
			Products.CAPPUCCINO, new Money("2.30", "EUR"));
	public static final ProductOffer DOUBLE_CAPPUCCINO = addProductOffer(
			Products.DOUBLE_CAPPUCCINO, new Money("3.10", "EUR"));

	public static final ProductOffer LATTE_MACCHIATO = addProductOffer(
			Products.LATTE_MACCHIATO, new Money("3", "EUR"));

	public static final ProductOffer KAKAO = addProductOffer(Products.KAKAO,
			new Money("3", "EUR"));
	public static final ProductOffer SINGLE_SCHOKO_CAPPUCCINO = addProductOffer(
			Products.SINGLE_SCHOKO_CAPPUCCINO, new Money("4", "EUR"));

	public static final ProductOffer ICED_DOUBLE_CAPPUCCINO = addProductOffer(
			Products.ICED_DOUBLE_CAPPUCCINO, new Money("2.80", "EUR"));
	public static final ProductOffer FLAT_WHITE = addProductOffer(
			Products.FLAT_WHITE, new Money("3.10", "EUR"));

	public static final ProductOffer MINT_TEA = addProductOffer(
			Products.MINT_TEA, new Money("1.80", "EUR"));
	public static final ProductOffer LINDENBLUETEN = addProductOffer(
			Products.LINDENBLUETEN, new Money("1.90", "EUR"));
	public static final ProductOffer WHITE_TEA = addProductOffer(
			Products.WHITE_TEA, new Money("2.60", "EUR"));

	public static final ProductOffer APFELSCHORLE = addProductOffer(
			Products.APFELSCHORLE, new Money("2.50", "EUR"));
	public static final ProductOffer APFEL_KIRSCH_HOLUNDER = addProductOffer(
			Products.APFEL_KIRSCH_HOLUNDER, new Money("2.50", "EUR"));
	public static final ProductOffer RHABARBERSCHORLE = addProductOffer(
			Products.RHABARBERSCHORLE, new Money("2.50", "EUR"));
	public static final ProductOffer CHARITEA = addProductOffer(
			Products.CHARITEA, new Money("3", "EUR"));
	public static final ProductOffer CLUB_MATE_COLA = addProductOffer(
			Products.CLUB_MATE, new Money("2.20", "EUR"));

	public static final ProductOffer BUTTER_CROISSANT = addProductOffer(
			Products.BUTTER_CROISSANT, new Money("1.90", "EUR"));
	public static final ProductOffer FRENCH_BREAKFAST = addProductOffer(
			Products.FRENCH_BREAKFAST, new Money("2.50", "EUR"));
	public static final ProductOffer CHEESE_BREAKFAST = addProductOffer(
			Products.CHEESE_BREAKFAST, new Money("3.20", "EUR"));
	public static final ProductOffer VEGAN_BREAKFAST = addProductOffer(
			Products.VEGAN_BREAKFAST, new Money("3.50", "EUR"));

	private static ProductOffer addProductOffer(Product product, Money price) {
		return new ProductOffer(product, price, null,
				ValidityDates.VALID_FROM_DATE, ValidityDates.VALID_TO_DATE);
	}

	@Override
	public Class<ProductOffer> getType() {
		return ProductOffer.class;
	}

}
