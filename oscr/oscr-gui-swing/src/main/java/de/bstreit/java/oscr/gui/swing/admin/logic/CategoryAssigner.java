package de.bstreit.java.oscr.gui.swing.admin.logic;

import java.util.List;
import java.util.Scanner;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import de.bstreit.java.oscr.business.products.Product;
import de.bstreit.java.oscr.business.products.category.ProductCategory;
import de.bstreit.java.oscr.business.products.category.dao.IProductCategoryRepository;
import de.bstreit.java.oscr.business.products.dao.IProductRepository;
import de.bstreit.java.oscr.gui.swing.admin.util.ChoiceHelper;
import de.bstreit.java.oscr.gui.swing.admin.util.ToString;

@Named
public class CategoryAssigner implements IAdminBean {

	@Inject
	private IProductRepository productRepository;

	@Inject
	private IProductCategoryRepository productCategoryRepository;

	private Scanner scanner;

	@Transactional
	@Override
	public void performTask() {

		final List<Product> activeProducts = productRepository
				.findActiveProducts();
		final List<ProductCategory> activeProductCategories = productCategoryRepository
				.findActiveProductCategories();

		final ChoiceHelper<Product> productChoice = ChoiceHelper
				.withoutCancelOption(activeProducts,
						"Available products (* == without category)", scanner,
						getToString());

		final ChoiceHelper<ProductCategory> categoryChoice = ChoiceHelper
				.withoutCancelOption(activeProductCategories,
						"Available Categories", scanner);

		final List<Product> products = productChoice.makeMultipleChoice();
		final ProductCategory category = categoryChoice.makeChoice();

		for (final Product product : products) {
			product.setProductCategory(category);
			productRepository.save(product);
		}
	}

	private ToString<Product> getToString() {
		return new ToString<Product>() {
			@Override
			public String toString(Product product) {
				if (product.getProductCategory() == null) {
					return product.toString() + "*";
				}
				return product.toString();
			}

		};
	}

	@Override
	public void setScanner(Scanner scanner) {
		this.scanner = scanner;
	}

	@Override
	public String toString() {
		return "Assign product category";
	}

}
