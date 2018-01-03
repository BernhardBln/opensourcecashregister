package de.bstreit.java.oscr.text.formatting;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import org.springframework.format.number.CurrencyFormatter;

import javax.inject.Named;
import java.math.RoundingMode;
import java.util.Locale;

@Named
public class MoneyFormatter {

  public final CurrencyFormatter numberFormat = new CurrencyFormatter();


  public MoneyFormatter() {
    numberFormat.setFractionDigits(2);
    numberFormat.setRoundingMode(RoundingMode.HALF_UP);
  }

  public String format(final Money money) {
    numberFormat.setCurrency(money.getCurrency());
    return numberFormat.print(money.getAmount(), Locale.getDefault());
  }

}
