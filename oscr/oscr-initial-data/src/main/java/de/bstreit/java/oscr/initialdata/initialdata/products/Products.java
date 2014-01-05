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
package de.bstreit.java.oscr.initialdata.initialdata.products;

import de.bstreit.java.oscr.initialdata.initialdata.ContainerSizes;
import de.bstreit.java.oscr.initialdata.initialdata.ValidityDates;
import de.bstreit.java.oscr.business.products.ContainerSize;
import de.bstreit.java.oscr.business.products.Product;

/**
 * @author streit
 * 
 */
public class Products {

  public static final Product ESPRESSO = addProduct("Espresso", ContainerSizes.CUP_100_ML);
  public static final Product DOUBLE_ESPRESSO = addProduct("Double Espresso", ContainerSizes.CUP_100_ML);

  public static final Product AMERICANO = addProduct("Americano");
  public static final Product DOUBLE_AMERICANO = addProduct("Double Americano");

  public static final Product SINGLE_ESPRESSO_MACCHIATO = addProduct("Single Espresso Macchiato",
      ContainerSizes.CUP_100_ML);
  public static final Product DOUBLE_ESPRESSO_MACCHIATO = addProduct("Double Espresso Macchiato",
      ContainerSizes.CUP_100_ML);

  public static final Product CAPPUCCINO = addProduct("Cappuccino", ContainerSizes.CUP_200_ML);
  public static final Product DOUBLE_CAPPUCCINO = addProduct("Double Cappuccino",
      ContainerSizes.CUP_200_ML);

  public static final Product LATTE_MACCHIATO = addProduct("Latte Macchiato", ContainerSizes.CUP_200_ML);
  public static final Product DOUBLE_LATTE_MACCHIATO = addProduct("Double Latte Macchiato",
      ContainerSizes.CUP_300_ML);

  public static final Product KAKAO = addProduct("Kakao", ContainerSizes.CUP_200_ML);
  public static final Product SINGLE_SCHOKO_CAPPUCCINO = addProduct("Single Schoko-Cappuccino",
      ContainerSizes.CUP_300_ML);
  public static final Product DOUBLE_SCHOKO_CAPPUCCINO = addProduct("Double Schoko-Cappuccino",
      ContainerSizes.CUP_300_ML);

  public static final Product VIETNAMESISCHER_EISKAFFEE = addProduct("Vietnamesischer Eiskaffee");
  public static final Product ICED_DOUBLE_ESPRESSO = addProduct("Iced Double Espresso");
  public static final Product ICED_DOUBLE_CAPPUCCINO = addProduct("Iced Double Cappuccino");
  public static final Product ICED_COFFEE = addProduct("Iced Coffee");
  public static final Product ICED_COFFEE_MIT_MILCH = addProduct("Iced Coffee mit Milch");

  public static final Product MINT_TEA = addProduct("Mint Tea", ContainerSizes.CUP_200_ML);
  public static final Product BLACK_TEA = addProduct("Black Tea", ContainerSizes.CUP_200_ML);
  public static final Product WHITE_TEA = addProduct("White Tea");

  public static final Product HEISSE_BIO_ZITRONE = addProduct("Heiße Bio-Zitrone",
      ContainerSizes.CUP_200_ML);

  public static final Product APFELSCHORLE = addProduct("Apfelschorle");
  public static final Product ICED_TEA = addProduct("Iced Tea");


  private static Product addProduct(String productName) {
    return new Product(productName, ValidityDates.VALID_FROM_DATE, ValidityDates.VALID_TO_DATE);
  }

  private static Product addProduct(String productName, ContainerSize packageSize) {
    final Product product = new Product(productName, ValidityDates.VALID_FROM_DATE,
        ValidityDates.VALID_TO_DATE);

    product.setPackageSize(packageSize);

    return product;
  }


}
