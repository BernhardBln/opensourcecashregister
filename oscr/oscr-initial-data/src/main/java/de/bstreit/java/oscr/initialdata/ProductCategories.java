package de.bstreit.java.oscr.initialdata;

import javax.inject.Named;

import de.bstreit.java.oscr.business.products.category.ProductCategory;

@Named
public class ProductCategories extends AbstractDataContainer<ProductCategory> {

	@Override
	public Class<ProductCategory> getType() {
		return ProductCategory.class;
	}

	public static final ProductCategory coffee_drinks = create("Coffee Drinks",
			"#e5bd8d");

	public static final ProductCategory HOT_DRINKS = create("Hot Drinks",
			"#ffb996");
	public static final ProductCategory COLD_DRINKS = create("Cold Drinks",
			"#96beff");

	public static final ProductCategory Food = create("Food", "#e28de5");

	public static final ProductCategory WEEKLY = create("Weekly", "#9dff96");

	private static ProductCategory create(String name, String colourCode) {
		final ProductCategory productCategory = new ProductCategory(name);
		productCategory.setColour(colourCode);
		return productCategory;
	}
}
