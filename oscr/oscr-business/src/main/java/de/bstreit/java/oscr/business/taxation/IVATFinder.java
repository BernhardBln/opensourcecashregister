package de.bstreit.java.oscr.business.taxation;

import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillItem;

/**
 * Helps you to determine the VAT class for a bill item.
 * 
 * @author Bernhard Streit
 */
public interface IVATFinder {


  /**
   * Analyse the bill's global tax infos and the ones from the billItem and
   * return the correct VAT class.
   * 
   * @param billItem
   * @param bill
   * @return the vat class applying to this bill
   */
  public VATClass getVATClassFor(BillItem billItem, Bill bill);

}
