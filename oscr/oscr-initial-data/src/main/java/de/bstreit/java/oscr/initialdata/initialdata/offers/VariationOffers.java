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
package de.bstreit.java.oscr.initialdata.initialdata.offers;

import javax.inject.Named;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.offers.VariationOffer;
import de.bstreit.java.oscr.business.products.Variation;
import de.bstreit.java.oscr.initialdata.AbstractDataContainer;
import de.bstreit.java.oscr.initialdata.initialdata.ValidityDates;
import de.bstreit.java.oscr.initialdata.initialdata.products.Variations;

@Named
public class VariationOffers extends AbstractDataContainer<VariationOffer> {

  public static final VariationOffer SOJA = create(Variations.SOJA, new Money("0.30", "EUR"));
  public static final VariationOffer LAKTOSEFREI = create(Variations.LAKTOSEFREI, new Money("0.20", "EUR"));
  public static final VariationOffer KOFFEINFREI = create(Variations.KOFFEINFREI, new Money("0.20", "EUR"));


  private static VariationOffer create(Variation variation, Money price) {
    final VariationOffer variationOffer = new VariationOffer(variation, price,
        ValidityDates.VALID_FROM_DATE, ValidityDates.VALID_TO_DATE);
    return variationOffer;
  }


  @Override
  public Class<VariationOffer> getType() {
    return VariationOffer.class;
  }

}
