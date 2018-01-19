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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.bstreit.java.oscr.business.base.date.ICurrentDateProvider;
import de.bstreit.java.oscr.business.bill.calculator.WhatToCount;
import de.bstreit.java.oscr.business.bill.dao.IBillRepository;
import de.bstreit.java.oscr.business.eventbroadcasting.EventBroadcaster;
import de.bstreit.java.oscr.business.export.IService;
import de.bstreit.java.oscr.business.offers.ExtraOffer;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.offers.PromoOffer;
import de.bstreit.java.oscr.business.offers.VariationOffer;
import de.bstreit.java.oscr.business.products.category.ProductCategory;
import de.bstreit.java.oscr.business.products.category.dao.IProductCategoryRepository;
import de.bstreit.java.oscr.business.staff.IUserService;
import de.bstreit.java.oscr.business.staff.User;
import de.bstreit.java.oscr.business.taxation.TaxInfo;
import de.bstreit.java.oscr.business.util.DateFactory;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.*;

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

  @Value("${categoriesForRecup:''}")
  private String categoriesForRecupStr;
  private Set<ProductCategory> categoriesForRecup = newHashSet();

  @Value("${cupNames:''}")
  private String cupNamesStr;
  private Set<String> cupNames = newHashSet();

  @Inject
  private ICurrentDateProvider currentDateProvider;

  @Inject
  private IProductCategoryRepository productCategoryRepository;

  @Inject
  private IMultipleBillsCalculatorFactory multipleBillsCalculatorFactory;

  @Inject
  private Set<IService> services;

  @Inject
  private EventBroadcaster eventBroadcaster;

  private Bill currentBill;

  private BillItem lastAddedItem;

  private List<String> warnings = Lists.newArrayList();

  @PostConstruct
  public void initCategoriesForRecup() {
    if (StringUtils.isBlank(categoriesForRecupStr)) {
      return;
    }

    String[] categoriesForRecupStrs = StringUtils.splitByWholeSeparator(categoriesForRecupStr, ";");

    for (String categoryForRecupStr : categoriesForRecupStrs) {

      if (StringUtils.isBlank(categoryForRecupStr)) {
        continue;
      }

      ProductCategory category = productCategoryRepository.findByName(categoryForRecupStr.trim());

      if (category != null) {
        categoriesForRecup.add(category);
      }
    }
  }

  @PostConstruct
  public void initCupNames() {
    if (StringUtils.isBlank(cupNamesStr)) {
      return;
    }

    String[] cupNamesStrArray = StringUtils.splitByWholeSeparator(cupNamesStr, ";");

    cupNames = newHashSet(cupNamesStrArray);
  }

  public boolean hasWarnings() {
    return !warnings.isEmpty();
  }

  public List<String> warnings() {
    return ImmutableList.copyOf(warnings);
  }


  private void fireBillChangedEvent() {
    eventBroadcaster.notifyBillUpdated(this, currentBill);
  }

  /**
   * Add a product offer to a bill. Creates a new bill if there is no open bill
   * available.
   *
   * @param productOffer
   * @return the bill item which was created and added to the bill with the
   * given offer. Can be null if the item could not be added to the bill, for whatever constraints
   */
  public BillItem addProductOffer(final ProductOffer productOffer) throws CannotAddItemException {


    if (currentBill != null && productOffer
      .getOfferedItem()
      .isNoReduction() && currentBill.isFreePromotionOffer()) {
      // no-reduction items cannot be added to bills which are totally free
      throw new CannotAddItemException("Cannot add " + productOffer
        .getOfferedItem()
        .getName() +
        " to a bill which is free/promo! Instead, use a separate" +
        " new bill in order to add, make it NOT free and let the guest pay for it!");
    }

    initBillIfEmpty();


    final BillItem billItem = new BillItem(productOffer);
    currentBill.addBillItem(billItem);

    saveBill();

    // fire events after lastAddedItem was changed - just in case...
    fireBillChangedEvent();

    return billItem;
  }

  public void toggleProductVariationOffer(final VariationOffer variationOffer) {
    if (lastAddedItem == null) {
      return;
    }

    lastAddedItem.toggleVariationOffer(variationOffer);

    saveBill();
    fireBillChangedEvent();
  }

  public void setStaffConsumer(final User consumer) {
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
      saveBill();
    }


    checkForWarnings();

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
      warnings.clear();
    }
  }

  /**
   * Add an extra offer to the last added product offer on the bill.
   *
   * @param extraOffer
   * @throws NoOpenBillException when there is no open bill or the bill is empty
   */
  public void addExtraOffer(final ExtraOffer extraOffer) {
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
   * @throws NoOpenBillException when there is no open bill or the bill is empty
   */
  public void setVariationOffer(final VariationOffer variationOffer) {
    final String errorMessage = "Cannot set variation '" + variationOffer
      + "' - no bill available!";

    assertCurrentBillNotNull(errorMessage);
    assertCurrentBillNotEmpty(errorMessage);

    checkNotNull(variationOffer);

    lastAddedItem.toggleVariationOffer(variationOffer);

    saveBill();
    fireBillChangedEvent();
  }

  public void setPromoOffer(final PromoOffer promoOffer) {
    final String errorMessage = "Cannot set promo  offer '" + promoOffer
      + "' - no bill available!";

    assertCurrentBillNotNull(errorMessage);
    assertCurrentBillNotEmpty(errorMessage);

    checkNotNull(promoOffer);

    final boolean hasAlreadyPromoOffer = lastAddedItem
      .getExtraAndVariationOffers()
      .stream()
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
    warnings.clear();

    fireBillChangedEvent();

    return currentBillForFurtherReference;
  }

  private void assertCurrentBillNotNull(final String errorMessage) {
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

    checkForWarnings();
  }

  private void checkForWarnings() {
    warnings.clear();

    // later, make dynamic
    checkForToGoWithoutRecup();
  }

  // todo: put into modules later
  private void checkForToGoWithoutRecup() {
    if (currentBill == null || !currentBill
      .getGlobalTaxInfo()
      .getDenotation()
      .equals("to go")) {

      return;
    }

    // we have a bill with to go

    // count all products that are for recup
    long recupProducts = currentBill
      .getBillItems()
      .stream()
      .filter(bi -> categoriesForRecup.contains(bi
        .getOffer()
        .getOfferedItem()
        .getProductCategory()))
      .count();

    // count amount of recups, Pfand (our china cups), own cup and paper cups
    Map<String, Long> counts = currentBill
      .getOfferedItemsFlat()
      .stream()
      .filter(bi -> cupNames.contains(bi
        .getOfferedItem()
        .getName()))
      .collect(
        groupingBy(o -> o
            .getOfferedItem()
            .getName(),
          counting())
      );

    Long allCups = counts
      .values()
      .stream()
      .collect(summingLong(l -> l));


    if (recupProducts <= allCups) {
      return;
    }

    String warning = "There are " + recupProducts + " to go drinks, but only " + allCups + " " +
      "RECUP or other containers on the bill: " +
      counts
        .entrySet()
        .stream()
        .map(e -> e.getKey() + ": " + e.getValue())
        .collect(joining(", "));


    warnings.add(warning);

  }

  public void setGlobalTaxInfo(final TaxInfo taxInfo) {
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
  public void processTodaysBills(final IBillProcessor billProcessor) {
    final Collection<Bill> allBillsForToday = billRepository
      .getBillsForTodayWithoutStaff();

    for (final Bill bill : allBillsForToday) {
      billProcessor.processBill(bill);
    }

  }



  @Transactional
  public void processBillsAt(final IBillProcessor billProcessor, final Date day) {

    final Collection<Bill> allBillsForToday = getBillsForAllDay(day);

    for (final Bill bill : allBillsForToday) {
      billProcessor.processBill(bill);
    }

  }

  /**
   * Including tab customers and employees
   * @param billProcessor
   * @param day
   */
  @Transactional
  public void processAllBillsAt(final IBillProcessor billProcessor, final Date day) {

    final Collection<Bill> allBillsForToday = getAllBillsForAllDay(day);

    for (final Bill bill : allBillsForToday) {
      billProcessor.processBill(bill);
    }

  }

  public Collection<Bill> getBillsForAllDay(final Date day) {
    final Date from = DateFactory.getDateWithTimeMidnight(day.getYear() + 1900,
      day.getMonth() + 1, day.getDate());

    final Calendar nextDayCalendar = Calendar.getInstance();
    nextDayCalendar.setTime(from);
    nextDayCalendar.add(Calendar.DAY_OF_MONTH, 1);
    final Date to = nextDayCalendar.getTime();

    final Collection<Bill> allBillsForToday = billRepository
      .getBillsForDayWithoutStaff(from, to);

    return allBillsForToday;
  }

  /**
   * Return all bills, including staff and customers
   * @param day
   * @return
   */
  public Collection<Bill> getAllBillsForAllDay(final Date day) {
    final Date from = DateFactory.getDateWithTimeMidnight(day.getYear() + 1900,
      day.getMonth() + 1, day.getDate());

    final Calendar nextDayCalendar = Calendar.getInstance();
    nextDayCalendar.setTime(from);
    nextDayCalendar.add(Calendar.DAY_OF_MONTH, 1);
    final Date to = nextDayCalendar.getTime();

    final Collection<Bill> allBillsForToday = billRepository
      .getAllBills(from, to);

    return allBillsForToday;
  }

  /**
   * Get all bills from the whole month this day lies in.
   *
   * @param day
   * @return
   */
  public Collection<Bill> getBillsForMonthOf(final Date day) {
    // first of "current" month (the month that the given day lies in)
    final Date firstOfThisMonth = DateFactory.getDateWithTimeMidnight(day.getYear() + 1900,
      day.getMonth() + 1, 1);

    final Date firstOfNextMonth = DateFactory.getFirstOfNextMonthAtMidnight(firstOfThisMonth);

    final Collection<Bill> allBillsOfThatMonth = billRepository
      .getBillsForDayWithoutStaff(firstOfThisMonth, firstOfNextMonth);

    return allBillsOfThatMonth;
  }

  /**
   * Get all bills from the whole month this day lies in.
   *
   * @param day
   * @return
   */
  public Collection<Bill> getBillsForYear(final int year) {
    // first of "current" month (the month that the given day lies in)
    final Date firstOfThisYear = DateFactory.getDateWithTimeMidnight(year, 1, 1);
    final Date firstOfNextYear = DateFactory.getDateWithTimeMidnight(year + 1, 1, 1);


    final Collection<Bill> allBillsOfThatYear = billRepository
      .getBillsForDayWithoutStaff(firstOfThisYear, firstOfNextYear);

    return allBillsOfThatYear;
  }


  public void notifyShutdown() {
    for (final IService service : services) {
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

  private void initialise(final List<Bill> openBills) {
    for (final Bill bill : openBills) {

      Hibernate.initialize(bill);

      initialise(bill.getBillItems());
    }
  }

  private void initialise(final Collection<BillItem> billItems) {
    for (final BillItem billItem : billItems) {
      Hibernate.initialize(billItem);
      Hibernate.initialize(billItem.getExtraAndVariationOffers());
    }
  }

  public void newBill() {
    currentBill = null;
    lastAddedItem = null;
    checkForWarnings();
    fireBillChangedEvent();
  }

  public void loadBill(final Bill bill) {
    currentBill = bill;
    checkForWarnings();
    lastAddedItem = (bill == null ? null : bill.getLastBillItemOrNull());
    fireBillChangedEvent();
  }

  @VisibleForTesting
  void setBillRepository(final IBillRepository billRepository) {
    this.billRepository = billRepository;
  }

  @VisibleForTesting
  void setUserProvider(final IUserService userProvider) {
    this.userProvider = userProvider;
  }

  @VisibleForTesting
  void setDefaultTaxInfoForNewBills(final TaxInfo defaultTaxInfoForNewBills) {
    this.defaultTaxInfoForNewBills = defaultTaxInfoForNewBills;
  }

  @VisibleForTesting
  void setCurrentDateProvider(final ICurrentDateProvider currentDateProvider) {
    this.currentDateProvider = currentDateProvider;
  }

  @VisibleForTesting
  void setMultipleBillsCalculatorFactory(final IMultipleBillsCalculatorFactory multipleBillsCalculatorFactory) {
    this.multipleBillsCalculatorFactory = multipleBillsCalculatorFactory;
  }

  @VisibleForTesting
  void setServices(final Set<IService> services) {
    this.services = services;
  }

  @VisibleForTesting
  void setEventBroadcaster(final EventBroadcaster eventBroadcaster) {
    this.eventBroadcaster = eventBroadcaster;
  }


}
