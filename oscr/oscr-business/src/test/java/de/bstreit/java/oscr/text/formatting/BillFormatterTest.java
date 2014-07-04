package de.bstreit.java.oscr.text.formatting;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Currency;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.bstreit.java.oscr.business.AbstractSpringTestWithContext;
import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillItem;
import de.bstreit.java.oscr.business.bill.BillService;
import de.bstreit.java.oscr.business.bill.IBillCalculatorFactory;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.offers.VariationOffer;
import de.bstreit.java.oscr.business.products.Product;
import de.bstreit.java.oscr.business.products.Variation;
import de.bstreit.java.oscr.business.taxation.TaxInfo;
import de.bstreit.java.oscr.testutils.business.bill.JUnitBillCalculatorFactory;

@Configuration
public class BillFormatterTest extends AbstractSpringTestWithContext {

	private static final TaxInfo NON_FOOD_TAX_INFO = new TaxInfo("non-food",
			null, null);
	private static final TaxInfo TO_GO_TAX_INFO = new TaxInfo("to go", null,
			null);

	@Value("#{ systemProperties['line.separator'] }")
	private String NEWLINE;

	private String sampleBill_inhouse_onlyOneVATClass_withVariation;

	@Inject
	private BillService billService;

	@Inject
	private BillFormatter billFormatter;

	@Inject
	private JUnitBillCalculatorFactory billCalculatorFactory;

	@Inject
	private Currency defaultCurrency;

	private BillItem billItem1Espresso;
	private BillItem billItem2Cappuccino;
	private BillItem billItem3Hario;
	private BillItem billItem4Cappuccino;
	private BillItem billItem5CappuccinoWithAlmondMilk;

	@Bean
	public BillFormatter getBillFormatter() {
		return new BillFormatter();
	}

	@Bean
	public IBillCalculatorFactory getBillCalculatorFactory() {
		return new JUnitBillCalculatorFactory();
	}

	@Bean(name = "togoTaxInfo")
	public TaxInfo togoTaxInfo() {
		return TO_GO_TAX_INFO;
	}

	@Bean(name = "defaultGlobalTaxInfoForNewBills")
	public TaxInfo defaultTaxInfoForNewBills() {
		return TO_GO_TAX_INFO;
	}

	@After
	public void reset() {
		billCalculatorFactory.reset();
	}

	@Test
	public void sampleBill_inhouse_onlyOneVATClass() {
		// INIT
		final ProductOffer espressoOffer = createOffer("Espresso", "1.07");
		final ProductOffer cappuccinoOffer = createOffer("Cappuccino", "2.38");
		final ProductOffer harioOffer = createOffer(
				"Hario V60 Papierfilter 01 weiß", "7.10", NON_FOOD_TAX_INFO);

		billItem1Espresso = billService.addProductOffer(espressoOffer);
		billItem2Cappuccino = billService.addProductOffer(cappuccinoOffer);
		billItem3Hario = billService.addProductOffer(harioOffer);
		billItem4Cappuccino = billService.addProductOffer(cappuccinoOffer);

		initBillCalculatorForInHouse(billItem1Espresso, billItem2Cappuccino,
				billItem3Hario, billItem4Cappuccino);

		final String expectedOutput = ""
				+ "Rechnung                    31.01.2014 12:05"
				+ NEWLINE
				+ "============================================"
				+ NEWLINE
				+ "                     Mwst.  netto*    brutto"
				+ NEWLINE
				+ "Espresso               A    0,90 €    1,07 €"
				+ NEWLINE
				+ "Cappuccino             A    2,00 €    2,38 €"
				+ NEWLINE
				+ "Hario V60              A    5,97 €    7,10 €"
				+ NEWLINE
				+ "  Papierfilter 01"
				+ NEWLINE
				+ "  weiß"
				+ NEWLINE
				+ "Cappuccino             A    2,00 €    2,38 €"
				+ NEWLINE
				+ "--------------------------------------------"
				+ NEWLINE
				+ "Gesamtsumme (brutto):                12,93 €"
				+ NEWLINE
				+ "============================================"
				+ NEWLINE
				+ NEWLINE
				+ "A - Normaler Steuersatz (19%)"
				+ NEWLINE
				+ "      netto*  10,87 €"
				+ NEWLINE
				+ "      Mwst.*   2,06 €"
				+ NEWLINE
				+ "      brutto  12,93 €"
				+ NEWLINE
				+ NEWLINE
				+ "* gerundete Beträge" + NEWLINE;

		// RUN
		final Bill bill = billService.closeBill();
		final String actualOutput = billFormatter.formatBill(bill);

		// ASSERT
		assertEquals(expectedOutput, actualOutput);
	}

