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

import static de.bstreit.java.oscr.testutils.TestConstants.*;
import static org.junit.Assert.*;

public class BillCalculatorPromoTotalTest {


  public static final Money NULL = Money.NULL(EUR);


  private BillCalculatorPromoTotal underTest = new BillCalculatorPromoTotal();

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
    assertEquals(NULL, underTest.getTotalGross());

    assertEquals(NULL, underTest.getTotalGrossFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalGrossFor(REDUCED_VAT_CLASS));

    assertEquals(NULL, underTest.getTotalNetFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalNetFor(REDUCED_VAT_CLASS));

    assertEquals(NULL, underTest.getTotalVATFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalVATFor(REDUCED_VAT_CLASS));

    final BillItem billItem = bill
      .getBillItems()
      .get(0);

    assertBillItem(billItem, null, "0");

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
    assertEquals(NULL, underTest.getTotalGross());

    assertEquals(NULL, underTest.getTotalGrossFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalGrossFor(REDUCED_VAT_CLASS));

    assertEquals(NULL, underTest.getTotalNetFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalNetFor(REDUCED_VAT_CLASS));

    assertEquals(NULL, underTest.getTotalVATFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalVATFor(REDUCED_VAT_CLASS));

    for (final BillItem billItem : bill.getBillItems()) {
      assertBillItem(billItem, null, "0");
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
    assertEquals(NULL, underTest.getTotalGross());

    assertEquals(NULL, underTest.getTotalGrossFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalGrossFor(REDUCED_VAT_CLASS));

    assertEquals(NULL, underTest.getTotalNetFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalNetFor(REDUCED_VAT_CLASS));

    assertEquals(NULL, underTest.getTotalVATFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalVATFor(REDUCED_VAT_CLASS));


    final BillItem billItem = bill
      .getBillItems()
      .get(0);

    assertBillItem(billItem, null, "0");
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
    assertEquals(NULL, underTest.getTotalGross());

    assertEquals(NULL, underTest.getTotalGrossFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalGrossFor(REDUCED_VAT_CLASS));

    assertEquals(NULL, underTest.getTotalNetFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalNetFor(REDUCED_VAT_CLASS));

    assertEquals(NULL, underTest.getTotalVATFor(NORMAL_VAT_CLASS));
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

    assertBillItem(capp1, null, "0");
    assertBillItem(espr, null, "0");
    assertBillItem(capp2, null, "0");
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
    assertEquals(NULL, underTest.getTotalGross());

    assertEquals(NULL, underTest.getTotalGrossFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalGrossFor(REDUCED_VAT_CLASS));

    // TODO: 4,37
    assertEquals(NULL, underTest.getTotalNetFor(NORMAL_VAT_CLASS));
    assertEquals(money("0"), underTest.getTotalNetFor(REDUCED_VAT_CLASS));

    assertEquals(NULL, underTest.getTotalVATFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalVATFor(REDUCED_VAT_CLASS));


    final BillItem capp1 = bill
      .getBillItems()
      .get(0);
    final BillItem ice = bill
      .getBillItems()
      .get(1);
    final BillItem capp2 = bill
      .getBillItems()
      .get(2);

    assertBillItem(capp1, null, "0");
    assertBillItem(ice, null, "0");
    assertBillItem(capp2, null, "0");
  }

  @Test
  public void testWithBill_threeItems_inHouse_mixedVatClasses_plusPfand() {
    // INIT
    final Bill bill = BillGenerator
      .builder()
      .inHouse()
      .addCappuccino()
      .addIcecream_alwaysReducedVAT()
      .addCappuccino()
      .addPfand()
      .getBill();

    // RUN
    underTest.analyse(bill);

    // ASSERT
    assertEquals(NULL, underTest.getTotalGross());

    assertEquals(NULL, underTest.getTotalGrossFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalGrossFor(REDUCED_VAT_CLASS));

    // TODO: 4,37
    assertEquals(NULL, underTest.getTotalNetFor(NORMAL_VAT_CLASS));
    assertEquals(money("0"), underTest.getTotalNetFor(REDUCED_VAT_CLASS));

    assertEquals(NULL, underTest.getTotalVATFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalVATFor(REDUCED_VAT_CLASS));


    final BillItem capp1 = bill
      .getBillItems()
      .get(0);
    final BillItem ice = bill
      .getBillItems()
      .get(1);
    final BillItem capp2 = bill
      .getBillItems()
      .get(2);
    final BillItem pfand = bill
      .getBillItems()
      .get(3);

    assertBillItem(capp1, null, "0");
    assertBillItem(ice, null, "0");
    assertBillItem(capp2, null, "0");
    assertBillItem(pfand, null, "0");
  }

  @Test
  public void testWithBill_threeItems_inHouse_mixedVatClasses_returnPfand() {
    // INIT
    final Bill bill = BillGenerator
      .builder()
      .toGo()
      .addEspresso()
      .addPfandReturn()
      .getBill();

    // RUN
    underTest.analyse(bill);

    // ASSERT
    assertEquals(NULL, underTest.getTotalGross());

    assertEquals(NULL, underTest.getTotalGrossFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalGrossFor(REDUCED_VAT_CLASS));

    assertEquals(NULL, underTest.getTotalNetFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalNetFor(REDUCED_VAT_CLASS));

    assertEquals(NULL, underTest.getTotalVATFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalVATFor(REDUCED_VAT_CLASS));


    final BillItem espr = bill
      .getBillItems()
      .get(0);
    final BillItem pfandReturn = bill
      .getBillItems()
      .get(1);

    assertBillItem(espr, null, "0");
    assertBillItem(pfandReturn, null, "0");
  }

