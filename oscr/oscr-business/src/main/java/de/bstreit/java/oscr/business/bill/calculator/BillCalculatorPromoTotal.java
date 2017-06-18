package de.bstreit.java.oscr.business.bill.calculator;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillItem;
import de.bstreit.java.oscr.business.bill.IBillCalculator;
import de.bstreit.java.oscr.business.offers.PromoOffer;
import de.bstreit.java.oscr.business.taxation.IVATFinder;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.*;

import static lombok.AccessLevel.PACKAGE;

@Named
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class BillCalculatorPromoTotal implements IBillCalculator {

  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
    .getLogger(BillCalculatorPromoTotal.class);

  @Inject
  @Setter(PACKAGE)
  private Currency defaultCurrency;

  @Inject
  @Setter(PACKAGE)
  private IVATFinder vatFinder;

  private Bill bill;
  private final Map<BillItem, Character> billItemsVatClassesAbbreviated = new HashMap<>();
  private final BiMap<Character, VATClass> vatClassAbbreviations = HashBiMap
    .create();

  private Money ZERO;

  private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);


  BillCalculatorPromoTotal() {

  }

  @PostConstruct
  public void init() {
    ZERO = new Money(BigDecimal.ZERO, defaultCurrency);
  }

  /**
   * Analyse this bill. Use the get methods to query information about this
   * bill.
   * <p>
   * TODO: After usage, call freeResults() to clear the cache. (??)
   *
   * @param bill
   */
  @Override
  public void analyse(final Bill bill) {

    this.bill = bill;

    char currentChar = 'A';

    for (final BillItem item : bill) {

      if (noPromoAtAll(bill, item)) {
        continue;
      }

      final VATClass vatClass = vatFinder.getVATClassFor(item, bill);

      if (!vatClassAbbreviations
        .values()
        .contains(vatClass)) {
        vatClassAbbreviations.put(currentChar, vatClass);
        currentChar++;
      }

      billItemsVatClassesAbbreviated.put(item, vatClassAbbreviations
        .inverse()
        .get(vatClass));

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
      final Money reduction = getGrossPromoReduction(item);

      if (bill.isFreePromotionOffer()) {
        currentPriceGross = item
          .getPriceGross()
          .absolute();
      } else if (bill.hasReduction()) {
        // if e.g. 1 EUR is taken off from an item that costs 3 EURs, and
        // additionally 20% is offered,
        // then the further reduction is (3 - 1) * 0.2
        final Money furtherReduction = item
          .getPriceGross()
          .absolute()
          .multiply(percentReduction(bill));
        currentPriceGross = reduction.add(furtherReduction);
      } else {
        currentPriceGross = reduction;
      }

      total = total.add(currentPriceGross);
    }

    return total;
  }


  @Override
  public Money getTotalNetFor(final VATClass vatClass) {
    Money total = ZERO;

    for (final BillItem item : bill) {

      if (noPromoAtAll(bill, item)) {
        continue;
      }

      if (vatClass.equals(vatFinder.getVATClassFor(item, bill))) {

        final Money currentPriceGross;
        final Money reduction = getGrossPromoReduction(item);

        if (bill.isFreePromotionOffer()) {
          currentPriceGross = item.getPriceGross();
        } else if (bill.hasReduction()) {
          final Money furtherReduction = item
            .getPriceGross()
            .absolute()
            .multiply(percentReduction(bill));
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
  public Money getTotalGrossFor(final VATClass vatClass) {
    Money total = ZERO;

    for (final BillItem item : bill) {

      if (noPromoAtAll(bill, item)) {
        continue;
      }

      if (vatClass.equals(vatFinder.getVATClassFor(item, bill))) {

        final Money currentPriceGross;
        final Money reduction = getGrossPromoReduction(item);

        if (bill.isFreePromotionOffer()) {
          currentPriceGross = item.getPriceGross();
        } else if (bill.hasReduction()) {
          final Money furtherReduction = item
            .getPriceGross()
            .absolute()
            .multiply(percentReduction(bill));
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
  public Money getTotalVATFor(final VATClass vatClass) {

    final Money totalGross = getTotalGrossFor(vatClass);
    final Money totalNet = getTotalNetFor(vatClass);

    return totalGross.subtract(totalNet);
  }

  @Override
  public Money getNetFor(final BillItem billItem) {
    if (!bill
      .getBillItems()
      .contains(billItem)) {
      throw new RuntimeException("billItem not contained in bill!");
    }

    if (billItem
      .getOffer()
      .getOfferedItem()
      .isNoReduction()) {
      return new Money(BigDecimal.ZERO, defaultCurrency);
    }

    final VATClass applyingVATClass = vatFinder.getVATClassFor(billItem,
      bill);

    Money reduction = getGrossPromoReduction(billItem);

    if (bill.hasReduction()) {
      final Money furtherReduction = billItem
        .getPriceGross()
        .absolute()
        .multiply(percentReduction(bill));
      reduction = reduction.add(furtherReduction);
    }

    return reduction.getNet(applyingVATClass);
  }

  @Override
  public String getVATClassAbbreviationFor(final BillItem billItem) {
    final Character abbr = billItemsVatClassesAbbreviated
      .get(billItem);
    return abbr == null ? null : abbr.toString();
  }

  @Override
  public VATClass getVATClassForAbbreviation(final Character abbreviation) {
    return vatClassAbbreviations.get(abbreviation);
  }

  @Override
  public SortedSet<Character> allFoundVATClassesAbbreviated() {
    return new TreeSet<>(vatClassAbbreviations.keySet());
  }

  private boolean noPromoAtAll(final Bill bill,
                               final BillItem item) {

    return
      (!bill.isFreePromotionOffer() && !hasPromoOffer(item) && !bill.hasReduction())
        || item
        .getOffer()
        .getOfferedItem()
        .isNoReduction();
  }

  private BigDecimal percentReduction(final Bill bill2) {
    return BigDecimal
      .valueOf(bill2.getReduction())
      .divide(ONE_HUNDRED);
  }

  private boolean hasPromoOffer(final BillItem item) {
    return item
      .getExtraAndVariationOffers()
      .stream()
      .anyMatch(o -> o instanceof PromoOffer);
  }

  private Money getGrossPromoReduction(final BillItem item) {
    return item
      .getExtraAndVariationOffers()
      .stream()
      .filter(o -> (o instanceof PromoOffer))
      .map(o -> o
        .getPriceGross()
        .absolute())
      .reduce((p1, p2) -> p1.add(p2))
      .orElse(ZERO);
  }
}