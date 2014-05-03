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

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.NaturalId;

import de.bstreit.java.oscr.business.base.ILabelledItem;
import de.bstreit.java.oscr.business.base.persistence.AbstractPersistentObjectWithContinuance;
import de.bstreit.java.oscr.business.taxation.TaxInfo;

@Entity
@Table(name = "SalesItems")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class AbstractSalesItem extends AbstractPersistentObjectWithContinuance<AbstractSalesItem>
    implements
    ILabelledItem {

  @NaturalId
  private String name;

  /**
   * <p>
   * The vat class for products on the bill is usually globally determined by
   * the tax info attached to the bill.
   * </p>
   * <p>
   * Some products, however, might need to carry additional information that
   * help determining the correct tax class.
   * </p>
   * <p>
   * <b> Consider the following example (please be sure to read our warning
   * notice about tax examples, e.g. in the {@link TaxInfo} JavaDoc or in the
   * projects README.txt!) </b>
   * </p>
   * <p>
   * In a restaurant, the global tax information for the bill could be
   * "sold to go" or "eaten inhouse" which determines whether the tax class for
   * food is reduced or standard vat.
   * </p>
   * <p>
   * But if the same restaurant is selling China plates with their logo, the tax
   * class for those plates should always be "normal vat", even if the other
   * items on the bill are food that was ordered "to go" and hence taxed
   * "reduced vat".
   * </p>
   */
  @ManyToOne(cascade = CascadeType.ALL, optional = true)
  private TaxInfo overridingTaxInfo;


  protected AbstractSalesItem(String name, Date validFrom, Date validTo) {
    super(validFrom, validTo);
    this.name = name;
  }

  public TaxInfo getOverridingTaxInfo() {
    return overridingTaxInfo;
  }

  public String getName() {
    return name;
  }

  public void setOverridingTaxInfo(TaxInfo taxInfo) {
    this.overridingTaxInfo = taxInfo;
  }

  @Override
  public String getLabel() {
    return name;
  }

  @Override
  protected final void additionalEqualsForSubclasses(EqualsBuilder builder, AbstractSalesItem obj) {
    builder.append(name, obj.name);
  }

  @Override
  protected final void additionalHashcodeForSubclasses(HashCodeBuilder builder) {
    builder.append(name);
  }

}