  @Test
  public void testWithBill_threeItems_inHouse_mixedVatClasses_returnPfand_withReduction() {
    // INIT
    final Bill bill = BillGenerator
      .builder()
      .toGo()
      .addCappuccino()
      // pfand is not eligable for promo, hence won't appear in the total
      .addPfandReturn()
      .setReduction10Percent()
      .getBill();

    // RUN
    underTest.analyse(bill);

    // ASSERT
    // 2,60 * 0,1 = 0,26
    assertEquals(money("0,26"), underTest.getTotalGross());

    assertEquals(NULL, underTest.getTotalGrossFor(NORMAL_VAT_CLASS));
    assertEquals(money("0,26"), underTest.getTotalGrossFor(REDUCED_VAT_CLASS));

    assertEquals(NULL, underTest.getTotalNetFor(NORMAL_VAT_CLASS));
    assertEquals(money("0,2430"), underTest.getTotalNetFor(REDUCED_VAT_CLASS));

    assertEquals(NULL, underTest.getTotalVATFor(NORMAL_VAT_CLASS));
    assertEquals(money("0,0170"), underTest.getTotalVATFor(REDUCED_VAT_CLASS));


    final BillItem espr = bill
      .getBillItems()
      .get(0);
    final BillItem pfandReturn = bill
      .getBillItems()
      .get(1);

    assertBillItem(espr, REDUCED_VAT_CLASS, "0,2430");
    assertBillItem(pfandReturn, null, "0");
  }

  @Test
  public void testWithBill_threeItems_inHouse_mixedVatClasses_byoc_withReduction() {
    // INIT
    final Bill bill = BillGenerator
      .builder()
      .toGo()
      // bring your own cup - 10 cent off!
      .addCappuccinoWithOwnCup()
      .setReduction10Percent()
      .getBill();

    // RUN
    underTest.analyse(bill);

    // ASSERT
    // (2,60 - 0,10) * 0,1 = 0,25
    assertEquals(money("0,25"), underTest.getTotalGross());

    assertEquals(NULL, underTest.getTotalGrossFor(NORMAL_VAT_CLASS));
    assertEquals(money("0,25"), underTest.getTotalGrossFor(REDUCED_VAT_CLASS));

    assertEquals(NULL, underTest.getTotalNetFor(NORMAL_VAT_CLASS));
    assertEquals(money("0,2336"), underTest.getTotalNetFor(REDUCED_VAT_CLASS));

    assertEquals(NULL, underTest.getTotalVATFor(NORMAL_VAT_CLASS));
    assertEquals(money("0,0164"), underTest.getTotalVATFor(REDUCED_VAT_CLASS));


    final BillItem capp = bill
      .getBillItems()
      .get(0);

    assertBillItem(capp, REDUCED_VAT_CLASS, "0,2336");
  }

  @Test
  public void testWithBill_threeItems_inHouse_mixedVatClasses_byocAndWithout_withReduction() {
    // INIT
    final Bill bill = BillGenerator
      .builder()
      .toGo()
      // bring your own cup - 10 cent off!
      .addCappuccinoWithOwnCup()
      // this is not in an own cup
      .addCappuccino()
      .setReduction10Percent()
      .getBill();

    // RUN
    underTest.analyse(bill);

    // ASSERT
    // ( 2,60 + 2,60 - 0,10 ) * 0,1 = 0,51
    assertEquals(money("0,51"), underTest.getTotalGross());

    assertEquals(NULL, underTest.getTotalGrossFor(NORMAL_VAT_CLASS));
    assertEquals(money("0,51"), underTest.getTotalGrossFor(REDUCED_VAT_CLASS));

    assertEquals(NULL, underTest.getTotalNetFor(NORMAL_VAT_CLASS));
    assertEquals(money("0,4766"), underTest.getTotalNetFor(REDUCED_VAT_CLASS));

    assertEquals(NULL, underTest.getTotalVATFor(NORMAL_VAT_CLASS));
    assertEquals(money("0,0334"), underTest.getTotalVATFor(REDUCED_VAT_CLASS));


    final BillItem cappByoc = bill
      .getBillItems()
      .get(0);
    final BillItem capp = bill
      .getBillItems()
      .get(1);

    assertBillItem(cappByoc, REDUCED_VAT_CLASS, "0,2336");
    assertBillItem(capp, REDUCED_VAT_CLASS, "0,2430");
  }

  private void assertBillItem(final BillItem billItem, final VATClass vatClass,
                              final String priceNet) {

    assertEquals(money(priceNet), underTest.getNetFor(billItem));
    final Character vatClassAbbreviation = toChar(underTest.getVATClassAbbreviationFor(billItem));
    assertEquals(vatClass, underTest.getVATClassForAbbreviation(vatClassAbbreviation));
  }

  private Character toChar(final String vatClassAbbreviationFor) {
    return vatClassAbbreviationFor == null ? null : vatClassAbbreviationFor.charAt(0);
  }

  private Money money(final String price) {
    return Money.of(price, EUR);
  }


}