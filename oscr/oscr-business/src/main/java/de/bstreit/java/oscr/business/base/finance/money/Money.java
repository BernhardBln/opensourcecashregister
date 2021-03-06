/*
 * Open Source Cash Register
 *
 * Copyright (C) 2013, 2014 Bernhard Streit
 *
 * This file is part of the Open Source Cash Register program.
 *
 * Open Source Cash Register is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Open Source Cash Register is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * --------------------------------------------------------------------------
 *
 * See oscr/licenses/gpl-3.txt for a copy of the GNU GPL.
 * See oscr/README.txt for more information about the software and the author(s).
 *
 */
package de.bstreit.java.oscr.business.base.finance.money;

import com.google.common.base.Preconditions;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.math.RoundingMode.HALF_EVEN;

public class Money implements Serializable {

  private static final BigDecimal HUNDRED = new BigDecimal("100");

  private static transient NumberFormat nf = NumberFormat
    .getCurrencyInstance();

  private final BigDecimal amount;

  private final Currency currency;


  /**
   * @param amount       The amount as string, to be parsed by
   *                     {@link BigDecimal#BigDecimal(String)}
   * @param currencyCode The ISO 4217 code of the currency
   */
  public Money(final String amount, final String currencyCode) {
    this(amount, Currency.getInstance(currencyCode));
  }

  public Money(final BigDecimal amount, final Currency currency) {
    Preconditions.checkNotNull(amount);
    Preconditions.checkNotNull(currency);

    this.amount = amount.setScale(4, HALF_EVEN);
    this.currency = currency;
  }

  public Money(final String amount, final Currency currency) {
    Preconditions.checkNotNull(amount);
    Preconditions.checkNotNull(currency);

    this.amount = new BigDecimal(amount
      .trim()
      .replace(
        ",", "."))
      .setScale(4, HALF_EVEN);
    this.currency = currency;
  }

  public static Money of(final String s, final Currency c) {
    return new Money(s, c);
  }

  public static Money NULL(final Currency c) {
    return new Money("0", c);
  }


  public BigDecimal getAmount() {
    return amount;
  }

  public Currency getCurrency() {
    return currency;
  }

  @Override
  public String toString() {
    return nf.format(amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount.setScale(2, HALF_EVEN), currency);
  }

  @Override
  public boolean equals(final Object obj) {
    final boolean isNull = (obj == null);
    final boolean wrongClass = !(obj instanceof Money);

    if (isNull || wrongClass) {
      return false;
    }

    final Money otherObj = (Money) obj;

    // need to use compare for big decimal
    final boolean sameAmount = amount == null ? otherObj.getAmount() == null : amount.compareTo
      (otherObj.getAmount()) == 0;

    final boolean sameCurrency = Objects.equals(currency,
      otherObj.getCurrency());

    return sameAmount && sameCurrency;
  }

  public static String getClassname() {
    return Money.class.getName();
  }

  /**
   * Ugly - find a better way! Helps in case the system locale was changed by
   * the program.
   */
  public static void resetNumberFormatter() {
    nf = NumberFormat.getCurrencyInstance();
  }

  public Money add(final Money otherMoney) {
    checkNotNull(otherMoney);
    assertSameCurrency(otherMoney,
      "Cannot sum up two prices with different currencies!");

    final BigDecimal newValue = amount.add(otherMoney.getAmount());

    return new Money(newValue, currency);
  }

  public Money subtract(final Money otherMoney) {
    checkNotNull(otherMoney);
    assertSameCurrency(otherMoney,
      "Cannot subtract two prices with different currencies!");

    final BigDecimal newValue = amount.subtract(otherMoney.getAmount());

    return new Money(newValue, currency);
  }

  public Money multiply(final int multiplicator) {
    final BigDecimal newValue = amount.multiply(new BigDecimal(
      multiplicator));
    return new Money(newValue, currency);
  }

  public Money multiply(final BigDecimal multiplicator) {
    final BigDecimal newValue = amount.multiply(
      multiplicator);
    return new Money(newValue, currency);
  }

  /**
   * @param otherMoney
   * @param message    TODO
   */
  private void assertSameCurrency(final Money otherMoney, final String message) {
    if (!currency.equals(otherMoney.getCurrency())) {
      throw new DifferentCurrenciesException(message);
    }
  }

  /**
   * <p>
   * Assuming this to be a gross value, calculate the corresponding net value
   * for the given vatClass.
   * </p>
   * <p>
   * <pre>
   * gross = net * (1 + (vatRate / 100))
   * net = gross / (1 + (vatRate / 100))
   * </pre>
   *
   * @param vatClass
   * @return the net value of this price
   */
  public Money getNet(final VATClass vatClass) {
    final BigDecimal vatRateDivBy100 = vatClass
      .getRate()
      .divide(HUNDRED);
    final BigDecimal divider = BigDecimal.ONE.add(vatRateDivBy100);
    final BigDecimal netValue = amount
      .divide(divider, RoundingMode.HALF_UP);
    return new Money(netValue, currency);
  }

  public Money getVAT(final VATClass vatClass) {
    final BigDecimal vat = amount.subtract(getNet(vatClass).getAmount());
    return money(vat);
  }

  public Money absolute() {
    return money(amount.abs());
  }

  private Money money(final BigDecimal amount) {
    return new Money(amount, currency);
  }


}
