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
  public IBillCalculator create(Bill bill) {

    final BillCalculator billCalculator = context.getBean(BillCalculator.class);

    billCalculator.analyse(bill);

    return billCalculator;
  }
}
