package de.bstreit.java.oscr.gui.formatting;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.bstreit.java.oscr.business.base.date.ICurrentDateProvider;
import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.base.finance.tax.dao.IVATClassRepository;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillCalculator;
import de.bstreit.java.oscr.business.bill.BillService;
import de.bstreit.java.oscr.business.bill.dao.IBillRepository;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.products.Product;
import de.bstreit.java.oscr.business.taxation.IVATFinder;
import de.bstreit.java.oscr.business.taxation.SimpleVATFinderDoNotUseInProduction;
import de.bstreit.java.oscr.business.taxation.TaxInfo;
import de.bstreit.java.oscr.business.taxation.dao.ITaxInfoRepository;
import de.bstreit.java.oscr.business.user.IUserService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BillFormatterTest.class })
@Configuration
public class BillFormatterTest {

  private static final TaxInfo NON_FOOD_TAX_INFO = new TaxInfo("non-food", null, null);
  private static final TaxInfo TO_GO_TAX_INFO = new TaxInfo("to go", null, null);

  @Value("#{ systemProperties['line.separator'] }")
  private String NEWLINE;

  @Inject
  private BillService billService;

  @Inject
  private BillFormatter billFormatter;


  @Test
  public void sampleBill_onlyOneVATClass() {
    // INIT
    final ProductOffer espressoOffer = createOffer("Espresso", "1.07");
    final ProductOffer cappuccinoOffer = createOffer("Cappuccino", "2.38");
    final ProductOffer harioOffer = createOffer("Hario V60 Papierfilter 01 weiß", "7.10", NON_FOOD_TAX_INFO);

    billService.addProductOffer(espressoOffer);
    billService.addProductOffer(cappuccinoOffer);
    billService.addProductOffer(harioOffer);
    billService.addProductOffer(cappuccinoOffer);

    final String expectedOutput = "" +
        "Rechnung                    31.01.2014 12:05" + NEWLINE +
        "============================================" + NEWLINE +
        "                     Mwst.  netto     brutto" + NEWLINE +
        "Espresso               A    0,90 €    1,07 €" + NEWLINE +
        "Cappuccino             A    2,00 €    2,38 €" + NEWLINE +
        "Hario V60              A    5,97 €    7,10 €" + NEWLINE +
        "  Papierfilter 01" + NEWLINE +
        "  weiß" + NEWLINE +
        "Cappuccino             A    2,00 €    2,38 €" + NEWLINE +
        "--------------------------------------------" + NEWLINE +
        "Gesamtsumme (brutto):                12,93 €" + NEWLINE +
        "============================================" + NEWLINE +
        NEWLINE +
        "A - Normaler Steuersatz (19%)" + NEWLINE +
        "   netto   10,87 €" + NEWLINE +
        "   Mwst.    2,06 €" + NEWLINE +
        "   brutto  12,93 €" + NEWLINE;

    // RUN
    final Bill bill = billService.closeBill();
    final String actualOutput = billFormatter.formatBill(bill);

    // ASSERT
    assertEquals(expectedOutput, actualOutput);
  }


