package de.bstreit.java.oscr.business.base.finance.money;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

/**
 * Created by bernhard on 24.03.17.
 */
public class MoneyTest {


  @Test
  public void round_noRounding() throws Exception {
    assertEquals(bigInt("1.00"), new Money("1", "EUR").roundToTenCents().getAmount());
    assertEquals(bigInt("1.00"), new Money("1.0", "EUR").roundToTenCents().getAmount());
    assertEquals(bigInt("1.00"), new Money("1.00", "EUR").roundToTenCents().getAmount());

    assertEquals(bigInt("1.10"), new Money("1.1", "EUR").roundToTenCents().getAmount());
    assertEquals(bigInt("1.10"), new Money("1.10", "EUR").roundToTenCents().getAmount());

    assertEquals(bigInt("1.10"), new Money("1.1", "EUR").roundToTenCents().getAmount());
    assertEquals(bigInt("1.10"), new Money("1.10", "EUR").roundToTenCents().getAmount());

    assertEquals(bigInt("1.50"), new Money("1.5", "EUR").roundToTenCents().getAmount());
    assertEquals(bigInt("1.50"), new Money("1.50", "EUR").roundToTenCents().getAmount());

    assertEquals(bigInt("1.90"), new Money("1.9", "EUR").roundToTenCents().getAmount());
    assertEquals(bigInt("1.90"), new Money("1.90", "EUR").roundToTenCents().getAmount());

  }

  @Test
  public void round_rounding() throws Exception {
    assertEquals(bigInt("1.00"), new Money("1.001", "EUR").roundToTenCents().getAmount());
    assertEquals(bigInt("1.00"), new Money("1.01", "EUR").roundToTenCents().getAmount());
    assertEquals(bigInt("1.00"), new Money("1.04", "EUR").roundToTenCents().getAmount());

    // This becomes 1.10, as 1.049 (when turned into Money) already is rounded
    // to 1.05)
    assertEquals(bigInt("1.10"), new Money("1.049", "EUR").roundToTenCents().getAmount());

    assertEquals(bigInt("1.10"), new Money("1.05", "EUR").roundToTenCents().getAmount());
    assertEquals(bigInt("1.10"), new Money("1.051", "EUR").roundToTenCents().getAmount());
    assertEquals(bigInt("1.10"), new Money("1.09", "EUR").roundToTenCents().getAmount());
    assertEquals(bigInt("1.10"), new Money("1.099", "EUR").roundToTenCents().getAmount());

  }

  private BigDecimal bigInt(String i) {
    return new BigDecimal(i);
  }

}