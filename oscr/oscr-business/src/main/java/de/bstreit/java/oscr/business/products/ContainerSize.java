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

import javax.persistence.Entity;

import org.hibernate.annotations.NaturalId;

import de.bstreit.java.oscr.business.base.persistence.AbstractPersistentObjectWithContinuance;

@Entity
public class ContainerSize extends AbstractPersistentObjectWithContinuance<ContainerSize> {

	/** e.g. "50 ml" */
	@NaturalId
	private String size;


	@SuppressWarnings("unused")
	private ContainerSize() {
	}

	public ContainerSize(String size, Date validFrom, Date validTo) {
		super(validFrom, validTo);
		this.size = size;
	}

	public String getSize() {
		return size;
	}

	@Override
	protected boolean additionalEqualsForSubclasses(ContainerSize obj) {
		return size.equals(obj.size);
	}

	@Override
	protected int additionalHashcodeForSubclasses() {
		return size.hashCode();
	}

}
