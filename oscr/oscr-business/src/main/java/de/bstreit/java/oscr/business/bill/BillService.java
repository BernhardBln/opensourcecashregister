/*
 * Open Source Cash Register
 *
 * Copyright (C) 2013-2014 Bernhard Streit
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
 * See /licenses/gpl-3.txt for a copy of the GNU GPL.
 * See /README.txt for more information about the software and the author(s).
 *
 */
package de.bstreit.java.oscr.business.bill;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.hibernate.Hibernate;

import com.google.common.annotations.VisibleForTesting;

import de.bstreit.java.oscr.business.base.date.ICurrentDateProvider;
import de.bstreit.java.oscr.business.bill.calculator.WhatToCount;
import de.bstreit.java.oscr.business.bill.dao.IBillRepository;
import de.bstreit.java.oscr.business.eventbroadcasting.EventBroadcaster;
import de.bstreit.java.oscr.business.export.IService;
import de.bstreit.java.oscr.business.offers.ExtraOffer;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.offers.PromoOffer;
import de.bstreit.java.oscr.business.offers.VariationOffer;
import de.bstreit.java.oscr.business.staff.IUserService;
import de.bstreit.java.oscr.business.staff.User;
import de.bstreit.java.oscr.business.taxation.TaxInfo;
import de.bstreit.java.oscr.business.util.DateFactory;

/**
 * For the bill management. Creates new bills, keeps one bill as "active" (i.e.
 * the one currently displayed and manipulated by a view), can return all open
 * bills (e.g. for people sitting at tables) and adds elements to the bill.
 *
 * @author Bernhard Streit
 */
@Named
public class BillService {

  private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(BillService.class);

  @Inject
  private IBillRepository billRepository;

  @Inject
  private IUserService userProvider;

  @Inject
  @Named("defaultGlobalTaxInfoForNewBills")
  private TaxInfo defaultTaxInfoForNewBills;

  @Inject
  private ICurrentDateProvider currentDateProvider;

  @Inject
  private IMultipleBillsCalculatorFactory multipleBillsCalculatorFactory;

  @Inject
  private Set<IService> services;

  @Inject
  private EventBroadcaster eventBroadcaster;

  private Bill currentBill;

  private BillItem lastAddedItem;


  private void fireBillChangedEvent() {
    eventBroadcaster.notifyBillUpdated(this, currentBill);
  }

  /**
   * Add a product offer to a bill. Creates a new bill if there is no open bill
   * available.
   *
   * @param productOffer
   * @return the bill item which was created and added to the bill with the
   *         given offer. Can be null if the item could not be added to the bill, for whatever constraints
   */
  public BillItem addProductOffer(ProductOffer productOffer) throws CannotAddItemException {


    if (currentBill != null && productOffer.getOfferedItem().isNoReduction() && currentBill.isFreePromotionOffer()) {
      // no-reduction items cannot be added to bills which are totally free
      throw new CannotAddItemException("Cannot add this item to a bill which is free/promo! Use a new bill to charge this!");
    }

    initBillIfEmpty();


    final BillItem billItem = new BillItem(productOffer);
    currentBill.addBillItem(billItem);

    saveBill();

    // fire events after lastAddedItem was changed - just in case...
    fireBillChangedEvent();

    return billItem;
  }

  public void toggleProductVariationOffer(VariationOffer variationOffer) {
    if (lastAddedItem == null) {
      return;
    }

    lastAddedItem.toggleVariationOffer(variationOffer);

    saveBill();
    fireBillChangedEvent();
  }

  public void setStaffConsumer(User consumer) {
    if (currentBill == null) {
      return;
    }

    currentBill.setStaffConsumer(consumer);

    saveBill();
    fireBillChangedEvent();
  }

  public void clearStaffConsumer() {
    if (currentBill == null) {
      return;
    }

    currentBill.clearStaffConsumer();

    saveBill();
    fireBillChangedEvent();
  }

  public void setFreePromotion() {
    if (currentBill == null) {
      return;
    }

    currentBill.setFreePromotionOffer(true);

    saveBill();
    fireBillChangedEvent();
  }


  public void clearFreePromotion() {
    if (currentBill == null) {
      return;
    }

    currentBill.setFreePromotionOffer(false);

    saveBill();
    fireBillChangedEvent();
  }

  public void toggleReduction() {
    if (currentBill == null) {
      return;
    }

    switch (currentBill.getReduction()) {

    case 0:
      currentBill.setReduction(10);
      break;

    case 10:
      currentBill.setReduction(20);
      break;

    case 20:
      currentBill.setReduction(40);
      break;

    case 40:
      currentBill.setReduction(null);
      break;

    }

    saveBill();
    fireBillChangedEvent();
  }

