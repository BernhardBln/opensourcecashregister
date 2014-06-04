package de.bstreit.java.oscr.business.eventbroadcasting;

import com.google.common.base.Optional;

import de.bstreit.java.oscr.business.bill.Bill;

public interface BillChangeListener {

	public void billUpdated(Optional<Bill> newBill);

}
