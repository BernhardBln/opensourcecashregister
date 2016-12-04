package de.bstreit.java.oscr.gui.noswing.admin.logic;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.offers.dao.IProductOfferRepository;

@Named
public class ProductOutdater implements IAdminBean {

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

    System.out.println("Outdate product:\n" + "============\n\n");

    System.out.println("Choose product:");
    System.out.println();
    final ProductOffer selectedOldOffer = selectProductOffer();

    System.out.println(" -> " + selectedOldOffer);

    final Date now = new Date();

    selectedOldOffer.setValidTo(now);
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


  @Override
  public String toString() {
    return "Outdate a product";
  }
}
