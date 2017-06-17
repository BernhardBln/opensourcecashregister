package de.bstreit.java.oscr.testutils;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.products.Product;
import de.bstreit.java.oscr.business.staff.User;
import de.bstreit.java.oscr.business.taxation.TaxInfo;
import de.bstreit.java.oscr.business.util.DateFactory;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

public class TestConstants {

  public static final Date PAST = DateFactory.getDateWithTimeMidnight(2017, 1, 1);
  public static final Date STILL_VALID = null;


  public static final VATClass NORMAL_VAT_CLASS = new VATClass("Normaler Steuersatz",
    BigDecimal.valueOf(19), PAST, STILL_VALID);

  public static final VATClass REDUCED_VAT_CLASS = new VATClass("Ermäßigter Steuersatz",
    BigDecimal.valueOf(7), PAST, STILL_VALID);


  public static final TaxInfo TO_GO = new TaxInfo("to go", PAST, STILL_VALID);
  public static final TaxInfo IN_HOUSE = new TaxInfo("inhouse", PAST, STILL_VALID);
  public static final TaxInfo NON_FOOD = new TaxInfo("non-food", PAST, STILL_VALID);
  public static final TaxInfo FOOD_SALE = new TaxInfo("food sale", PAST, STILL_VALID);


  public static final Currency EUR = Currency.getInstance("EUR");
  public static final User STAFF_USER = new User("John", "John Doe");

  public static final Product ESPRESSO_PROD = new Product("Espresso", PAST, STILL_VALID);

  public static final Money PRICE_ESPRESSO_GROSS = new Money("1,20", EUR);
  public static final Money COSTS_ESPRESSO_NET = new Money("0,30", EUR);

  public static final Product CAPPUCCINO_PROD = new Product("Cappuccino", PAST, STILL_VALID);
  public static final Money CAPPUCCINO_PRICE_GROSS = new Money("2,60", EUR);
  public static final Money CAPPUCCINO_COSTS_NET = new Money("0,50", EUR);

  public static final Product ICECREAM_PROD = product("Ice cream", FOOD_SALE);

  public static final Money ICECREAM_PRICE_GROSS = new Money("2,14", EUR);
  public static final Money ICECREAM_COSTS_NET = new Money("0,90", EUR);

  public static final ProductOffer ESPRESSO = new ProductOffer(ESPRESSO_PROD,
    PRICE_ESPRESSO_GROSS, COSTS_ESPRESSO_NET,
    PAST, STILL_VALID);

  public static final ProductOffer CAPPUCCINO = new ProductOffer(CAPPUCCINO_PROD,
    CAPPUCCINO_PRICE_GROSS, CAPPUCCINO_COSTS_NET,
    PAST, STILL_VALID);

  public static final ProductOffer ICECREAM = new ProductOffer(
    ICECREAM_PROD,
    ICECREAM_PRICE_GROSS,
    ICECREAM_COSTS_NET,
    PAST, STILL_VALID);

  private static Product product(final String prodName, final TaxInfo taxInfo) {
    final Product product = new Product(prodName, PAST, STILL_VALID);
    product.setOverridingTaxInfo(taxInfo);
    return product;
  }
}
