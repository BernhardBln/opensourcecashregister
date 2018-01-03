package de.bstreit.java.oscr.business.taxation;

import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.base.finance.tax.dao.IVATClassRepository;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillItem;
import de.bstreit.java.oscr.business.offers.AbstractOffer;
import de.bstreit.java.oscr.business.products.AbstractSalesItem;
import de.bstreit.java.oscr.business.taxation.dao.ITaxInfoRepository;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Objects;
import java.util.Optional;

import static lombok.AccessLevel.PROTECTED;

/**
 * <p>
 * Simple version - use overriding tax info from bill item, or if not present,
 * the bill's global tax info.
 * </p>
 * <p>
 * Only in case we have "to go" as tax info, use reduced VAT.
 * </p>
 *
 * @author Bernhard Streit
 */
@Named
@Setter(PROTECTED)
public class SimpleVATFinderDoNotUseInProduction implements IVATFinder {

  @Inject
  private IVATClassRepository vatClassRepository;
  @Inject
  private ITaxInfoRepository taxInfoRepository;

  private VATClass normalVATClass;
  private VATClass reducedVATClass;

  private TaxInfo reducedVATClassTaxInfo;
  private TaxInfo foodSellVATClassTaxInfo;


  @PostConstruct
  private void init() {
    normalVATClass = vatClassRepository.findByDesignationAndValidToIsNull("Normaler Steuersatz");
    reducedVATClass = vatClassRepository.findByDesignationAndValidToIsNull("Ermäßigter Steuersatz");
    reducedVATClassTaxInfo = taxInfoRepository.findByDenotationAndValidToIsNull("to go");
    foodSellVATClassTaxInfo = taxInfoRepository.findByDenotationAndValidToIsNull("food sale");
  }


  /**
   * Most complicated case:
   * <p>
   * Global tax info: inhouse (19%)
   * - overwriting tax info of bill item offered product: food (7%)
   * --- overwriting tax info of extraoffer 1: xxx (19%)
   * <p>
   * In that case, it would be 19%
   * <p>
   * Another example:
   * <p>
   * Global tax info: inhouse (19%)
   * - overwriting tax info of bill item offered product: food (7%)
   * <p>
   * In that case, it would be 7%.
   * <p>
   * Problematic example (do not define differnet vat classes for extra and variation offers for
   * the same
   *
   * @param billItem
   * @param bill
   * @return
   */
  @Override
  public VATClass getVATClassFor(final BillItem billItem, final Bill bill) {

    final Optional<TaxInfo> overridingTaxInfoFromExtraOrVariationOffer =
      getHighestVatFromExtraOrVariationOffers(billItem);

    // tax info from offered product, or of bill if not set
    final TaxInfo itemOrParentTaxInfo = Optional
      .ofNullable(getTaxInfoFrom(billItem))
      .orElse(bill.getGlobalTaxInfo());

    // if extra or variation offer VAT class is set, it wins
    final TaxInfo applyingTaxInfo = overridingTaxInfoFromExtraOrVariationOffer
      .orElse(itemOrParentTaxInfo);

    if (applyingTaxInfo.equals(reducedVATClassTaxInfo) || applyingTaxInfo.equals
      (foodSellVATClassTaxInfo)) {
      return reducedVATClass;
    }

    return normalVATClass;
  }

  private Optional<TaxInfo> getHighestVatFromExtraOrVariationOffers(final BillItem billItem) {

    final Optional<TaxInfo> notReduced = billItem
      .getExtraAndVariationOffers()
      .stream()
      .map(AbstractOffer::getOfferedItem)
      .map(AbstractSalesItem::getOverridingTaxInfo)
      .filter(Objects::nonNull)
      .filter(foodSellVATClassTaxInfo::equals)
      .findAny();

    if (notReduced.isPresent()) {
      return notReduced;
    }

    final Optional<TaxInfo> reduced = billItem
      .getExtraAndVariationOffers()
      .stream()
      .map(AbstractOffer::getOfferedItem)
      .map(AbstractSalesItem::getOverridingTaxInfo)
      .filter(Objects::nonNull)
      .filter(reducedVATClassTaxInfo::equals)
      .findAny();

    return reduced;
  }


  private TaxInfo getTaxInfoFrom(final BillItem billItem) {
    return billItem
      .getOffer()
      .getOfferedItem()
      .getOverridingTaxInfo();
  }
}
