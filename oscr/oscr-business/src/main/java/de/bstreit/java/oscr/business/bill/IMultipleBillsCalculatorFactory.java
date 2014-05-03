package de.bstreit.java.oscr.business.bill;

import java.util.Collection;



public interface IMultipleBillsCalculatorFactory {

  public abstract IMultipleBillsCalculator create(Collection<Bill> bill);

}