  public void undoLastAction() {
    if (currentBill == null) {
      return;
    }

    currentBill.undoLastAction();

    if (currentBill.isEmpty()) {
      billRepository.delete(currentBill);
      currentBill = null;
      lastAddedItem = null;
    } else {
      lastAddedItem = currentBill.getLastBillItemOrNull();
    }

    fireBillChangedEvent();
  }

  @Transactional
  public IMultipleBillsCalculator getTotalForToday() {
    final Collection<Bill> todaysBills = billRepository
        .getBillsForTodayWithoutStaff();
    return multipleBillsCalculatorFactory.create(todaysBills,
        WhatToCount.TOTAL);
  }

  @Transactional
  public IMultipleBillsCalculator getTotalForYesterday() {
    final Collection<Bill> yesterdaysBills = billRepository
        .getBillsForYesterdayWithoutStaff();
    return multipleBillsCalculatorFactory.create(yesterdaysBills,
        WhatToCount.TOTAL);
  }

  @Transactional
  public IMultipleBillsCalculator getFreePomotionTotalForToday() {
    final Collection<Bill> todaysBills = billRepository
        .getBillsForTodayWithoutStaff();
    return multipleBillsCalculatorFactory.create(todaysBills,
        WhatToCount.PROMO_TOTAL);
  }

  @Transactional
  public IMultipleBillsCalculator getFreePomotionTotalForYesterday() {
    final Collection<Bill> yesterdaysBills = billRepository
        .getBillsForYesterdayWithoutStaff();
    return multipleBillsCalculatorFactory.create(yesterdaysBills,
        WhatToCount.PROMO_TOTAL);
  }

  private void initBillIfEmpty() {
    if (currentBill == null) {
      currentBill = new Bill(defaultTaxInfoForNewBills,
          currentDateProvider.getCurrentDate());
      lastAddedItem = null;
    }
  }

  /**
   * Add an extra offer to the last added product offer on the bill.
   *
   * @param extraOffer
   * @throws NoOpenBillException
   *           when there is no open bill or the bill is empty
   */
  public void addExtraOffer(ExtraOffer extraOffer) {
    final String errorMessage = "Cannot add extra offer '" + extraOffer
        + "' - no bill available!";
    assertCurrentBillNotNull(errorMessage);
    assertCurrentBillNotEmpty(errorMessage);

    lastAddedItem.addExtraOffer(extraOffer);

    saveBill();

    fireBillChangedEvent();
  }

  /**
   * Set a product variation offer to the last added product offer on the bill.
   *
   * @param variationOffer
   * @throws NoOpenBillException
   *           when there is no open bill or the bill is empty
   */
  public void setVariationOffer(VariationOffer variationOffer) {
    final String errorMessage = "Cannot set variation '" + variationOffer
        + "' - no bill available!";

    assertCurrentBillNotNull(errorMessage);
    assertCurrentBillNotEmpty(errorMessage);

    checkNotNull(variationOffer);

    lastAddedItem.toggleVariationOffer(variationOffer);

    saveBill();
    fireBillChangedEvent();
  }

  public void setPromoOffer(PromoOffer promoOffer) {
    final String errorMessage = "Cannot set promo  offer '" + promoOffer
        + "' - no bill available!";

    assertCurrentBillNotNull(errorMessage);
    assertCurrentBillNotEmpty(errorMessage);

    checkNotNull(promoOffer);

    boolean hasAlreadyPromoOffer = lastAddedItem
        .getExtraAndVariationOffers().stream()
        .anyMatch(o -> o instanceof PromoOffer);

    if (hasAlreadyPromoOffer) {
      throw new AlreadyHasPromoOfferException();
    }

    lastAddedItem.addPromoOffer(promoOffer);

    saveBill();
    fireBillChangedEvent();
  }

  public Bill closeBill() {
    assertCurrentBillNotNull("Cannot close bill - no bill available!");

    currentBill.closeBill(userProvider.getCurrentUser(),
        currentDateProvider.getCurrentDate());

    saveBill();

    final Bill currentBillForFurtherReference = currentBill;

    currentBill = null;
    lastAddedItem = null;

    fireBillChangedEvent();

    return currentBillForFurtherReference;
  }

  private void assertCurrentBillNotNull(String errorMessage) {
    if (currentBill == null) {
      throw new NoOpenBillException(errorMessage);
    }
  }

  private void assertCurrentBillNotEmpty(final String errorMessage) {
    if (lastAddedItem == null) {
      throw new NoOpenBillException(errorMessage);
    }
  }

  private void saveBill() {
    currentBill = billRepository.save(currentBill);

    // only keep reference if saveBill was successful
    lastAddedItem = currentBill.getLastBillItemOrNull();
  }

