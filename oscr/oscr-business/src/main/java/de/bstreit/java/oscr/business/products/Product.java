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

import de.bstreit.java.oscr.business.products.category.ProductCategory;

import javax.persistence.*;
import java.util.Date;

/**
 * @author streit
 */
@Entity
@DiscriminatorValue(value = "Product")
public class Product extends AbstractSalesItem {

  /**
   * Optional; not used in equals or hashcode
   */
  @ManyToOne(cascade = CascadeType.ALL, optional = true)
  private ContainerSize containerSize = null;

  @Column(nullable = false, columnDefinition = "BIT NOT NULL DEFAULT FALSE")
  private boolean pfand = false;

  /**
   * Product categories (drinks, food, ...) help to visualise the buttons in
   * the cash register front-end better, and also provide additional
   * information for controlling.
   * <p>
   * Can be optional so we don't force people to define categories
   */
  @ManyToOne(cascade = CascadeType.ALL, optional = true)
  private ProductCategory productCategory;

  private Product() {
    super(null, null, null);
  }

  public Product(final String name, final Date validFrom, final Date validTo) {
    super(name, validFrom, validTo);
  }

  public ProductCategory getProductCategory() {
    return productCategory;
  }

  public ContainerSize getContainerSize() {
    return containerSize;
  }

  /**
   * Optional: Packungsgröße setzen
   *
   * @param containerSize
   */
  public void setPackageSize(final ContainerSize containerSize) {
    this.containerSize = containerSize;
  }

  public void setProductCategory(final ProductCategory productCategory) {
    this.productCategory = productCategory;
  }

  public void setPfand(final boolean pfand) {
    this.pfand = pfand;
  }

  public boolean isPfand() {
    return pfand;
  }
}
