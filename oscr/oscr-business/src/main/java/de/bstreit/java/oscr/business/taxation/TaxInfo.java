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
package de.bstreit.java.oscr.business.taxation;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.NaturalId;

import de.bstreit.java.oscr.business.base.persistence.AbstractPersistentObjectWithContinuance;
import de.bstreit.java.oscr.business.products.AbstractSalesItem;

/**
 * <p>
 * A tax info is an annotation for an {@link AbstractSalesItem} which helps to
 * determine the correct VAT class for the item.
 * </p>
 * 
 * <p>
 * For example, currently, in Germany, selling food to-go might be a reason to
 * only tax the products with reduced VAT. Hence we would create a "sold to go"
 * tax info instance which gets mapped later to reduced VAT.
 * </p>
 * 
 * <p>
 * <b>Warning:</b> all information about taxes, tax classes and taxation that
 * you find in this project are just <b>examples</b>, they could simply be
 * <b>wrong</b>, not suit your individual situation, be different in your
 * country or area, or change over time - always ask a certified tax consultant!
 * Do <b>not</b> simply rely on those examples when configuring your cash
 * register for production use or for any other purpose! We do <b>not</b> take
 * any responsibility or liability if you get in trouble with your local tax
 * office or loose money because you pay more VAT to the tax office than you
 * actually need!
 * </p>
 * 
 * @author streit
 */
@Entity
public class TaxInfo extends AbstractPersistentObjectWithContinuance<TaxInfo> {

  @NaturalId
  @Access(AccessType.FIELD)
  private String denotation;


  @SuppressWarnings("unused")
  private TaxInfo() {
  }

  public TaxInfo(String denotation, Date validFrom, Date validTo) {
    super(validFrom, validTo);
    this.denotation = denotation;
  }


  /**
   * @return the {@link #denotation}
   */
  public String getDenotation() {
    return denotation;
  }

  @Override
  protected void additionalEqualsForSubclasses(EqualsBuilder equalsBuilder, TaxInfo otherObject) {
    equalsBuilder.append(denotation, otherObject.denotation);

  }

  @Override
  protected void additionalHashcodeForSubclasses(HashCodeBuilder builder) {
    builder.append(denotation);
  }

}
