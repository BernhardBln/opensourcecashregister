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

import java.math.BigDecimal;

import static de.bstreit.java.oscr.testutils.TestConstants.*;
import static org.junit.Assert.*;


public class BillCalculatorPaymentTest {

  public static final Money NULL = Money.NULL(EUR);


  private BillCalculatorPayment underTest = new BillCalculatorPayment();

  @Before
  public void setup() {
    underTest.setDefaultCurrency(TestConstants.EUR);
    underTest.setVatFinder(new MockedVATFinder());

    underTest.init();
  }

  @Test
  public void testGetPromotionPercentageForReduction() throws Exception {

    // 20% off -> should return 0.8
    assertEquals(new BigDecimal("0.8"), BillCalculatorPayment.getPromotionPercentageForReduction
      (20));

    // 40% off -> should return 0.6
    assertEquals(new BigDecimal("0.6"), BillCalculatorPayment.getPromotionPercentageForReduction
      (40));

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

    assertEquals(money("2,4299"), underTest.getTotalNetFor(REDUCED_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalNetFor(NORMAL_VAT_CLASS));

    assertEquals(money("0,1701"), underTest.getTotalVATFor(REDUCED_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalVATFor(NORMAL_VAT_CLASS));

    final BillItem billItem = bill
      .getBillItems()
      .get(0);

    assertBillItem(billItem, REDUCED_VAT_CLASS, "2,4299");

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

    assertEquals(money("7,2897"), underTest.getTotalNetFor(REDUCED_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalNetFor(NORMAL_VAT_CLASS));

    assertEquals(money("0,5103"), underTest.getTotalVATFor(REDUCED_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalVATFor(NORMAL_VAT_CLASS));

    for (final BillItem billItem : bill.getBillItems()) {
      assertBillItem(billItem, REDUCED_VAT_CLASS, "2,4299");
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

    assertEquals(money("2,1849"), underTest.getTotalNetFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalNetFor(REDUCED_VAT_CLASS));

    assertEquals(money("0,4151"), underTest.getTotalVATFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalVATFor(REDUCED_VAT_CLASS));


    final BillItem billItem = bill
      .getBillItems()
      .get(0);

    assertBillItem(billItem, NORMAL_VAT_CLASS, "2,1849");
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

    assertEquals(money("5,3782"), underTest.getTotalNetFor(NORMAL_VAT_CLASS));
    assertEquals(NULL, underTest.getTotalNetFor(REDUCED_VAT_CLASS));

    assertEquals(money("1,0218"), underTest.getTotalVATFor(NORMAL_VAT_CLASS));
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

    assertBillItem(capp1, NORMAL_VAT_CLASS, "2,1849");
    assertBillItem(espr, NORMAL_VAT_CLASS, "1,0084");
    assertBillItem(capp2, NORMAL_VAT_CLASS, "2,1849");
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

    assertEquals(money("4,3698"), underTest.getTotalNetFor(NORMAL_VAT_CLASS));
    assertEquals(money("2"), underTest.getTotalNetFor(REDUCED_VAT_CLASS));

    assertEquals(money("0,8302"), underTest.getTotalVATFor(NORMAL_VAT_CLASS));
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

    assertBillItem(capp2, NORMAL_VAT_CLASS, "2,1849");
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
    assertEquals(money("9,34"), underTest.getTotalGross());
    assertEquals(money("7,20"), underTest.getTotalGrossFor(NORMAL_VAT_CLASS));
    assertEquals(money("2,14"), underTest.getTotalGrossFor(REDUCED_VAT_CLASS));
    assertEquals(money("6,0505"), underTest.getTotalNetFor(NORMAL_VAT_CLASS));
    assertEquals(money("2"), underTest.getTotalNetFor(REDUCED_VAT_CLASS));
    assertEquals(money("1,1495"), underTest.getTotalVATFor(NORMAL_VAT_CLASS));
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
    final BillItem pfand = bill
      .getBillItems()
      .get(3);

    assertBillItem(capp1, NORMAL_VAT_CLASS, "2,1849");
    assertBillItem(ice, REDUCED_VAT_CLASS, "2,00");
    assertBillItem(capp2, NORMAL_VAT_CLASS, "2,1849");
    assertBillItem(pfand, NORMAL_VAT_CLASS, "1,6807");
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
    assertEquals(money("-0,80"), underTest.getTotalGross());
    assertEquals(money("-2"), underTest.getTotalGrossFor(NORMAL_VAT_CLASS));
    assertEquals(money("1,20"), underTest.getTotalGrossFor(REDUCED_VAT_CLASS));
    assertEquals(money("-1,6807"), underTest.getTotalNetFor(NORMAL_VAT_CLASS));
    assertEquals(money("1,1215"), underTest.getTotalNetFor(REDUCED_VAT_CLASS));
    assertEquals(money("-0,3193"), underTest.getTotalVATFor(NORMAL_VAT_CLASS));
    assertEquals(money("0,0785"), underTest.getTotalVATFor(REDUCED_VAT_CLASS));


    final BillItem espr = bill
      .getBillItems()
      .get(0);
    final BillItem pfandReturn = bill
      .getBillItems()
      .get(1);

    assertBillItem(espr, REDUCED_VAT_CLASS, "1,1215");
    assertBillItem(pfandReturn, NORMAL_VAT_CLASS, "-1,6807");
  }

  @Test
  public void testWithBill_threeItems_inHouse_mixedVatClasses_returnPfand_withReduction() {
    // INIT
    final Bill bill = BillGenerator
      .builder()
      .toGo()
      .addEspresso()
      .addPfandReturn()
      // No influence on the total!
      .setReduction10Percent()
      .getBill();

    // RUN
    underTest.analyse(bill);

    // ASSERT
    // 1,20 * 0,9 - 2 = -0,92
    assertEquals(money("-0,92"), underTest.getTotalGross());
    assertEquals(money("-2"), underTest.getTotalGrossFor(NORMAL_VAT_CLASS));
    assertEquals(money("1,08"), underTest.getTotalGrossFor(REDUCED_VAT_CLASS));
    assertEquals(money("-1,6807"), underTest.getTotalNetFor(NORMAL_VAT_CLASS));
    assertEquals(money("1,0094"), underTest.getTotalNetFor(REDUCED_VAT_CLASS));
    assertEquals(money("-0,3193"), underTest.getTotalVATFor(NORMAL_VAT_CLASS));
    assertEquals(money("0,0706"), underTest.getTotalVATFor(REDUCED_VAT_CLASS));


    final BillItem espr = bill
      .getBillItems()
      .get(0);
    final BillItem pfandReturn = bill
      .getBillItems()
      .get(1);

    assertBillItem(espr, REDUCED_VAT_CLASS, "1,0094");
    assertBillItem(pfandReturn, NORMAL_VAT_CLASS, "-1,6807");
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
    // (2,60 - 0,10) * 0,9 = 2,25
    assertEquals(money("2,25"), underTest.getTotalGross());

    assertEquals(NULL, underTest.getTotalGrossFor(NORMAL_VAT_CLASS));
    assertEquals(money("2,25"), underTest.getTotalGrossFor(REDUCED_VAT_CLASS));

    assertEquals(NULL, underTest.getTotalNetFor(NORMAL_VAT_CLASS));
    final Money m = money("2,1028");
    final Money mAct = underTest.getTotalNetFor(REDUCED_VAT_CLASS);
    assertEquals(m.getAmount(), mAct.getAmount());

    assertEquals(NULL, underTest.getTotalVATFor(NORMAL_VAT_CLASS));
    final Money m1 = money("0,1472");
    final Money m1act = underTest.getTotalVATFor(REDUCED_VAT_CLASS);
    assertEquals(m1.getAmount(), m1act.getAmount());


    final BillItem capp = bill
      .getBillItems()
      .get(0);

    assertBillItem(capp, REDUCED_VAT_CLASS, "2,1028");
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
    // (2 * 2,60 - 0,10) * 0,9 = 4,59
    assertEquals(money("4,59"), underTest.getTotalGross());

    assertEquals(NULL, underTest.getTotalGrossFor(NORMAL_VAT_CLASS));
    assertEquals(money("4,59"), underTest.getTotalGrossFor(REDUCED_VAT_CLASS));

    assertEquals(NULL, underTest.getTotalNetFor(NORMAL_VAT_CLASS));
    assertEquals(money("4,2897"), underTest.getTotalNetFor(REDUCED_VAT_CLASS));

    assertEquals(NULL, underTest.getTotalVATFor(NORMAL_VAT_CLASS));
    assertEquals(money("0,3003"), underTest.getTotalVATFor(REDUCED_VAT_CLASS));


    final BillItem cappByoc = bill
      .getBillItems()
      .get(0);
    final BillItem capp = bill
      .getBillItems()
      .get(1);

    assertBillItem(cappByoc, REDUCED_VAT_CLASS, "2,1028");
    assertBillItem(capp, REDUCED_VAT_CLASS, "2,1869");
  }


  private void assertBillItem(final BillItem billItem, final VATClass vatClass,
                              final String priceNet) {

    assertEquals(money(priceNet).getAmount(), underTest
      .getNetFor(billItem)
      .getAmount());
    final char vatClassAbbreviation = underTest
      .getVATClassAbbreviationFor(billItem)
      .charAt(0);
    assertEquals(vatClass, underTest.getVATClassForAbbreviation(vatClassAbbreviation));
  }

  private Money money(final String price) {
    return Money.of(price, EUR);
  }

}
