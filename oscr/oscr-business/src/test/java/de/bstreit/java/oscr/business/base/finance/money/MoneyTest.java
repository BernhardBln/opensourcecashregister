package de.bstreit.java.oscr.business.base.finance.money;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;

import static org.junit.Assert.*;

/**
 * Created by bernhard on 24.03.17.
 */
public class MoneyTest {

  public static final Currency EUR = Currency.getInstance("EUR");

  @Test
  public void testEquals() throws Exception {

    // INIT
    final BigDecimal dec1 = new BigDecimal(new BigInteger("1234"), 2).divide(BigDecimal.TEN);
    final BigDecimal dec2 = new BigDecimal(new BigInteger("1234"), 3);

    final Money m1 = new Money(dec1, EUR);
    final Money m2 = new Money(dec2, EUR);

    // RUN / ASSERT
    assertEquals(m1, m2);


  }
}