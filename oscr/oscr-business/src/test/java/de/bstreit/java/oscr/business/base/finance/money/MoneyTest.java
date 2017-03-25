package de.bstreit.java.oscr.business.base.finance.money;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Created by bernhard on 24.03.17.
 */
public class MoneyTest {


    @Test
    public void round_noRounding() throws Exception {
        assertEquals(bigInt("1.00"), new Money("1", "EUR").round().getAmount());
        assertEquals(bigInt("1.00"), new Money("1.0", "EUR").round().getAmount());
        assertEquals(bigInt("1.00"), new Money("1.00", "EUR").round().getAmount());

        assertEquals(bigInt("1.10"), new Money("1.1", "EUR").round().getAmount());
        assertEquals(bigInt("1.10"), new Money("1.10", "EUR").round().getAmount());

        assertEquals(bigInt("1.10"), new Money("1.1", "EUR").round().getAmount());
        assertEquals(bigInt("1.10"), new Money("1.10", "EUR").round().getAmount());

        assertEquals(bigInt("1.50"), new Money("1.5", "EUR").round().getAmount());
        assertEquals(bigInt("1.50"), new Money("1.50", "EUR").round().getAmount());

        assertEquals(bigInt("1.90"), new Money("1.9", "EUR").round().getAmount());
        assertEquals(bigInt("1.90"), new Money("1.90", "EUR").round().getAmount());

    }

    @Test
    public void round_rounding() throws Exception {
        assertEquals(bigInt("1.00"), new Money("1.001", "EUR").round().getAmount());
        assertEquals(bigInt("1.00"), new Money("1.01", "EUR").round().getAmount());
        assertEquals(bigInt("1.00"), new Money("1.04", "EUR").round().getAmount());

        // This becomes 1.10, as 1.049 (when turned into Money) already is rounded to 1.05)
        assertEquals(bigInt("1.10"), new Money("1.049", "EUR").round().getAmount());

        assertEquals(bigInt("1.10"), new Money("1.05", "EUR").round().getAmount());
        assertEquals(bigInt("1.10"), new Money("1.051", "EUR").round().getAmount());
        assertEquals(bigInt("1.10"), new Money("1.09", "EUR").round().getAmount());
        assertEquals(bigInt("1.10"), new Money("1.099", "EUR").round().getAmount());

    }

    private BigDecimal bigInt(String i) {
        return new BigDecimal(i);
    }

}