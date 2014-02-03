package de.bstreit.java.oscr.business.taxation;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Optional;

import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.base.finance.tax.dao.IVATClassRepository;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillItem;
import de.bstreit.java.oscr.business.taxation.dao.ITaxInfoRepository;

/**
 * <p>
 * Simple version - use overriding tax info from bill item, or if not present,
 * the bill's global tax info.
 * </p>
 * <p>
 * Only in case we have "to go" as tax info, use reduced VAT.
 * </p>
 * 
 * 
 * @author Bernhard Streit
 */
@Named
public class SimpleVATFinderDoNotUseInProduction implements IVATFinder {

  @Inject
  private IVATClassRepository vatClassRepository;
  @Inject
  private ITaxInfoRepository taxInfoRepository;

  private VATClass normalVATClass;
  private VATClass reducedVATClass;

  private TaxInfo reducedVATClassTaxInfo;


  @PostConstruct
  private void init() {
    normalVATClass = vatClassRepository.findByDesignationAndValidToIsNull("Normaler Steuersatz");
    reducedVATClass = vatClassRepository.findByDesignationAndValidToIsNull("Ermäßigter Steuersatz");
    reducedVATClassTaxInfo = taxInfoRepository.findByDenotationAndValidToIsNull("to go");
  }


  @Override
  public VATClass getVATClassFor(BillItem billItem, Bill bill) {

    final TaxInfo applyingTaxInfo = Optional
        .fromNullable(getTaxInfoFrom(billItem))
        .or(bill.getGlobalTaxInfo());

    if (applyingTaxInfo.equals(reducedVATClassTaxInfo)) {
      return reducedVATClass;
    }

    return normalVATClass;
  }


  private TaxInfo getTaxInfoFrom(BillItem billItem) {
    return billItem.getOffer().getOfferedItem().getOverridingTaxInfo();
  }
}
