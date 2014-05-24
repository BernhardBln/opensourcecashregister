package de.bstreit.java.oscr.gui.noswing.admin.logic;

import java.util.Scanner;

import javax.inject.Inject;
import javax.inject.Named;

import de.bstreit.java.oscr.business.products.category.ProductCategory;
import de.bstreit.java.oscr.business.products.category.dao.IProductCategoryRepository;

@Named
public class CategoryAdder implements IAdminBean {

	@Inject
	private IProductCategoryRepository productCategoryRepository;

	private Scanner scanner;

	@Override
	public void performTask() {
		System.out.println("Category to add: ");
		final String category = scanner.nextLine().trim();

		System.out.print("HTML Colour: #");
		final String colour = scanner.nextLine().trim();

		final ProductCategory productCategory = new ProductCategory(category);
		productCategory.setColour(colour);

		productCategoryRepository.save(productCategory);
	}

	@Override
	public void setScanner(Scanner scanner) {
		this.scanner = scanner;
	}

	@Override
	public String toString() {
		return "Add category";
	}

}
