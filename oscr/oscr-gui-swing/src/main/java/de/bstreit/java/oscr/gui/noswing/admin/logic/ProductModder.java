package de.bstreit.java.oscr.gui.noswing.admin.logic;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.offers.dao.IProductOfferRepository;

@Named
public class ProductModder implements IAdminBean {

	@Inject
	private IProductOfferRepository productOfferRepository;

	private Scanner scanner;

	@Override
	public void setScanner(Scanner scanner) {
		this.scanner = scanner;
	}

	@Transactional
	@Override
	public void performTask() {

		System.out.println("Modify product:\n" + "============\n\n");

		System.out.println("Choose product:");
		System.out.println();
		final ProductOffer selectedOldOffer = selectProductOffer();

		System.out.println(" -> " + selectedOldOffer);

		final Money price = getPrice("price (gross)", false);
		final Money costsNet = getPrice("costs (net)", true);

		final Date changeDate = new Date();

		final ProductOffer newProductOffer = new ProductOffer(
				selectedOldOffer.getOfferedItem(), price, costsNet, changeDate,
				null);

		selectedOldOffer.setValidTo(changeDate);

		productOfferRepository.save(newProductOffer);
		productOfferRepository.save(selectedOldOffer);
	}

	private ProductOffer selectProductOffer() {

		final List<ProductOffer> allActiveOffers = productOfferRepository
				.findAllActiveOffers();

		int i = 1;
		for (final ProductOffer productOffer : allActiveOffers) {
			System.out.println(i++ + ") " + productOffer);
		}
		System.out.println();
		System.out.println("0) Exit");

		final int choice = Integer.valueOf(scanner.nextLine().trim());

		if (choice == 0) {
			throw new AbortedException();
		}

		return allActiveOffers.get(choice - 1);
	}

	private Money getPrice(String label, boolean isOptional) {

		final String leaveEmptyOpt = isOptional ? " (Press enter to leave empty)"
				: "";

		System.out.println("Enter " + label + leaveEmptyOpt + ": ");
		final String amount = scanner.nextLine().trim().replace(",", ".");

		if (isOptional && StringUtils.isBlank(amount)) {
			return null;
		}

		System.out.println("Enter Currency [EUR]: ");
		String currencyCode = scanner.nextLine().trim();

		if (StringUtils.isBlank(currencyCode)) {
			currencyCode = "EUR";
		}
		return new Money(amount, currencyCode);
	}

	@Override
	public String toString() {
		return "Modify a product";
	}
}
