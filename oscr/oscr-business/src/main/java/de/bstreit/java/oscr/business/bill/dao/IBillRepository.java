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
package de.bstreit.java.oscr.business.bill.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.bstreit.java.oscr.business.bill.Bill;

public interface IBillRepository extends JpaRepository<Bill, String> {

	/**
	 * @return all bills opened today, including promotional offers
	 *         ("free drinks") but excluding staff consumption
	 */
	@Query("from Bill where billOpened >= current_date and internalConsumer is NULL ORDER BY billOpened DESC")
	public Collection<Bill> getBillsForTodayWithoutStaff();

	/**
	 * @return all bills opened yesterday, including promotional offers
	 *         ("free drinks") but excluding staff consumption
	 */
	@Query("from Bill where billOpened >= current_date - 1 and internalConsumer is NULL AND billOpened < current_date ORDER BY billOpened DESC")
	public Collection<Bill> getBillsForYesterdayWithoutStaff();

	@Query("from Bill where billOpened >= current_date and internalConsumer is NULL AND freePromotionOffer = TRUE order by billOpened desc")
	public Collection<Bill> getPromotionBillsForTodayWithoutStaff();

	@Query("from Bill where billOpened >= current_date - 1 and internalConsumer is NULL AND billOpened < current_date AND freePromotionOffer = TRUE")
	public Collection<Bill> getPromotionBillsForYesterdayWithoutStaff();

	public List<Bill> billClosedIsNull();

	/**
	 *
	 * @param from
	 * @param to
	 * @return all bills opened on or after the from date and before the to date
	 */
	@Query("from Bill where billOpened >= ?1 AND billOpened < ?2 AND internalConsumer is NOT NULL")
	public Collection<Bill> getBillsForStaff(Date from, Date to);

	@Query("from Bill where billOpened >= ?1 and billOpened < ?2 and internalConsumer is NULL order by billOpened desc")
	public Collection<Bill> getBillsForDayWithoutStaff(Date from, Date to);

	@Query("FROM Bill WHERE billOpened >= ?1 AND billOpened < ?2 AND internalConsumer IS NULL AND freePromotionOffer = TRUE")
	public Collection<Bill> getPromotionBillsForDayWithoutStaff(Date from,
			Date to);

}