  @Test
  public void sampleBill_twoVATClasses() {
    // INIT
    final ProductOffer espressoOffer = createOffer("Espresso", "1.07");
    final ProductOffer cappuccinoOffer = createOffer("Cappuccino", "2.38");
    final ProductOffer harioOffer = createOffer("Hario V60 Papierfilter 01 weiß", "7.10", NON_FOOD_TAX_INFO);

    billService.addProductOffer(espressoOffer);
    billService.addProductOffer(cappuccinoOffer);
    billService.addProductOffer(harioOffer);
    billService.addProductOffer(cappuccinoOffer);

    final String expectedOutput = "Rechnung                   31.01.2014 12:05" + NEWLINE +
        "===========================================" + NEWLINE +
        "                    Mwst.  netto     brutto" + NEWLINE +
        "Espresso              A    0,90 €    1,07 €" + NEWLINE +
        "Cappuccino            A    2,00 €    2,38 €" + NEWLINE +
        "Hario V60             B    5,97 €    7,10 €" + NEWLINE +
        "  Papierfilter 01" + NEWLINE +
        "  weiß   " + NEWLINE +
        NEWLINE +
        "Cappuccino           A     2,00 €    2,38 €" + NEWLINE +
        NEWLINE +
        "Gesamtsumme (brutto):               12,93 €" + NEWLINE +
        NEWLINE +
        "----------------------------------------" + NEWLINE +
        NEWLINE +
        "A - Ermäßigter Steuersatz (7%)" + NEWLINE +
        "   netto   10,87 €" + NEWLINE +
        "   Mwst.    2,06 €" + NEWLINE +
        "   brutto  12,93 €" + NEWLINE +
        NEWLINE +
        "B - Normaler Steuersatz (19%)" + NEWLINE +
        "   netto   10,87 €" + NEWLINE +
        "   Mwst.    2,06 €" + NEWLINE +
        "   brutto  12,93 €" + NEWLINE;

    // // RUN
    // final Bill bill = billService.closeBill();
    // final String actualOutput = billFormatter.formatBill(bill);
    //
    // // ASSERT
    // assertEquals(expectedOutput, actualOutput);
  }

  private ProductOffer createOffer(String name, String price, TaxInfo... taxInfos) {
    final Product product = new Product(name, null, null);

    if (taxInfos != null && taxInfos.length > 0) {
      product.setOverridingTaxInfo(taxInfos[0]);
    }

    final Money priceAsMoney = new Money(new BigDecimal(price), getDefaultCurrency());
    final ProductOffer productOffer = new ProductOffer(product, priceAsMoney, null, null);

    return productOffer;
  }

  @Bean
  public Locale getLocale() {
    return Locale.GERMANY;
  }

  @Bean
  public Currency getDefaultCurrency() {
    return Currency.getInstance(getLocale());
  }

  @Bean
  public TaxInfo getDefaultTaxInfoForNewBills() {
    return new TaxInfo("In-house", null, null);
  }

  @Bean
  public ICurrentDateProvider getCurrentDateProvider() throws ParseException {
    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, getLocale());
    System.out.println(df.format(new Date()));
    final Date date1 = df.parse("31.01.2014 12:01:00");
    final Date date2 = df.parse("31.01.2014 12:05:00");
    return new FixDateProvider(date1, date2);
  }

  @Bean
  public BillService getBillService() {
    return new BillService();
  }

  @Bean
  public BillFormatter getBillFormatter() {
    return new BillFormatter();
  }

  @Bean
  public BillCalculator getBillCalculator() {
    return new BillCalculator();
  }

  @Bean
  public IVATFinder getSimpleVATFinder() {
    return new SimpleVATFinderDoNotUseInProduction();
  }

  @Bean
  public IBillRepository getMockedBillRepository() {
    final IBillRepository mock = mock(IBillRepository.class);
    // pass through argument
    when(mock.save(Mockito.any(Bill.class))).then(new Answer<Bill>() {

      @Override
      public Bill answer(InvocationOnMock invocation) throws Throwable {
        return (Bill) invocation.getArguments()[0];
      }

    });

    return mock;
  }

  @Bean
  public IUserService getMockedUserProvider() {
    return mock(IUserService.class);
  }

  @Bean
  public ITaxInfoRepository getMockedTaxInfoRepository() {
    final ITaxInfoRepository mock = mock(ITaxInfoRepository.class);
    when(mock.findByDenotationAndValidToIsNull("non-food")).thenReturn(NON_FOOD_TAX_INFO);
    when(mock.findByDenotationAndValidToIsNull("to go")).thenReturn(TO_GO_TAX_INFO);
    return mock;
  }

  @Bean
  public IVATClassRepository getMockedVATClassRepository() {
    final IVATClassRepository mock = mock(IVATClassRepository.class);
    when(mock.findByDesignationAndValidToIsNull("Normaler Steuersatz")).thenReturn(
        new VATClass("Normaler Steuersatz", new BigDecimal("19"), null, null));
    when(mock.findByDesignationAndValidToIsNull("Ermäßigter Steuersatz")).thenReturn(
        new VATClass("Ermäßigter Steuersatz", new BigDecimal("7"), null, null));
    return mock;
  }

}
