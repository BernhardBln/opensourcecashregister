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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Objects;

import com.google.common.base.Preconditions;

public class Money implements Serializable {

  private static transient NumberFormat nf = NumberFormat.getCurrencyInstance();

  private final BigDecimal amount;

  private final Currency currency;


  /**
   * 
   * @param amount
   *          The amount as string, to be parsed by
   *          {@link BigDecimal#BigDecimal(String)}
   * @param currencyCode
   *          The ISO 4217 code of the currency
   */
  public Money(String amount, String currencyCode) {
    Preconditions.checkNotNull(amount);
    Preconditions.checkNotNull(currencyCode);

    this.amount = resetAmountScale(new BigDecimal(amount));

    this.currency = Currency.getInstance(currencyCode);
  }

  public Money(BigDecimal amount, Currency currency) {
    Preconditions.checkNotNull(amount);
    Preconditions.checkNotNull(currency);

    this.amount = resetAmountScale(amount);

    this.currency = currency;
  }

  /**
   * We always reset the scale, as the scale influences equals and hashcode.
   * 
   * @param amount
   * @return the amount with scale set to 2
   */
  private BigDecimal resetAmountScale(BigDecimal amount) {
    return amount.setScale(2, RoundingMode.HALF_EVEN);
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
    return Objects.hash(amount, currency);
  }

  @Override
  public boolean equals(Object obj) {
    final boolean isNull = (obj == null);
    final boolean wrongClass = !(obj instanceof Money);

    if (isNull || wrongClass) {
      return false;
    }

    final Money otherObj = (Money) obj;

    final boolean sameAmount = Objects.equals(amount, otherObj.getAmount());
    final boolean sameCurrency = Objects.equals(currency, otherObj.getCurrency());

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
}
