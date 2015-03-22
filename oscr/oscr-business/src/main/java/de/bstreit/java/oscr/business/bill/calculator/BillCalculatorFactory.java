package de.bstreit.java.oscr.business.bill.calculator;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.context.ConfigurableApplicationContext;

import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.IBillCalculator;
import de.bstreit.java.oscr.business.bill.IBillCalculatorFactory;

@Named
public class BillCalculatorFactory implements IBillCalculatorFactory {

	@Inject
	private ConfigurableApplicationContext context;

	@Override
	public IBillCalculator create(Bill bill, WhatToCount whatToCount) {

		final IBillCalculator billCalculator = createBillCalculator(whatToCount);

		billCalculator.analyse(bill);

		return billCalculator;
	}

	private IBillCalculator createBillCalculator(WhatToCount whatToCount) {

		switch (whatToCount) {

		case PAYMENT:
			return context.getBean(BillCalculatorPayment.class);

		case PROMO_TOTAL:
			return context.getBean(BillCalculatorPromoTotal.class);

		case TOTAL:
			return context.getBean(BillCalculatorTotal.class);

		default:
			throw new IllegalStateException(
					"Missing calculator factory for type " + whatToCount);

		}
	}
}
