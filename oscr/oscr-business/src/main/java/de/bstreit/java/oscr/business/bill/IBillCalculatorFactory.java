package de.bstreit.java.oscr.business.bill;



public interface IBillCalculatorFactory {

  public abstract IBillCalculator create(Bill bill);

}