	@Test
	public void sampleBill_inhouse_onlyOneVATClass_withVariation() {

		// INIT
		final ProductOffer espressoOffer = createOffer("Espresso", "1.07");
		final ProductOffer cappuccinoOffer = createOffer("Cappuccino", "2.38");
		final ProductOffer harioOffer = createOffer(
				"Hario V60 Papierfilter 01 weiß", "7.10", NON_FOOD_TAX_INFO);

		billItem1Espresso = billService.addProductOffer(espressoOffer);
		billItem2Cappuccino = billService.addProductOffer(cappuccinoOffer);
		billItem3Hario = billService.addProductOffer(harioOffer);
		billItem4Cappuccino = billService.addProductOffer(cappuccinoOffer);

		billItem5CappuccinoWithAlmondMilk = billService
				.addProductOffer(createOffer("Cappuccino", "2.38"));
		billService.toggleProductVariationOffer(createVariationOffer(
				"Almond Milk", "0.90"));

		initBillCalculatorForInHouseWithVariation(billItem1Espresso,
				billItem2Cappuccino, billItem3Hario, billItem4Cappuccino,
				billItem5CappuccinoWithAlmondMilk);

		final String expectedOutput = ""
				+ "Rechnung                    31.01.2014 12:05"
				+ NEWLINE
				+ "============================================"
				+ NEWLINE
				+ "                     Mwst.  netto*    brutto"
				+ NEWLINE
				+ "Espresso               A    0,90 €    1,07 €"
				+ NEWLINE
				+ "Cappuccino             A    2,00 €    2,38 €"
				+ NEWLINE
				+ NEWLINE
				+ "Cappuccino             A    2,00 €    2,38 €"
				+ NEWLINE
				+ " - Almond Milk         A    0,76 €    0,90 €"
				+ NEWLINE
				+ " SUBTOTAL              A    2,76 €    3,28 €"
				+ NEWLINE
				+ NEWLINE
				+ "Hario V60              A    5,97 €    7,10 €"
				+ NEWLINE
				+ "  Papierfilter 01"
				+ NEWLINE
				+ "  weiß"
				+ NEWLINE
				+ "Cappuccino             A    2,00 €    2,38 €"
				+ NEWLINE
				+ "--------------------------------------------"
				+ NEWLINE
				+ "Gesamtsumme (brutto):                12,93 €"
				+ NEWLINE
				+ "============================================"
				+ NEWLINE
				+ NEWLINE
				+ "A - Normaler Steuersatz (19%)"
				+ NEWLINE
				+ "      netto*  10,87 €"
				+ NEWLINE
				+ "      Mwst.*   2,06 €"
				+ NEWLINE
				+ "      brutto  12,93 €"
				+ NEWLINE
				+ NEWLINE
				+ "* gerundete Beträge" + NEWLINE;

		// RUN
		final Bill bill = billService.closeBill();
		final String actualOutput = billFormatter.formatBill(bill);

		// ASSERT
		assertEquals(expectedOutput, actualOutput);
	}

	private void initBillCalculatorForInHouse(final BillItem bi1,
			final BillItem bi2, final BillItem bi3, final BillItem bi4) {

		billCalculatorFactory.addVATClassAndTotalNetAndTotalGross('A',
				"Normaler Steuersatz", 19, "10.87", "12.93");

		billCalculatorFactory.setVATClassAndNetPriceFor(bi1, 'A', "0.9");
		billCalculatorFactory.setVATClassAndNetPriceFor(bi2, 'A', "2");
		billCalculatorFactory.setVATClassAndNetPriceFor(bi3, 'A', "5.97");
		billCalculatorFactory.setVATClassAndNetPriceFor(bi4, 'A', "2");

		billCalculatorFactory.setTotalGross("12.93");
	}

