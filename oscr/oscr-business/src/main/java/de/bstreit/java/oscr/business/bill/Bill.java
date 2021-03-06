/*
 * Open Source Cash Register
 *
 * Copyright (C) 2013, 2014 Bernhard Streit
 *
 * This file is part of the Open Source Cash Register program.
 *
 * Open Source Cash Register is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Open Source Cash Register is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * --
 *
 * See oscr/licenses/gpl-3.txt for a copy of the GNU GPL.
 * See oscr/README.txt for more information about the software and the author(s).
 *
 */
package de.bstreit.java.oscr.business.bill;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import de.bstreit.java.oscr.business.offers.AbstractOffer;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.products.AbstractSalesItem;
import de.bstreit.java.oscr.business.staff.User;
import de.bstreit.java.oscr.business.taxation.TaxInfo;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author streit
 */
@Entity
public class Bill implements Iterable<BillItem> {

  @Id
  @Column(length = 36)
  private final String id = UUID
    .randomUUID()
    .toString();

  /**
   * An optional description of the bill, can help to identify an opened bill
   * (e.g. "table 3")
   */
  @Column(nullable = true)
  private String description;

  @OneToMany(cascade = CascadeType.ALL)
  private final List<BillItem> billItems = new ArrayList<>();

  /**
   * The date when the bill was opened.
   */
  @Column(nullable = false)
  private Date billOpened;

  /**
   * The date when the bill was closed and paid.
   */
  @Column(nullable = true)
  private Date billClosed;

  /**
   * The global tax info. Must not be null; default value must be set within the
   * app
   */
  // TODO [10]: check which cascade types we really need here. Same at
  // AbstractSalesItem.overridingTaxInfo
  @ManyToOne(cascade = {CascadeType.REFRESH}, optional = false)
  private TaxInfo globalTaxInfo;

  /**
   * The user who was logged in when the bill was closed. This is not
   * necessarily the person that opened the bill, but that received the payment.
   */
  @ManyToOne(optional = true)
  private User cashier;

  @ManyToOne(optional = true)
  private User internalConsumer = null;

  @Column(nullable = true)
  private Integer reduction;

  /**
   * In case the customer didn't pay for this, e.g. because of an promotion, as
   * apologies for sth or because he brought a full loyalty card.
   */
  @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private boolean freePromotionOffer = false;


  public Bill(final TaxInfo defaultGlobalTaxInfo, final Date billOpeningDate) {
    checkNotNull(defaultGlobalTaxInfo);
    setGlobalTaxInfo(defaultGlobalTaxInfo);
    billOpened = billOpeningDate;
  }

  public Bill() {
    // for spring
  }

  public void addBillItem(final BillItem item) {
    billItems.add(item);
  }

  /**
   * In case this was consumed by a member of the staff
   */
  public void setStaffConsumer(final User internalConsumer) {
    Preconditions.checkNotNull(internalConsumer);

    this.internalConsumer = internalConsumer;
  }

  public void clearStaffConsumer() {
    this.internalConsumer = null;
  }

  /**
   * @return the {@link #globalTaxInfo}
   */
  public TaxInfo getGlobalTaxInfo() {
    return globalTaxInfo;
  }

  public boolean isConsumedByStaff() {
    return internalConsumer != null;
  }

  public User getStaffConsumer() {
    return internalConsumer;
  }

  /**
   * @param globalTaxInfo the {@link #globalTaxInfo} to set
   */
  public void setGlobalTaxInfo(final TaxInfo globalTaxInfo) {
    this.globalTaxInfo = globalTaxInfo;
  }

  public void setFreePromotionOffer(final boolean freePromotionOffer) {
    this.freePromotionOffer = freePromotionOffer;
  }

  public boolean isFreePromotionOffer() {
    return freePromotionOffer;
  }

  /**
   * @return the {@link #billClosed}
   */
  public Date getBillClosed() {
    return billClosed;
  }

  /**
   * @return a flat list with all offered items, that is, all offered items, and all attached
   * variation or extra offers, without a specific order. If an item was added multiple times, it
   * is returned multiple times.
   */
  public Collection<AbstractOffer<?>> getOfferedItemsFlat() {

    Stream<ProductOffer> products = billItems
      .stream()
      .map(BillItem::getOffer);

    Stream<AbstractOffer<?>> extrasAndVariations = billItems
      .stream()
      .map(b -> b.getExtraAndVariationOffers())
      .flatMap(List::stream);

    return Stream
      .concat(products, extrasAndVariations)
      .collect(Collectors.toList());
  }

  /**
   * Invoked when an open bill is getting paid and hence closed.
   *
   * @param billClosingDate
   */
  void closeBill(final User cashier, final Date billClosingDate) {
    this.billClosed = billClosingDate;
    this.cashier = cashier;
  }

  public List<BillItem> getBillItems() {
    return ImmutableList.copyOf(billItems);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Bill other = (Bill) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }

  @Override
  public Iterator<BillItem> iterator() {
    return getBillItems().iterator();
  }

  public Date getBillOpened() {
    return billOpened;
  }

  public void undoLastAction() {
    final int lastItemIndex = billItems.size() - 1;

    final BillItem lastItem = billItems.get(lastItemIndex);

    if (lastItem.hasUndoable()) {
      lastItem.undoLastAction();
    } else {
      billItems.remove(lastItemIndex);
    }

  }

  public boolean isEmpty() {
    return billItems.isEmpty();
  }

  /**
   * @return the last bill item - or null, if there aren't any
   */
  public BillItem getLastBillItemOrNull() {
    if (billItems.isEmpty()) {
      return null;
    }

    return billItems.get(billItems.size() - 1);
  }

  public boolean hasPfand() {
    return billItems
      .stream()
      .anyMatch(b -> b
        .getOffer()
        .getOfferedItem()
        .isPfand());
  }


  public int getReduction() {
    return reduction == null ? 0 : reduction;
  }


  public void setReduction(final Integer reduction) {
    this.reduction = reduction;
  }

  public boolean hasReduction() {
    return reduction != null;
  }

}
