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
 * --------------------------------------------------------------------------
 *
 * See oscr/licenses/gpl-3.txt for a copy of the GNU GPL.
 * See oscr/README.txt for more information about the software and the author(s).
 *
 */
package de.bstreit.java.oscr.business.storage;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.base.finance.tax.dao.IVATClassRepository;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillTestFactory;
import de.bstreit.java.oscr.business.bill.dao.IBillRepository;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.offers.dao.IProductOfferRepository;
import de.bstreit.java.oscr.business.products.Product;
import de.bstreit.java.oscr.business.products.dao.IProductRepository;
import de.bstreit.java.oscr.business.taxation.TaxInfo;
import de.bstreit.java.oscr.business.taxation.dao.ITaxInfoRepository;

@Named
public class StorageService {

	@Inject
	private IProductRepository prodRepository;

	@Inject
	private IProductOfferRepository offerRepository;

	@Inject
	private IBillRepository billRepository;

	@Inject
	private ITaxInfoRepository taxInfoRepository;

	@Inject
	private IVATClassRepository vatClassRepository;

	@Inject
	private BillTestFactory billTestFactory;

	@Transactional
	public void saveSomeProductsAndOffers() {
		prodRepository.save(Products.ESPRESSO);
		offerRepository.save(Arrays.asList(ProductOffers.ESPRESSO,
				ProductOffers.CAPPUCCINO, ProductOffers.LATTE_MACCHIATO));
	}

	@Transactional
	public void saveSomeBills() {
		// hard to prevent - we can use fix times here, but the database uses
		// system time.
		// maybe modify query so it gets todays date from outside
		assertTrue("Test does only run during datetime :)",
				new Date().getHours() > 1 && new Date().getHours() < 21);

		final Calendar yesterdayOneHourEarlier = Calendar.getInstance();
		yesterdayOneHourEarlier.roll(Calendar.DAY_OF_MONTH, false);
		yesterdayOneHourEarlier.roll(Calendar.HOUR_OF_DAY, false);

		final Calendar yesterdayOneHourLater = Calendar.getInstance();
		yesterdayOneHourLater.roll(Calendar.DAY_OF_MONTH, false);
		yesterdayOneHourLater.roll(Calendar.HOUR_OF_DAY, true);

		final Calendar todayOneHourEarlier = Calendar.getInstance();
		todayOneHourEarlier.roll(Calendar.HOUR_OF_DAY, false);

		final Calendar todayOneHourLater = Calendar.getInstance();
		todayOneHourLater.roll(Calendar.HOUR_OF_DAY, true);

		final VATClass vatClass = new VATClass("Normal",
				BigDecimal.valueOf(0.19));
		vatClassRepository.save(vatClass);

		final TaxInfo taxInfo = new TaxInfo("19%", vatClass);
		final Bill billYesterdayOneHourEarlier = billTestFactory.create(
				taxInfo, yesterdayOneHourEarlier.getTime(),
				yesterdayOneHourEarlier.getTime());

		final Bill billYesterdayOneHourLater = billTestFactory.create(taxInfo,
				yesterdayOneHourLater.getTime(),
				yesterdayOneHourLater.getTime());

		final Bill billTodayOneHourEarlier = billTestFactory.create(taxInfo,
				todayOneHourEarlier.getTime(), todayOneHourEarlier.getTime());

		final Bill billTodayOneHourLater = billTestFactory.create(taxInfo,
				todayOneHourLater.getTime(), todayOneHourLater.getTime());

		taxInfoRepository.save(taxInfo);

		billRepository.save(billYesterdayOneHourEarlier);
		billRepository.save(billYesterdayOneHourLater);
		billRepository.save(billTodayOneHourEarlier);
		billRepository.save(billTodayOneHourLater);
	}

	@Transactional
	public void saveSomeOpenBills() {
		// hard to prevent - we can use fix times here, but the database uses
		// system time.
		// maybe modify query so it gets todays date from outside
		assertTrue("Test does only run during datetime :)",
				new Date().getHours() > 1 && new Date().getHours() < 21);

		final Calendar todayOneHourEarlier = Calendar.getInstance();
		todayOneHourEarlier.roll(Calendar.HOUR_OF_DAY, false);

		final VATClass vatClass = new VATClass("Normal",
				BigDecimal.valueOf(0.19));
		vatClassRepository.save(vatClass);

		final TaxInfo taxInfo = new TaxInfo("19%", vatClass, null, null);

		final Bill billTodayOneHourEarlier = billTestFactory.create(taxInfo,
				todayOneHourEarlier.getTime(), null);

		taxInfoRepository.save(taxInfo);

		billRepository.save(billTodayOneHourEarlier);
	}

	public List<Product> getProducts() {
		return prodRepository.findAll();
	}

	public List<ProductOffer> getOffers() {
		return offerRepository.findAll();
	}

	public Collection<Bill> getBillsOfToday() {
		return billRepository.getBillsForTodayWithoutStaff();
	}

	public Collection<Bill> getOpenBillsOfToday() {
		return billRepository.billClosedIsNull();
	}

	public void clearDatabase() {
		billRepository.deleteAll();
	}

}
