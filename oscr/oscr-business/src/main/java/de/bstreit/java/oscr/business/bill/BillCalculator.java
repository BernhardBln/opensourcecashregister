package de.bstreit.java.oscr.business.bill;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.base.finance.tax.dao.IVATClassRepository;
import de.bstreit.java.oscr.business.taxation.IVATFinder;
import de.bstreit.java.oscr.business.taxation.TaxInfo;
import de.bstreit.java.oscr.business.taxation.dao.ITaxInfoRepository;

@Named
public class BillCalculator {

  @Inject
  private Currency defaultCurrency;

  @Inject
  private ITaxInfoRepository taxInfoRepository;

  @Inject
  private IVATClassRepository vatClassRepository;

  @Inject
  private IVATFinder vatFinder;

  private Bill bill;
  private Map<BillItem, Character> billItemsVatClassesAbbreviated = new HashMap<BillItem, Character>();
  private BiMap<Character, VATClass> vatClassAbbreviations = HashBiMap.create();

  private Money ZERO;
  private TaxInfo NON_FOOD;
  private VATClass NORMAL_VAT;


  @PostConstruct
  private void init() {
    ZERO = new Money(BigDecimal.ZERO, defaultCurrency);
    NON_FOOD = taxInfoRepository.findByDenotationAndValidToIsNull("non-food");
    NORMAL_VAT = vatClassRepository.findByDesignationAndValidToIsNull("Normaler Steuersatz");
  }

  /**
   * Analyse this bill. Use the get methods to query information about this
   * bill.
   * 
   * TODO: After usage, call freeResults() to clear the cache. (??)
   * 
   * @param bill
   */
  public void analyse(Bill bill) {
    this.bill = bill;

    char currentChar = 'A';

    for (BillItem item : bill) {
      final VATClass vatClass = vatFinder.getVATClassFor(item, bill);

      if (!vatClassAbbreviations.values().contains(vatClass)) {
        vatClassAbbreviations.put(currentChar, vatClass);
        currentChar++;
      }

      billItemsVatClassesAbbreviated.put(item, vatClassAbbreviations.inverse().get(vatClass));

    }

  }

  public void freeResults() {
    this.bill = null;
    vatClassAbbreviations.clear();
    billItemsVatClassesAbbreviated.clear();
    // TODO: clear cache if there is any
  }

  public Money getTotalGross() {
    Money total = ZERO;

    for (BillItem item : bill) {
      final Money currentPriceGross = item.getOffer().getPriceGross();
      total = total.add(currentPriceGross);
    }

    return total;
  }

  public Money getTotalNetFor(VATClass vatClass) {
    Money total = ZERO;

    for (BillItem item : bill) {
      if (vatClass.equals(vatFinder.getVATClassFor(item, bill))) {
        final Money currentPriceGross = item.getOffer().getPriceGross();
        final Money currentPriceNet = currentPriceGross.getNet(vatClass);
        total = total.add(currentPriceNet);
      }
    }

    return total;
  }

  public Money getTotalGrossFor(VATClass vatClass) {
    Money total = ZERO;

    for (BillItem item : bill) {
      if (vatClass.equals(vatFinder.getVATClassFor(item, bill))) {
        total = total.add(item.getOffer().getPriceGross());
      }
    }

    return total;
  }


  public Money getNetFor(BillItem billItem) {
    if (!bill.getBillItems().contains(billItem)) {
      throw new RuntimeException("billItem not contained in bill!");
    }

    final VATClass applyingVATClass = vatFinder.getVATClassFor(billItem, bill);

    return billItem.getOffer().getPriceGross().getNet(applyingVATClass);
  }

  //
  // public VATClass getVATClassFor(String abbreviation) {
  // return vatClassAbbreviations.get(abbreviation);
  // }

  public String getVATClassAbbreviationFor(BillItem billItem) {
    return billItemsVatClassesAbbreviated.get(billItem).toString();
  }

  public String getAbbreviationFor(VATClass vatClass) {
    return vatClassAbbreviations.inverse().get(vatClass).toString();
  }

  public Collection<VATClass> allFoundVATClasses() {
    return vatClassAbbreviations.values();
  }
}