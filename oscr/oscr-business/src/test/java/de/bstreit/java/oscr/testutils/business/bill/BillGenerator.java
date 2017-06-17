package de.bstreit.java.oscr.testutils.business.bill;

import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillItem;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import lombok.RequiredArgsConstructor;

import static de.bstreit.java.oscr.testutils.TestConstants.*;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public class BillGenerator {

  private final Bill bill;

  public static BillGenerator builder() {
    return new BillGenerator(new Bill());
  }

  public Bill getBill() {
    if (bill.getGlobalTaxInfo() == null) {
      throw new IllegalStateException("no global tax info set.");
    }
    return bill;
  }

  public BillGenerator inHouse() {
    bill.setGlobalTaxInfo(IN_HOUSE);
    return this;
  }

  public BillGenerator toGo() {
    bill.setGlobalTaxInfo(TO_GO);
    return this;
  }

  public BillGenerator addEspresso() {
    return add(ESPRESSO);
  }

  public BillGenerator addCappuccino() {
    return add(CAPPUCCINO);
  }

  public BillGenerator addIcecream_alwaysReducedVAT() {
    return add(ICECREAM);
  }

  private BillGenerator add(final ProductOffer offer) {
    final BillItem billItem = new BillItem(offer);
    bill.addBillItem(billItem);
    return this;
  }

}
