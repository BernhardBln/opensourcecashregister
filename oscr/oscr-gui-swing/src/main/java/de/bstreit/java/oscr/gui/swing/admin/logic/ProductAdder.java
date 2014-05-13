package de.bstreit.java.oscr.gui.swing.admin.logic;

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
import de.bstreit.java.oscr.business.products.Product;
import de.bstreit.java.oscr.business.products.category.ProductCategory;
import de.bstreit.java.oscr.business.products.category.dao.IProductCategoryRepository;
import de.bstreit.java.oscr.gui.swing.admin.util.ChoiceHelper;

@Named
public class ProductAdder implements IAdminBean {

	@Inject
	private IProductOfferRepository productOfferRepository;
	@Inject
	private IProductCategoryRepository productCategoryRepository;

	private Scanner scanner;

	@Override
	public void setScanner(Scanner scanner) {
		this.scanner = scanner;
	}

	@Transactional
	@Override
	public void performTask() {

		System.out.println("Add product:\n" + "============\n\n");

		final Product product = getProduct();
		final Money price = getPrice();

		final ProductOffer productOffer = new ProductOffer(product, price,
				new Date(), null);

		productOfferRepository.save(productOffer);
	}

	private Money getPrice() {

		System.out.println("Enter price: ");
		final String amount = scanner.nextLine().trim().replace(",", ".");

		System.out.println("Enter Currency [EUR]: ");
		String currencyCode = scanner.nextLine().trim();

		if (StringUtils.isBlank(currencyCode)) {
			currencyCode = "EUR";
		}
		return new Money(amount, currencyCode);
	}

	private Product getProduct() {
		System.out.println("Enter product name: ");
		final String name = scanner.nextLine().trim();

		final Product product = new Product(name, new Date(), null);

		final ProductCategory productCategory = getProductCategory();
		product.setProductCategory(productCategory);

		return product;
	}

	private ProductCategory getProductCategory() {

		final List<ProductCategory> categories = productCategoryRepository
				.findActiveProductCategories();

		final ChoiceHelper<ProductCategory> choiceHelper = ChoiceHelper
				.withoutCancelOption(categories, "Select category:", scanner);

		return choiceHelper.makeChoice();
	}

	@Override
	public String toString() {
		return "Add a product";
	}
}
