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

import javax.inject.Inject;

import de.bstreit.java.oscr.business.bill.dao.IBillRepository;
import de.bstreit.java.oscr.business.offers.ExtraOffer;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.offers.VariationOffer;
import de.bstreit.java.oscr.business.user.IUserService;


/**
 * 
 * @author streit
 */
public class BillService {

  @Inject
  private IBillRepository billRepository;

  @Inject
  private IUserService userProvider;

  private Bill bill;

  private BillItem lastAddedItem = null;


  /**
   * Add a product offer to a bill. Creates a new bill if there is no open bill
   * available.
   * 
   * @param productOffer
   */
  public void addProductOffer(ProductOffer productOffer) {
    if (bill == null) {
      bill = new Bill();
      lastAddedItem = null;
    }

    final BillItem billItem = new BillItem(productOffer);
    bill.addBillItem(billItem);

    lastAddedItem = billItem;
  }

  /**
   * Add an extra offer to the last added product offer on the bill.
   * 
   * @param extraOffer
   * @throws NoOpenBillException
   *           when there is no open bill or the bill is empty
   */
  public void addExtraOffer(ExtraOffer extraOffer) {
    if (bill == null || lastAddedItem == null) {
      throw new NoOpenBillException("Cannot add extra offer '" + extraOffer + "' - no open bill available!");
    }

    lastAddedItem.addExtraOffer(extraOffer);
  }

  /**
   * Set a product variation offer to the last added product offer on the bill.
   * 
   * @param variationOffer
   * @throws NoOpenBillException
   *           when there is no open bill or the bill is empty
   */
  public void setVariationOffer(VariationOffer variationOffer) {
    if (bill == null || lastAddedItem == null) {
      throw new NoOpenBillException("Cannot set variation '" + variationOffer + "' - no open bill available!");
    }

    lastAddedItem.setVariationOffer(variationOffer);
  }

}
