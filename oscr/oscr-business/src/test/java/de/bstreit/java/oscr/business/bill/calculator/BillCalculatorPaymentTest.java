package de.bstreit.java.oscr.business.bill.calculator;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;


public class BillCalculatorPaymentTest {

  @Test
  public void testGetPromotionPercentageForReduction() throws Exception {

    // 20% off -> should return 0.8
    assertEquals(new BigDecimal("0.8"), BillCalculatorPayment.getPromotionPercentageForReduction(20));

    // 40% off -> should return 0.6
    assertEquals(new BigDecimal("0.6"), BillCalculatorPayment.getPromotionPercentageForReduction(40));

  }
}
