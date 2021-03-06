package de.bstreit.java.oscr.business.bill.calculator;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillItem;
import de.bstreit.java.oscr.business.bill.IBillCalculator;
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
class BillCalculatorPayment implements IBillCalculator {

  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
    .getLogger(BillCalculatorPayment.class);

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


  BillCalculatorPayment() {

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

    if (bill.isFreePromotionOffer()) {
      return ZERO;
    }

    Money total = ZERO;

    for (final BillItem item : bill) {
      Money currentPriceGross = item.getPriceGross();

      if (bill.hasReduction() && !item
        .getOffer()
        .getOfferedItem()
        .isNoReduction()) {
        currentPriceGross = currentPriceGross.multiply(getPromotionPercentageForReduction(bill
          .getReduction()));
      }

      total = total.add(currentPriceGross);
    }

    return total;
  }


  @Override
  public Money getTotalNetFor(final VATClass vatClass) {

    if (bill.isFreePromotionOffer()) {
      return ZERO;
    }

    Money total = ZERO;

    for (final BillItem item : bill) {
      if (vatClass.equals(vatFinder.getVATClassFor(item, bill))) {

        final Money currentPriceGross = item.getPriceGross();
        Money currentPriceNet = currentPriceGross
          .getNet(vatClass);

        if (bill.hasReduction() && !item
          .getOffer()
          .getOfferedItem()
          .isNoReduction()) {
          currentPriceNet = currentPriceNet.multiply(getPromotionPercentageForReduction(bill
            .getReduction()));
        }

        total = total.add(currentPriceNet);
      }
    }


    return total;
  }

  @Override
  public Money getTotalGrossFor(final VATClass vatClass) {

    if (bill.isFreePromotionOffer()) {
      return ZERO;
    }

    Money total = ZERO;

    for (final BillItem item : bill) {
      if (vatClass.equals(vatFinder.getVATClassFor(item, bill))) {

        Money priceGross = item.getPriceGross();

        if (bill.hasReduction() && !item
          .getOffer()
          .getOfferedItem()
          .isNoReduction()) {
          priceGross = priceGross.multiply(getPromotionPercentageForReduction(bill.getReduction()));
        }

        total = total.add(priceGross);
      }
    }

    return total;
  }

  @Override
  public Money getTotalVATFor(final VATClass vatClass) {

    if (bill.isFreePromotionOffer()) {
      return ZERO;
    }

    final Money totalGross = getTotalGrossFor(vatClass);
    final Money totalNet = getTotalNetFor(vatClass);

    return totalGross.subtract(totalNet);
  }

  @Override
  public Money getNetFor(final BillItem billItem) {

    if (bill.isFreePromotionOffer()) {
      return ZERO;
    }

    if (!bill
      .getBillItems()
      .contains(billItem)) {
      throw new RuntimeException("billItem not contained in bill!");
    }

    final VATClass applyingVATClass = vatFinder.getVATClassFor(billItem,
      bill);

    Money netAmount = billItem
      .getPriceGross()
      .getNet(applyingVATClass);

    if (bill.hasReduction() && !billItem
      .getOffer()
      .getOfferedItem()
      .isNoReduction()) {
      netAmount = netAmount.multiply(getPromotionPercentageForReduction(bill.getReduction()));
    }

    return netAmount;
  }


  @VisibleForTesting
  static BigDecimal getPromotionPercentageForReduction(final Integer promotion) {

    // Input: 20% Promotion
    //
    // Calculation:
    // 100 - 20 = 80
    // 80 / 100 = 0.8
    //
    // Result: Multiply with 0.8

    final BigDecimal promotionAsBD = BigDecimal.valueOf(promotion);

    return ONE_HUNDRED
      .subtract(promotionAsBD)
      .divide(ONE_HUNDRED);
  }

  @Override
  public String getVATClassAbbreviationFor(final BillItem billItem) {
    return billItemsVatClassesAbbreviated
      .get(billItem)
      .toString();
  }

  @Override
  public VATClass getVATClassForAbbreviation(final Character abbreviation) {
    return vatClassAbbreviations.get(abbreviation);
  }

  @Override
  public SortedSet<Character> allFoundVATClassesAbbreviated() {
    return new TreeSet<>(vatClassAbbreviations.keySet());
  }


}