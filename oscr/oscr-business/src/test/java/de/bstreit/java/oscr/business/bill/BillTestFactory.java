package de.bstreit.java.oscr.business.bill;

import java.util.Date;

import de.bstreit.java.oscr.business.staff.User;
import de.bstreit.java.oscr.business.taxation.TaxInfo;

public class BillTestFactory {

	public Bill create(TaxInfo defaultGlobalTaxInfo, Date billOpeningDate,
			Date billClosingDate) {
		final Bill bill = new Bill(defaultGlobalTaxInfo, billOpeningDate);

		if (billClosingDate != null) {
			bill.closeBill(new User("test", "test"), billClosingDate);
		}

		return bill;
	}
}
