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
package de.bstreit.java.oscr.business.offers;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;

import de.bstreit.java.oscr.business.base.ILabelledItem;
import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.persistence.AbstractPersistentObjectWithContinuance;
import de.bstreit.java.oscr.business.products.AbstractSalesItem;

@Entity
@Table(name = "Offers")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class AbstractOffer<OFFERED_ITEM extends AbstractSalesItem>
		extends
		AbstractPersistentObjectWithContinuance<AbstractOffer<OFFERED_ITEM>>
		implements ILabelledItem {

	/**
	 * <p>
	 * The gross price of this offer. Since this is fixed, the net price will
	 * vary depending on the vat class.
	 * </p>
	 * <p>
	 * This is not considered in {@link #equals(Object)} and {@link #hashCode()}
	 * as the sales items and the validFrom and validTo dates are already
	 * sufficient and serve as a natural key.
	 * </p>
	 */
	@Type(type = "de.bstreit.java.oscr.business.base.finance.money.MoneyType")
	@Columns(columns = { @Column(name = "priceValue"),
			@Column(name = "priceCurrency") })
	// @Access(AccessType.FIELD)
	private final Money priceGross;

	/**
	 * The item contained in this offer.
	 */
	@NaturalId
	// @Access(AccessType.FIELD)
	@ManyToOne(targetEntity = AbstractSalesItem.class, cascade = CascadeType.ALL)
	private final OFFERED_ITEM offeredItem;

	@Transient
	private transient String label = null;

	AbstractOffer(OFFERED_ITEM item, Money priceGross, Date validFrom,
			Date validТо) {
		super(validFrom, validТо);
		this.offeredItem = item;
		this.priceGross = priceGross;
	}

	public Money getPriceGross() {
		return priceGross;
	}

	@Override
	public String getLabel() {
		return "<html><center>" + offeredItem.getLabel() + "<BR>"
				+ getPriceGross().toString() + "</center></html>";
	}

	@Override
	protected final void additionalEqualsForSubclasses(EqualsBuilder builder,
			AbstractOffer<OFFERED_ITEM> obj) {
		builder.append(offeredItem, obj.offeredItem);
	}

	@Override
	protected final void additionalHashcodeForSubclasses(HashCodeBuilder builder) {
		builder.append(offeredItem);
	}

	public OFFERED_ITEM getOfferedItem() {
		return offeredItem;
	}

}
