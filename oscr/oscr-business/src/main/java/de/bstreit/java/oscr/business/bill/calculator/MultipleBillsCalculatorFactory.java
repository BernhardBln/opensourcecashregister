package de.bstreit.java.oscr.business.bill.calculator;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.context.ConfigurableApplicationContext;

import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculator;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculatorFactory;

@Named
public class MultipleBillsCalculatorFactory implements
IMultipleBillsCalculatorFactory {

	@Inject
	private ConfigurableApplicationContext context;

	@Override
	public IMultipleBillsCalculator create(Collection<Bill> bills,
			WhatToCount whatToCount) {

		final MultipleBillsCalculator billCalculator = context
				.getBean(MultipleBillsCalculator.class);

		billCalculator.setWhatToCount(whatToCount);
		billCalculator.analyse(bills);

		return billCalculator;
	}
}
