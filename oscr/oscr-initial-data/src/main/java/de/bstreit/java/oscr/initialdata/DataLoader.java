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
package de.bstreit.java.oscr.initialdata;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.base.finance.tax.dao.IVATClassRepository;
import de.bstreit.java.oscr.business.offers.ExtraOffer;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.offers.VariationOffer;
import de.bstreit.java.oscr.business.offers.dao.IExtraOfferRepository;
import de.bstreit.java.oscr.business.offers.dao.IProductOfferRepository;
import de.bstreit.java.oscr.business.offers.dao.IVariationOfferRepository;
import de.bstreit.java.oscr.business.products.ContainerSize;
import de.bstreit.java.oscr.business.products.TaxInfo;
import de.bstreit.java.oscr.business.products.dao.IContainerSizeRepository;
import de.bstreit.java.oscr.business.products.dao.ITaxInfoRepository;
import de.bstreit.java.oscr.initialdata.initialdata.ContainerSizes;
import de.bstreit.java.oscr.initialdata.initialdata.TaxInfos;
import de.bstreit.java.oscr.initialdata.initialdata.VATClasses;
import de.bstreit.java.oscr.initialdata.initialdata.offers.ExtraOffers;
import de.bstreit.java.oscr.initialdata.initialdata.offers.ProductOffers;
import de.bstreit.java.oscr.initialdata.initialdata.offers.VariationOffers;

@Named
public class DataLoader {

  @Inject
  private IProductOfferRepository productOfferRepository;

  @Inject
  private IExtraOfferRepository extraOfferRepository;

  @Inject
  private IVariationOfferRepository variationOfferRepository;

  @Inject
  private IVATClassRepository vatClassRepository;

  @Inject
  private ITaxInfoRepository taxInfoRepository;

  @Inject
  private IContainerSizeRepository containerSizeRepository;


  @Transactional
  public void populateDatabase() {
    saveEntities(getEntitiesOfType(ContainerSize.class, ContainerSizes.class),
        containerSizeRepository);
    saveEntities(getEntitiesOfType(VATClass.class, VATClasses.class), vatClassRepository);
    saveEntities(getEntitiesOfType(TaxInfo.class, TaxInfos.class), taxInfoRepository);

    saveEntities(getEntitiesOfType(ProductOffer.class, ProductOffers.class),
        productOfferRepository);
    saveEntities(getEntitiesOfType(ExtraOffer.class, ExtraOffers.class),
        extraOfferRepository);
    saveEntities(getEntitiesOfType(VariationOffer.class, VariationOffers.class),
        variationOfferRepository);
  }

  private <TYPE> Collection<TYPE> getEntitiesOfType(Class<TYPE> expectedClassType, Class<?> classInstances) {
    final Field[] declaredFields = classInstances.getDeclaredFields();
    final List<TYPE> entities = new ArrayList<TYPE>();

    for (Field field : declaredFields) {

      if (isConstantAndOfType(expectedClassType, field)) {
        addEntityFromField(expectedClassType, entities, field);
      }

    }

    return entities;

  }

  private <TYPE> void addEntityFromField(Class<TYPE> expectedClassType, final List<TYPE> values, Field f) {
    try {
      values.add(expectedClassType.cast(f.get(null)));

    } catch (IllegalArgumentException | IllegalAccessException e) {
      // Not expected
      throw new RuntimeException(
          "Unexpected exception caught while gathering static instances", e);
    }
  }

  private <TYPE> boolean isConstantAndOfType(Class<TYPE> expectedClassType, Field f) {
    final int modifiers = f.getModifiers();

    final boolean isConstant = Modifier.isStatic(modifiers)
        && Modifier.isFinal(modifiers)
        && Modifier.isPublic(modifiers);

    final boolean correctType = f.getType().equals(expectedClassType);

    return isConstant && correctType;
  }

  private <TYPE> void saveEntities(Collection<TYPE> allEntities,
      JpaRepository<TYPE, ?> repository) {

    repository.save(allEntities);

  }
}