  public void setGlobalTaxInfo(TaxInfo taxInfo) {
    // At the moment, we only support one tax info, might change in the
    // future
    assertCurrentBillNotNull("Cannot set tax info - no bill available");
    checkNotNull(taxInfo);

    if (taxInfo.equals(currentBill.getGlobalTaxInfo())) {
      return;
    }

    currentBill.setGlobalTaxInfo(taxInfo);

    saveBill();

    fireBillChangedEvent();
  }

  public TaxInfo getGlobalTaxInfo() {
    if (currentBill == null) {
      return null;
    }

    return currentBill.getGlobalTaxInfo();
  }

  @Transactional
  public void processTodaysBills(IBillProcessor billProcessor) {
    final Collection<Bill> allBillsForToday = billRepository
        .getBillsForTodayWithoutStaff();

    for (final Bill bill : allBillsForToday) {
      billProcessor.processBill(bill);
    }

  }

  @Transactional
  public void processBillsAt(IBillProcessor billProcessor, Date day) {

    final Collection<Bill> allBillsForToday = getBillsForAllDay(day);

    for (final Bill bill : allBillsForToday) {
      billProcessor.processBill(bill);
    }

  }

  public Collection<Bill> getBillsForAllDay(Date day) {
    Date from = DateFactory.getDateWithTimeMidnight(day.getYear() + 1900,
        day.getMonth() + 1, day.getDate());

    Calendar nextDayCalendar = Calendar.getInstance();
    nextDayCalendar.setTime(from);
    nextDayCalendar.add(Calendar.DAY_OF_MONTH, 1);
    Date to = nextDayCalendar.getTime();

    final Collection<Bill> allBillsForToday = billRepository
        .getBillsForDayWithoutStaff(from, to);

    return allBillsForToday;
  }

  /**
   * Get all bills from the whole month this day lies in.
   * 
   * @param day
   * @return
   */
  public Collection<Bill> getBillsForMonthOf(Date day) {
    // first of "current" month (the month that the given day lies in)
    Date firstOfThisMonth = DateFactory.getDateWithTimeMidnight(day.getYear() + 1900,
        day.getMonth() + 1, 1);

    Date firstOfNextMonth = DateFactory.getFirstOfNextMonthAtMidnight(firstOfThisMonth);

    final Collection<Bill> allBillsOfThatMonth = billRepository
        .getBillsForDayWithoutStaff(firstOfThisMonth, firstOfNextMonth);

    return allBillsOfThatMonth;
  }


  public void notifyShutdown() {
    for (IService service : services) {
      logger.info("Killing " + service);
      service.stopService();
    }
  }

  @Transactional
  public List<Bill> getOpenBills() {
    final List<Bill> openBills = billRepository.billClosedIsNull();

    initialise(openBills);

    return openBills;
  }

  private void initialise(List<Bill> openBills) {
    for (final Bill bill : openBills) {

      Hibernate.initialize(bill);

      initialise(bill.getBillItems());
    }
  }

  private void initialise(Collection<BillItem> billItems) {
    for (final BillItem billItem : billItems) {
      Hibernate.initialize(billItem);
      Hibernate.initialize(billItem.getExtraAndVariationOffers());
    }
  }

  public void newBill() {
    currentBill = null;
    lastAddedItem = null;
    fireBillChangedEvent();
  }

  public void loadBill(Bill bill) {
    currentBill = bill;
    lastAddedItem = (bill == null ? null : bill.getLastBillItemOrNull());
    fireBillChangedEvent();
  }

  @VisibleForTesting
  void setBillRepository(IBillRepository billRepository) {
    this.billRepository = billRepository;
  }

  @VisibleForTesting
  void setUserProvider(IUserService userProvider) {
    this.userProvider = userProvider;
  }

  @VisibleForTesting
  void setDefaultTaxInfoForNewBills(TaxInfo defaultTaxInfoForNewBills) {
    this.defaultTaxInfoForNewBills = defaultTaxInfoForNewBills;
  }

  @VisibleForTesting
  void setCurrentDateProvider(ICurrentDateProvider currentDateProvider) {
    this.currentDateProvider = currentDateProvider;
  }

  @VisibleForTesting
  void setMultipleBillsCalculatorFactory(IMultipleBillsCalculatorFactory multipleBillsCalculatorFactory) {
    this.multipleBillsCalculatorFactory = multipleBillsCalculatorFactory;
  }

  @VisibleForTesting
  void setServices(Set<IService> services) {
    this.services = services;
  }

  @VisibleForTesting
  void setEventBroadcaster(EventBroadcaster eventBroadcaster) {
    this.eventBroadcaster = eventBroadcaster;
  }


}
