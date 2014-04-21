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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.google.common.collect.ImmutableList;

import de.bstreit.java.oscr.business.taxation.TaxInfo;
import de.bstreit.java.oscr.business.user.User;

/**
 * 
 * @author streit
 */
@Entity
public class Bill implements Iterable<BillItem> {

	private static final TimeBasedGenerator timeBasedUuidGenerator;

	static {
		timeBasedUuidGenerator = Generators.timeBasedGenerator(EthernetAddress
				.fromInterface());
	}

	@Id
	@Column(length = 36)
	private final String id = timeBasedUuidGenerator.generate().toString();

	/**
	 * An optional description of the bill, can help to identify an opened bill
	 * (e.g. "table 3")
	 */
	@Column(nullable = true)
	private String description;

	@OneToMany(cascade = CascadeType.ALL)
	private final List<BillItem> billItems = new ArrayList<BillItem>();

	/** The date when the bill was opened. */
	@Column(nullable = false)
	private Date billOpened;

	/** The date when the bill was closed and paid. */
	@Column(nullable = true)
	private Date billClosed;

	/**
	 * The global tax info. Must not be null; default value must be set within
	 * the app
	 */
	// TODO [10]: check which cascade types we really need here. Same at
	// AbstractSalesItem.overridingTaxInfo
	@ManyToOne(cascade = { CascadeType.REFRESH }, optional = false)
	private TaxInfo globalTaxInfo;

	/**
	 * The user who was logged in when the bill was closed. This is not
	 * necessarily the person that opened the bill, but that received the
	 * payment.
	 */
	@ManyToOne(optional = true)
	private User cashier;

	Bill(TaxInfo defaultGlobalTaxInfo, Date billOpeningDate) {
		checkNotNull(defaultGlobalTaxInfo);
		setGlobalTaxInfo(defaultGlobalTaxInfo);
		billOpened = billOpeningDate;
	}

	private Bill() {

	}

	void addBillItem(BillItem item) {
		billItems.add(item);
	}

	/**
	 * @return the {@link #globalTaxInfo}
	 */
	public TaxInfo getGlobalTaxInfo() {
		return globalTaxInfo;
	}

	/**
	 * @param globalTaxInfo
	 *            the {@link #globalTaxInfo} to set
	 */
	public void setGlobalTaxInfo(TaxInfo globalTaxInfo) {
		this.globalTaxInfo = globalTaxInfo;
	}

	/**
	 * @return the {@link #billClosed}
	 */
	public Date getBillClosed() {
		return billClosed;
	}

	/**
	 * Invoked when an open bill is getting paid and hence closed.
	 * 
	 * @param billClosed
	 * @param billClosingDate
	 */
	void closeBill(User cashier, Date billClosingDate) {
		this.billClosed = billClosingDate;
		this.cashier = cashier;
	}

	public List<BillItem> getBillItems() {
		return ImmutableList.copyOf(billItems);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Bill other = (Bill) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public Iterator<BillItem> iterator() {
		return getBillItems().iterator();
	}

	public Date getBillOpened() {
		return billOpened;
	}

}
