package de.bstreit.java.oscr.business.bill.calculator;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillItem;
import de.bstreit.java.oscr.business.taxation.MockedVATFinder;
import de.bstreit.java.oscr.testutils.TestConstants;
import de.bstreit.java.oscr.testutils.business.bill.BillGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static de.bstreit.java.oscr.testutils.TestConstants.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class BillCalculatorTotalTest {

  public static final Money NULL = Money.NULL(EUR);


  private BillCalculatorTotal underTest = new BillCalculatorTotal();

  @Before
  public void setup() {
    underTest.setDefaultCurrency(TestConstants.EUR);
    underTest.setVatFinder(new MockedVATFinder());

    underTest.init();
  }


  @Test
  public void testWithBill_oneItem_togo() {
    // INIT
    final Bill bill = BillGenerator
      .builder()
      .addCappuccino()
      .toGo()
      .getBill();

    // RUN
    underTest.analyse(bill);

    // ASSERT
    assertEquals(money("2,60"), underTest.getTotalGross());

    assertEquals(money("2,60"), underTest.getTotalGrossFor(REDUCED_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalGrossFor(NORMAL_VAT_CLASS));

    assertEquals(money("2,43"), underTest.getTotalNetFor(REDUCED_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalNetFor(NORMAL_VAT_CLASS));

    assertEquals(money("0,17"), underTest.getTotalVATFor(REDUCED_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalVATFor(NORMAL_VAT_CLASS));

    final BillItem billItem = bill
      .getBillItems()
      .get(0);

    assertBillItem(billItem, REDUCED_VAT_CLASS, "2,43");

  }

  @Test
  public void testWithBill_threeItems_togo() {
    // INIT
    final Bill bill = BillGenerator
      .builder()
      .addCappuccino()
      .addCappuccino()
      .addCappuccino()
      .toGo()
      .getBill();

    // RUN
    underTest.analyse(bill);

    // ASSERT
    assertEquals(money("7,80"), underTest.getTotalGross());

    assertEquals(money("7,80"), underTest.getTotalGrossFor(REDUCED_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalGrossFor(NORMAL_VAT_CLASS));

    assertEquals(money("7,29"), underTest.getTotalNetFor(REDUCED_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalNetFor(NORMAL_VAT_CLASS));

    assertEquals(money("0,51"), underTest.getTotalVATFor(REDUCED_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalVATFor(NORMAL_VAT_CLASS));

    for (final BillItem billItem : bill.getBillItems()) {
      assertBillItem(billItem, REDUCED_VAT_CLASS, "2,43");
    }

  }


  @Test
  public void testWithBill_oneItem_inHouse() {
    // INIT
    final Bill bill = BillGenerator
      .builder()
      .addCappuccino()
      .inHouse()
      .getBill();

    // RUN
    underTest.analyse(bill);

    // ASSERT
    assertEquals(money("2,60"), underTest.getTotalGross());

    assertEquals(money("2,60"), underTest.getTotalGrossFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalGrossFor(REDUCED_VAT_CLASS));

    assertEquals(money("2,18"), underTest.getTotalNetFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalNetFor(REDUCED_VAT_CLASS));

    assertEquals(money("0,42"), underTest.getTotalVATFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalVATFor(REDUCED_VAT_CLASS));


    final BillItem billItem = bill
      .getBillItems()
      .get(0);

    assertBillItem(billItem, NORMAL_VAT_CLASS, "2,18");
  }

  @Test
  public void testWithBill_threeItems_inHouse() {
    // INIT
    final Bill bill = BillGenerator
      .builder()
      .addCappuccino()
      .addEspresso()
      .addCappuccino()
      .inHouse()
      .getBill();

    // RUN
    underTest.analyse(bill);

    // ASSERT
    assertEquals(money("6,40"), underTest.getTotalGross());

    assertEquals(money("6,40"), underTest.getTotalGrossFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalGrossFor(REDUCED_VAT_CLASS));

    assertEquals(money("5,37"), underTest.getTotalNetFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalNetFor(REDUCED_VAT_CLASS));

    assertEquals(money("1,03"), underTest.getTotalVATFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalVATFor(REDUCED_VAT_CLASS));


    final BillItem capp1 = bill
      .getBillItems()
      .get(0);
    final BillItem espr = bill
      .getBillItems()
      .get(1);
    final BillItem capp2 = bill
      .getBillItems()
      .get(2);

    assertBillItem(capp1, NORMAL_VAT_CLASS, "2,18");
    assertBillItem(espr, NORMAL_VAT_CLASS, "1,01");
    assertBillItem(capp2, NORMAL_VAT_CLASS, "2,18");
  }

  @Test
  public void testWithBill_threeItems_inHouse_mixedVatClasses() {
    // INIT
    final Bill bill = BillGenerator
      .builder()
      .inHouse()
      .addCappuccino()
      .addIcecream_alwaysReducedVAT()
      .addCappuccino()
      .getBill();

    // RUN
    underTest.analyse(bill);

    // ASSERT
    assertEquals(money("7,34"), underTest.getTotalGross());

    assertEquals(money("5,20"), underTest.getTotalGrossFor(NORMAL_VAT_CLASS));
    assertEquals(money("2,14"), underTest.getTotalGrossFor(REDUCED_VAT_CLASS));

    // TODO: 4,37
    assertEquals(money("4,36"), underTest.getTotalNetFor(NORMAL_VAT_CLASS));
    assertEquals(money("2"), underTest.getTotalNetFor(REDUCED_VAT_CLASS));

    assertEquals(money("0,84"), underTest.getTotalVATFor(NORMAL_VAT_CLASS));
    assertEquals(money("0,14"), underTest.getTotalVATFor(REDUCED_VAT_CLASS));


    final BillItem capp1 = bill
      .getBillItems()
      .get(0);
    final BillItem ice = bill
      .getBillItems()
      .get(1);
    final BillItem capp2 = bill
      .getBillItems()
      .get(2);

    assertBillItem(capp1, NORMAL_VAT_CLASS, "2,18");
    assertBillItem(ice, REDUCED_VAT_CLASS, "2,00");
    assertBillItem(capp2, NORMAL_VAT_CLASS, "2,18");
  }

  private void assertBillItem(final BillItem billItem, final VATClass vatClass,
                              final String priceNet) {

    assertEquals(money(priceNet), underTest.getNetFor(billItem));
    final char vatClassAbbreviation = underTest
      .getVATClassAbbreviationFor(billItem)
      .charAt(0);
    assertEquals(vatClass, underTest.getVATClassForAbbreviation(vatClassAbbreviation));
  }

  private Money money(final String price) {
    return Money.of(price, EUR);
  }


}