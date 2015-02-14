package de.bstreit.java.oscr.business.bill;

import java.util.Collection;

import de.bstreit.java.oscr.business.bill.calculator.WhatToCount;

public interface IMultipleBillsCalculatorFactory {

	public abstract IMultipleBillsCalculator create(Collection<Bill> bill,
			WhatToCount promoTotal);

}