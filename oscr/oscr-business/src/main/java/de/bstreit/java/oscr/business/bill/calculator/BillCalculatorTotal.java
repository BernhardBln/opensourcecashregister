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
class BillCalculatorTotal implements IBillCalculator {

  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
    .getLogger(BillCalculatorTotal.class);


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


  BillCalculatorTotal() {

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

    return bill
      .getBillItems()
      .stream()
      .map(item -> getGrossPrice(item))
      .reduce(ZERO, (p1, p2) -> p1.add(p2));
  }


  @Override
  public Money getTotalNetFor(final VATClass vatClass) {

    return bill
      .getBillItems()
      .stream()
      .filter(item -> vatClass.equals(vatFinder.getVATClassFor(item, bill)))
      .map(item -> getGrossPrice(item).getNet(vatClass))
      .reduce(ZERO, (p1, p2) -> p1.add(p2));

  }

  @Override
  public Money getTotalGrossFor(final VATClass vatClass) {

    return bill
      .getBillItems()
      .stream()
      .filter(item -> vatClass.equals(vatFinder.getVATClassFor(item, bill)))
      .map(item -> getGrossPrice(item))
      .reduce(ZERO, (p1, p2) -> p1.add(p2));

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

    final VATClass applyingVATClass = vatFinder.getVATClassFor(billItem,
      bill);

    return getGrossPrice(billItem).getNet(applyingVATClass);
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

  private Money getGrossPrice(final BillItem item) {

    final Money priceGross = item
      .getOffer()
      .getPriceGross();


    return item
      .getExtraAndVariationOffers()
      .stream()
      .filter(o -> !(o instanceof PromoOffer))
      .map(o -> o.getPriceGross())
      .reduce(priceGross, (p1, p2) -> p1.add(p2));
  }
}