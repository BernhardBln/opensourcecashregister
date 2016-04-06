package de.bstreit.java.oscr.business.bill.calculator;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillItem;
import de.bstreit.java.oscr.business.bill.IBillCalculator;
import de.bstreit.java.oscr.business.offers.PromoOffer;
import de.bstreit.java.oscr.business.taxation.IVATFinder;

@Named
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class BillCalculatorPromoTotal implements IBillCalculator {

  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
      .getLogger(BillCalculatorPromoTotal.class);
  @Inject
  private Currency defaultCurrency;

  @Inject
  private IVATFinder vatFinder;

  private Bill bill;
  private final Map<BillItem, Character> billItemsVatClassesAbbreviated = new HashMap<BillItem, Character>();
  private final BiMap<Character, VATClass> vatClassAbbreviations = HashBiMap
      .create();

  private Money ZERO;


  private static final BigDecimal TWENTY_PERCENT = new BigDecimal("0.2");
  private static final BigDecimal TWENTY = new BigDecimal("20");


  BillCalculatorPromoTotal() {

  }

  @PostConstruct
  private void init() {
    ZERO = new Money(BigDecimal.ZERO, defaultCurrency);
  }

  /**
   * Analyse this bill. Use the get methods to query information about this
   * bill.
   *
   * TODO: After usage, call freeResults() to clear the cache. (??)
   *
   * @param bill
   */
  @Override
  public void analyse(Bill bill) {

    this.bill = bill;

    char currentChar = 'A';

    for (final BillItem item : bill) {

      if (noPromoAtAll(bill, item)) {
        continue;
      }

      final VATClass vatClass = vatFinder.getVATClassFor(item, bill);

      if (!vatClassAbbreviations.values().contains(vatClass)) {
        vatClassAbbreviations.put(currentChar, vatClass);
        currentChar++;
      }

      billItemsVatClassesAbbreviated.put(item, vatClassAbbreviations
          .inverse().get(vatClass));

    }

  }

  @PreDestroy
  @Override
  public void close() {
    logger.debug("closing bill calculator");

    this.bill = null;
    vatClassAbbreviations.clear();
    billItemsVatClassesAbbreviated.clear();
    // TODO: clear cache if there is any
  }

  @Override
  public Money getTotalGross() {

    Money total = ZERO;

    for (final BillItem item : bill) {

      if (noPromoAtAll(bill, item)) {
        continue;
      }

      final Money currentPriceGross;
      Money reduction = getGrossPromoReduction(item);

      if (bill.isFreePromotionOffer()) {
        currentPriceGross = item.getPriceGross().absolute();
      }
      else if (bill.isTwentyPercentOff()) {
        // if e.g. 1 EUR is taken off from an item that costs 3 EURs, and
        // additionally 20% is offered,
        // then the further reduction is (3 - 1) * 0.2
        Money furtherReduction = item.getPriceGross().absolute().multiply(TWENTY_PERCENT);
        currentPriceGross = reduction.add(furtherReduction);
      } else {
        currentPriceGross = reduction;
      }

      total = total.add(currentPriceGross);
    }

    return total;
  }

  @Override
  public Money getTotalNetFor(VATClass vatClass) {
    Money total = ZERO;

    for (final BillItem item : bill) {

      if (noPromoAtAll(bill, item)) {
        continue;
      }

      if (vatClass.equals(vatFinder.getVATClassFor(item, bill))) {

        final Money currentPriceGross;
        Money reduction = getGrossPromoReduction(item);

        if (bill.isFreePromotionOffer()) {
          currentPriceGross = item.getPriceGross();
        }
        else if (bill.isTwentyPercentOff()) {
          Money furtherReduction = item.getPriceGross().absolute()
              .multiply(TWENTY_PERCENT);
          currentPriceGross = reduction.add(furtherReduction);
        } else {
          // whole bill is not free, but this item has a reduction
          currentPriceGross = reduction;
        }

        final Money currentPriceNet = currentPriceGross
            .getNet(vatClass);

        total = total.add(currentPriceNet);
      }
    }

    return total;
  }

  @Override
  public Money getTotalGrossFor(VATClass vatClass) {
    Money total = ZERO;

    for (final BillItem item : bill) {

      if (noPromoAtAll(bill, item)) {
        continue;
      }

      if (vatClass.equals(vatFinder.getVATClassFor(item, bill))) {

        final Money currentPriceGross;
        Money reduction = getGrossPromoReduction(item);

        if (bill.isFreePromotionOffer()) {
          currentPriceGross = item.getPriceGross();
        }
        else if (bill.isTwentyPercentOff()) {
          Money furtherReduction = item.getPriceGross().absolute()
              .multiply(TWENTY_PERCENT);
          currentPriceGross = reduction.add(furtherReduction);
        } else {
          // whole bill is not free, but this item has a reduction
          currentPriceGross = reduction;
        }

        total = total.add(currentPriceGross);
      }
    }

    return total;
  }

  @Override
  public Money getTotalVATFor(VATClass vatClass) {

    final Money totalGross = getTotalGrossFor(vatClass);
    final Money totalNet = getTotalNetFor(vatClass);

    return totalGross.subtract(totalNet);
  }

  @Override
  public Money getNetFor(BillItem billItem) {
    if (!bill.getBillItems().contains(billItem)) {
      throw new RuntimeException("billItem not contained in bill!");
    }

    final VATClass applyingVATClass = vatFinder.getVATClassFor(billItem,
        bill);

    Money reduction = getGrossPromoReduction(billItem);

    if (bill.isTwentyPercentOff()) {
      Money furtherReduction = billItem.getPriceGross().absolute()
          .multiply(TWENTY_PERCENT);
      reduction = reduction.add(furtherReduction);
    }

    return reduction.getNet(applyingVATClass);
  }

  @Override
  public String getVATClassAbbreviationFor(BillItem billItem) {
    return billItemsVatClassesAbbreviated.get(billItem).toString();
  }

  @Override
  public VATClass getVATClassForAbbreviation(Character abbreviation) {
    return vatClassAbbreviations.get(abbreviation);
  }

  @Override
  public SortedSet<Character> allFoundVATClassesAbbreviated() {
    return new TreeSet<Character>(vatClassAbbreviations.keySet());
  }

  private boolean noPromoAtAll(Bill bill,
      final BillItem item) {

    return !bill.isFreePromotionOffer() && !hasPromoOffer(item) && !bill.isTwentyPercentOff();
  }

  private boolean hasPromoOffer(final BillItem item) {
    return item.getExtraAndVariationOffers().stream()
        .anyMatch(o -> o instanceof PromoOffer);
  }

  private Money getGrossPromoReduction(final BillItem item) {
    return item.getExtraAndVariationOffers().stream()
        .filter(o -> (o instanceof PromoOffer))
        .map(o -> o.getPriceGross().absolute())
        .reduce((p1, p2) -> p1.add(p2)).orElse(ZERO);
  }
}