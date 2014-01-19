package de.bstreit.java.oscr.gui.formatting;

import java.util.Locale;

import javax.inject.Named;

import org.springframework.format.number.CurrencyFormatter;

import de.bstreit.java.oscr.business.base.finance.money.Money;

@Named
public class MoneyFormatter {

  public final CurrencyFormatter numberFormat = new CurrencyFormatter();


  public MoneyFormatter() {
    numberFormat.setFractionDigits(2);
  }

  public String format(Money money) {
    numberFormat.setCurrency(money.getCurrency());
    return numberFormat.print(money.getAmount(), Locale.getDefault());
  }

}