	private void initBillCalculatorForInHouseWithVariation(final BillItem bi1,
			final BillItem bi2, final BillItem bi3, final BillItem bi4,
			final BillItem bi5) {

		billCalculatorFactory.addVATClassAndTotalNetAndTotalGross('A',
				"Normaler Steuersatz", 19, "10.87", "12.93");

		billCalculatorFactory.setVATClassAndNetPriceFor(bi1, 'A', "0.9");
		billCalculatorFactory.setVATClassAndNetPriceFor(bi2, 'A', "2");
		billCalculatorFactory.setVATClassAndNetPriceFor(bi3, 'A', "5.97");
		billCalculatorFactory.setVATClassAndNetPriceFor(bi4, 'A', "2");
		billCalculatorFactory.setVATClassAndNetPriceFor(bi5, 'A', "2.76");

		billCalculatorFactory.setTotalGross("15.69");
	}

	@Test
	public void sampleBill_togo_twoVATClasses() {

		// INIT
		billService.setGlobalTaxInfo(TO_GO_TAX_INFO);

		initBillCalculatorForToGo(billItem1Espresso, billItem2Cappuccino,
				billItem3Hario, billItem4Cappuccino);

		final String expectedOutput = "" + //
				"Rechnung                    31.01.2014 12:05" + NEWLINE
				+ "============================================" + NEWLINE
				+ "Außer-Haus-Verzehr" + NEWLINE
				+ "                     Mwst.  netto*    brutto" + NEWLINE
				+ "Espresso               A    1,00 €    1,07 €" + NEWLINE
				+ "Cappuccino             A    2,22 €    2,38 €" + NEWLINE
				+ "Hario V60              B    5,97 €    7,10 €" + NEWLINE
				+ "  Papierfilter 01" + NEWLINE + "  weiß" + NEWLINE
				+ "Cappuccino             A    2,22 €    2,38 €" + NEWLINE
				+ "--------------------------------------------" + NEWLINE
				+ "Gesamtsumme (brutto):                12,93 €" + NEWLINE
				+ "============================================" + NEWLINE
				+ NEWLINE + "A - Ermäßigter Steuersatz (7%)" + NEWLINE
				+ "      netto*   5,44 €" + NEWLINE + "      Mwst.*   0,39 €"
				+ NEWLINE + "      brutto   5,83 €" + NEWLINE + NEWLINE
				+ "B - Normaler Steuersatz (19%)" + NEWLINE
				+ "      netto*   5,97 €" + NEWLINE + "      Mwst.*   1,13 €"
				+ NEWLINE + "      brutto   7,10 €" + NEWLINE + NEWLINE
				+ "* gerundete Beträge" + NEWLINE;

		// RUN
		final Bill bill = billService.closeBill();
		final String actualOutput = billFormatter.formatBill(bill);

		// ASSERT
		assertEquals(expectedOutput, actualOutput);
	}

	private void initBillCalculatorForToGo(final BillItem bi1,
			final BillItem bi2, final BillItem bi3, final BillItem bi4) {

		billCalculatorFactory.addVATClassAndTotalNetAndTotalGross('A',
				"Ermäßigter Steuersatz", 7, "5.44", "5.83");
		billCalculatorFactory.addVATClassAndTotalNetAndTotalGross('B',
				"Normaler Steuersatz", 19, "5.97", "7.10");

		billCalculatorFactory.setVATClassAndNetPriceFor(bi1, 'A', "1.00");
		billCalculatorFactory.setVATClassAndNetPriceFor(bi2, 'A', "2.22");
		billCalculatorFactory.setVATClassAndNetPriceFor(bi3, 'B', "5.97");
		billCalculatorFactory.setVATClassAndNetPriceFor(bi4, 'A', "2.22");

		billCalculatorFactory.setTotalGross("12.93");
	}

	private ProductOffer createOffer(String name, String price,
			TaxInfo... taxInfos) {
		final Product product = new Product(name, null, null);

		if (taxInfos != null && taxInfos.length > 0) {
			product.setOverridingTaxInfo(taxInfos[0]);
		}

		final Money priceAsMoney = new Money(new BigDecimal(price),
				defaultCurrency);
		final ProductOffer productOffer = new ProductOffer(product,
				priceAsMoney, null, null, null);

		return productOffer;
	}

	private VariationOffer createVariationOffer(String name, String price) {
		final Variation variation = new Variation(name, null, null);
		final Money pricesAsMoney = new Money(new BigDecimal(price),
				defaultCurrency);
		final VariationOffer variationOffer = new VariationOffer(variation,
				pricesAsMoney, null, null, null);

		return variationOffer;
	}
}
