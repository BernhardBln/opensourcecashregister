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
package de.bstreit.java.oscr.business.products;

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

import org.hibernate.annotations.NaturalId;

import de.bstreit.java.oscr.business.base.ILabelledItem;
import de.bstreit.java.oscr.business.base.persistence.AbstractPersistentObjectWithContinuance;

@Entity
@Table(name = "SalesItems")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class AbstractSalesItem extends AbstractPersistentObjectWithContinuance<AbstractSalesItem>
    implements
    ILabelledItem {

	@NaturalId
	private String name;

	@ManyToOne(cascade = CascadeType.ALL)
	@Column(nullable = true)
	private transient TaxInfo taxInfo;


	protected AbstractSalesItem(String name, Date validFrom, Date validTo) {
		super(validFrom, validTo);
		this.name = name;
	}

	public TaxInfo getTaxInfo() {
		return taxInfo;
	}

	public String getName() {
		return name;
	}

	public void setTaxInfo(TaxInfo taxInfo) {
		this.taxInfo = taxInfo;
	}

	@Override
	public String getLabel() {
		return name;
	}

	@Override
	protected final boolean additionalEqualsForSubclasses(AbstractSalesItem obj) {
		return name.equals(obj.getName());
	}

	@Override
	protected final int additionalHashcodeForSubclasses() {
		return name.hashCode();
	}

}
