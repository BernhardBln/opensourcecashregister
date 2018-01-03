package de.bstreit.java.oscr.business.bill;

import de.bstreit.java.oscr.business.offers.AbstractOffer;
import de.bstreit.java.oscr.business.offers.ExtraOffer;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.offers.VariationOffer;
import de.bstreit.java.oscr.business.products.Extra;
import de.bstreit.java.oscr.business.products.Product;
import de.bstreit.java.oscr.business.products.Variation;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class BillTest {

  @Test
  public void getOfferedItemsFlat() throws Exception {

    // INIT
    Bill bill = new Bill();

    bill.addBillItem(billItem("Cappuccino"));
    bill.addBillItem(billItem("Cappuccino", extraOffer("Sirup"), variationOffer("Soy milk")));

    bill.addBillItem(billItem("Latte", extraOffer("Sirup")));

    bill.addBillItem(billItem("Espresso"));

    // RUN
    Collection<AbstractOffer<?>> offeredItemsFlat = bill.getOfferedItemsFlat();

    // ASSERT
    assertEquals(7, offeredItemsFlat.size());

    assertEquals(2,
      offeredItemsFlat
        .stream()
        .filter(b -> b
          .getOfferedItem()
          .getName()
          .equals("Cappuccino"))
        .count());

    assertEquals(1,
      offeredItemsFlat
        .stream()
        .filter(b -> b
          .getOfferedItem()
          .getName()
          .equals("Espresso"))
        .count());

    assertEquals(1,
      offeredItemsFlat
        .stream()
        .filter(b -> b
          .getOfferedItem()
          .getName()
          .equals("Latte"))
        .count());

    assertEquals(2,
      offeredItemsFlat
        .stream()
        .filter(b -> b
          .getOfferedItem()
          .getName()
          .equals("Sirup"))
        .count());

    assertEquals(1,
      offeredItemsFlat
        .stream()
        .filter(b -> b
          .getOfferedItem()
          .getName()
          .equals("Soy milk"))
        .count());
  }

  private ExtraOffer extraOffer(String name) {
    return new ExtraOffer(extra(name), null, null, null, null);
  }

  private Extra extra(String name) {
    return new Extra(name, null, null);
  }

  private VariationOffer variationOffer(String name) {
    return new VariationOffer(variation(name), null, null, null, null);
  }

  private Variation variation(String name) {
    return new Variation(name, null, null);
  }

  private BillItem billItem(String name, AbstractOffer... abstractOffers) {
    BillItem billItem = new BillItem(offer(name));

    for (AbstractOffer a : abstractOffers) {
      if (a instanceof ExtraOffer) {
        billItem.addExtraOffer((ExtraOffer) a);
      }
      if (a instanceof VariationOffer) {
        billItem.toggleVariationOffer((VariationOffer) a);
      }
    }

    return billItem;
  }

  private ProductOffer offer(String name) {
    return new ProductOffer(product(name), null, null, null, null);
  }

  private Product product(String name) {
    return new Product(name, null, null);
  }
}