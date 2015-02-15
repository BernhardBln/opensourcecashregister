package de.bstreit.java.oscr.business.offers;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.products.Promo;

@Entity
@DiscriminatorValue(value = "PromoOffer")
public class PromoOffer extends AbstractOffer<Promo> {

	@SuppressWarnings("unused")
	private PromoOffer() {
		this(null, null, null, null, null);
	}

	public PromoOffer(Promo promo, Money priceGross, Money costsNet,
			Date validFrom, Date validТо) {
		super(promo, priceGross, costsNet, validFrom, validТо);

		if (priceGross != null && isPositive(priceGross.getAmount())) {
			throw new IllegalArgumentException(
					"Promo offers must have a negative price.");
		}
	}

	private boolean isPositive(BigDecimal amount) {
		return amount.compareTo(BigDecimal.ZERO) >= 0;
	}

	@Override
	public String toString() {
		return getOfferedItem().getName() + " - price (gross): "
				+ getPriceGross() + " - costs (net): " + getCostsNet();
	}
}
