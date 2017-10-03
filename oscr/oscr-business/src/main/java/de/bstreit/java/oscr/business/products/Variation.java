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
package de.bstreit.java.oscr.business.products;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Date;

/**
 * A variation modifies a product (e.g.
 * "replace cow milk by soy milk and charge 0,30 EUR extra for that")
 * <p>
 * Variations overwrite the parents overriding tax info in case their overriding tax info is set.
 *
 * @author streit
 */
@Entity
@DiscriminatorValue(value = "Variation")
public class Variation extends AbstractSalesItem {

  private Variation() {
    super(null, null, null);
  }

  public Variation(final String name, final Date validFrom, final Date validTo) {
    super(name, validFrom, validTo);
  }

}
