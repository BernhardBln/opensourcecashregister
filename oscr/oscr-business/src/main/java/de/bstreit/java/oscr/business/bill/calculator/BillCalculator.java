package de.bstreit.java.oscr.business.bill.calculator;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillItem;
import de.bstreit.java.oscr.business.bill.IBillCalculator;
import de.bstreit.java.oscr.business.taxation.IVATFinder;

@Named
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class BillCalculator implements IBillCalculator {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(BillCalculator.class);
	@Inject
	private Currency defaultCurrency;

	@Inject
	private IVATFinder vatFinder;

	private Bill bill;
	private final Map<BillItem, Character> billItemsVatClassesAbbreviated = new HashMap<BillItem, Character>();
	private final BiMap<Character, VATClass> vatClassAbbreviations = HashBiMap
			.create();

	private Money ZERO;

	BillCalculator() {

	}

	@PostConstruct
	private void init() {
		ZERO = new Money(BigDecimal.ZERO, defaultCurrency);
	}

	/**
	 * Analyse this bill. Use the get methods to query information about this
	 * bill.
	 * 
	 * TODO: After usage, call freeResults() to clear the cache. (??)
	 * 
	 * @param bill
	 */
	void analyse(Bill bill) {
		this.bill = bill;

		char currentChar = 'A';

		for (final BillItem item : bill) {
			final VATClass vatClass = vatFinder.getVATClassFor(item, bill);

			if (!vatClassAbbreviations.values().contains(vatClass)) {
				vatClassAbbreviations.put(currentChar, vatClass);
				currentChar++;
			}

			billItemsVatClassesAbbreviated.put(item, vatClassAbbreviations
					.inverse().get(vatClass));

		}

	}

	@PreDestroy
	@Override
	public void close() {
		logger.debug("closing bill calculator");

		this.bill = null;
		vatClassAbbreviations.clear();
		billItemsVatClassesAbbreviated.clear();
		// TODO: clear cache if there is any
	}

	@Override
	public Money getTotalGross() {
		Money total = ZERO;

		for (final BillItem item : bill) {
			final Money currentPriceGross = item.getOffer().getPriceGross();
			total = total.add(currentPriceGross);

			// add variations
			if (item.getVariationOffer() != null) {
				final Money variationOfferPriceGross = item.getVariationOffer()
						.getPriceGross();
				total = total.add(variationOfferPriceGross);
			}
		}

		return total;
	}

	@Override
	public Money getTotalNetFor(VATClass vatClass) {
		Money total = ZERO;

		for (final BillItem item : bill) {
			if (vatClass.equals(vatFinder.getVATClassFor(item, bill))) {

				final Money currentPriceGross = item.getOffer().getPriceGross();
				final Money currentPriceNet = currentPriceGross
						.getNet(vatClass);
				total = total.add(currentPriceNet);

				if (item.getVariationOffer() != null) {
					final Money variationOfferPriceGross = item
							.getVariationOffer().getPriceGross();
					total = total
							.add(variationOfferPriceGross.getNet(vatClass));
				}

			}
		}

		return total;
	}

	@Override
	public Money getTotalGrossFor(VATClass vatClass) {
		Money total = ZERO;

		for (final BillItem item : bill) {
			if (vatClass.equals(vatFinder.getVATClassFor(item, bill))) {
				total = total.add(item.getOffer().getPriceGross());
				if (item.getVariationOffer() != null) {
					total = total.add(item.getVariationOffer().getPriceGross());
				}
			}
		}

		return total;
	}

	@Override
	public Money getTotalVATFor(VATClass vatClass) {

		final Money totalGross = getTotalGrossFor(vatClass);
		final Money totalNet = getTotalNetFor(vatClass);

		return totalGross.subtract(totalNet);
	}

	@Override
	public Money getNetFor(BillItem billItem) {
		if (!bill.getBillItems().contains(billItem)) {
			throw new RuntimeException("billItem not contained in bill!");
		}

		final VATClass applyingVATClass = vatFinder.getVATClassFor(billItem,
				bill);

		return billItem.getOffer().getPriceGross().getNet(applyingVATClass);
	}

	//
	// public VATClass getVATClassFor(String abbreviation) {
	// return vatClassAbbreviations.get(abbreviation);
	// }

	@Override
	public String getVATClassAbbreviationFor(BillItem billItem) {
		return billItemsVatClassesAbbreviated.get(billItem).toString();
	}

	@Override
	public VATClass getVATClassForAbbreviation(Character abbreviation) {
		return vatClassAbbreviations.get(abbreviation);
	}

	@Override
	public SortedSet<Character> allFoundVATClassesAbbreviated() {
		return new TreeSet<Character>(vatClassAbbreviations.keySet());
	}

}