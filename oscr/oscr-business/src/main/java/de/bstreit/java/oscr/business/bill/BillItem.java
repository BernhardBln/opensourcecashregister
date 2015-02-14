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
 * --
 *
 * See oscr/licenses/gpl-3.txt for a copy of the GNU GPL.
 * See oscr/README.txt for more information about the software and the author(s).
 *
 */
package de.bstreit.java.oscr.business.bill;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.google.common.collect.Lists;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.persistence.AbstractPersistentObject;
import de.bstreit.java.oscr.business.offers.AbstractOffer;
import de.bstreit.java.oscr.business.offers.ExtraOffer;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.offers.PromoOffer;
import de.bstreit.java.oscr.business.offers.VariationOffer;

/**
 * Represents an item on the bill. Items can only be of type
 * {@link ProductOffer}, as Extras and Variations
 *
 * @author streit
 */
@Entity
public class BillItem extends AbstractPersistentObject {

	@ManyToOne(optional = false)
	private ProductOffer offer;

	@ManyToMany
	private final List<AbstractOffer<?>> extraAndVariationOffers = Lists
	.newArrayList();

	@SuppressWarnings("unused")
	private BillItem() {
		// For Hibernate
	}

	public BillItem(ProductOffer offer) {
		this.offer = offer;
	}

	/**
	 * @param variationOffer
	 */
	public void toggleVariationOffer(VariationOffer variationOffer) {
		final int index = extraAndVariationOffers.indexOf(variationOffer);

		if (index == -1) {
			extraAndVariationOffers.add(variationOffer);
		} else {
			extraAndVariationOffers.remove(index);
		}
	}

	public List<AbstractOffer<?>> getExtraAndVariationOffers() {
		return extraAndVariationOffers;
	}

	/**
	 * @param extraOffer
	 */
	void addExtraOffer(ExtraOffer extraOffer) {
		extraAndVariationOffers.add(extraOffer);
	}

	public void addPromoOffer(PromoOffer promoOffer) {
		extraAndVariationOffers.add(promoOffer);
	}

	/**
	 * @return the {@link #offer}
	 */
	public ProductOffer getOffer() {
		return offer;
	}

	public boolean hasUndoable() {
		return !extraAndVariationOffers.isEmpty();
	}

	public void undoLastAction() {
		extraAndVariationOffers.remove(extraAndVariationOffers.size() - 1);
	}

	public Money getPriceGross() {
		Money priceGross = offer.getPriceGross();
		Money zero = new Money(BigDecimal.ZERO, priceGross.getCurrency());

		return priceGross.add(extraAndVariationOffers.stream()
				.map(o -> o.getPriceGross()).reduce((p1, p2) -> p1.add(p2))
				.orElse(zero));
	}

	public String getName() {
		String name = getOffer().getOfferedItem().getName();

		for (final AbstractOffer<?> offer : extraAndVariationOffers) {
			name = name + (offer instanceof ExtraOffer ? " plus " : " with ")
					+ offer.getOfferedItem().getName();
		}

		return name;
	}

}
