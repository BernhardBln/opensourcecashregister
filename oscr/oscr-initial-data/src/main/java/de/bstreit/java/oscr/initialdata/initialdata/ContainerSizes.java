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
package de.bstreit.java.oscr.initialdata.initialdata;

import javax.inject.Named;

import de.bstreit.java.oscr.business.products.ContainerSize;
import de.bstreit.java.oscr.initialdata.AbstractDataContainer;

@Named
public class ContainerSizes extends AbstractDataContainer<ContainerSize> {

  @Override
  public Class<ContainerSize> getType() {
    return ContainerSize.class;
  }


  public static final ContainerSize CUP_100_ML = create("Cup 100 ml");
  public static final ContainerSize CUP_200_ML = create("Cup 200 ml");
  public static final ContainerSize CUP_300_ML = create("Cup 300 ml");


  private static ContainerSize create(String name) {
    return new ContainerSize(name, ValidityDates.VALID_FROM_DATE, ValidityDates.VALID_TO_DATE);
  }

}
