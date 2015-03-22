package de.bstreit.java.oscr.business.bill;

import de.bstreit.java.oscr.business.bill.calculator.WhatToCount;

public interface IBillCalculatorFactory {

	public abstract IBillCalculator create(Bill bill, WhatToCount whatToCount);

}