package de.bstreit.java.oscr.business.bill;

import java.util.Date;

import de.bstreit.java.oscr.business.taxation.TaxInfo;

public class BillTestFactory {

	public Bill create(TaxInfo defaultGlobalTaxInfo, Date billOpeningDate) {
		return new Bill(defaultGlobalTaxInfo, billOpeningDate);
	}
}
