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
package de.bstreit.java.oscr.business.offers.dao;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.products.category.ProductCategory;

public interface IProductOfferRepository extends
		JpaRepository<ProductOffer, Long> {

	@Query("select productOffer " + "  from ProductOffer productOffer "
			+ "  where productOffer.validTo is null"
			+ "    and productOffer.offeredItem.name = ?1"
			+ "    and productOffer.offeredItem.validTo is null")
	public ProductOffer findActiveOfferByProductName(String name);

	@Query("select productOffer " + "  from ProductOffer productOffer "
			+ "  where productOffer.validTo is null"
			+ "    and productOffer.offeredItem.productCategory = ?1"
			+ "    and productOffer.offeredItem.validTo is null")
	public Collection<ProductOffer> findActiveOffersByProductCategory(
			ProductCategory productCategory);

	@Query("select productOffer " + "  from ProductOffer productOffer "
			+ "  where productOffer.validTo is null"
			+ "    and productOffer.offeredItem.productCategory <> ?1"
			+ "    and productOffer.offeredItem.validTo is null")
	public Collection<ProductOffer> findActiveOffersByIsNotProductCategory(
			ProductCategory productCategory);

	@Query("select productOffer " + "  from ProductOffer productOffer "
			+ "  where productOffer.validTo is null"
			+ "    and productOffer.offeredItem.validTo is null")
	public Collection<ProductOffer> findAllActiveOffers();

}
