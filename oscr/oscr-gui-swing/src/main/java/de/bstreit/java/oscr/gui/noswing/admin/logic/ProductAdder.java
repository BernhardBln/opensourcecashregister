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
import de.bstreit.java.oscr.business.products.Product;
import de.bstreit.java.oscr.business.products.category.ProductCategory;
import de.bstreit.java.oscr.business.products.category.dao.IProductCategoryRepository;
import de.bstreit.java.oscr.gui.noswing.admin.util.ChoiceHelper;

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

    System.out.println("Existing products:\n==================\n");

    showProductOffers();

    System.out.println("Add product:\n" + "============\n\n");

    final Product product = getProduct();
    final Money price = getPrice("price (gross)", false);
    final Money costsNet = getPrice("costs (net)", true);

    final ProductOffer productOffer = new ProductOffer(product, price,
        costsNet, new Date(), null);

    productOfferRepository.save(productOffer);
  }

  private void showProductOffers() {

    final List<ProductOffer> allActiveOffers = productOfferRepository
        .findAllActiveOffers();

    int i = 1;
    for (final ProductOffer productOffer : allActiveOffers) {
      System.out.println(i++ + ") " + productOffer + " [# " + productOffer.getOfferedItem().getOrderNumber()
          + "]");
    }
    System.out.println();

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

  private Product getProduct() {
    System.out.println("Enter product name: ");
    final String name = scanner.nextLine().trim();

    System.out.println("Enter order number: ");
    final int orderNumber = Integer.parseInt(scanner.nextLine().trim());

    final Product product = new Product(name, new Date(), null);
    product.setOrderNumber(orderNumber);

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
