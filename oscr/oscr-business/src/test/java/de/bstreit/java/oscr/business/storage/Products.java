/*
 * Open Source Cash Register
 * 
 * Copyright (C) 2013 Bernhard Streit
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
 * See /licenses/gpl-3.txt for a copy of the GNU GPL.
 * See /README.txt for more information about the software and the author(s).
 * 
 */
package de.bstreit.java.oscr.business.storage;

import java.util.Date;

import de.bstreit.java.oscr.business.products.ContainerSize;
import de.bstreit.java.oscr.business.products.Product;

/**
 * @author streit
 * 
 */
public class Products {

	public static final Product ESPRESSO = addProduct("Espresso", ContainerSizes.CUP_100_ML);

	public static final Product CAPPUCCINO = addProduct("Cappuccino", ContainerSizes.CUP_200_ML);

	public static final Product LATTE_MACCHIATO = addProduct("Latte Macchiato", ContainerSizes.CUP_200_ML);


	private static Product addProduct(String productName, ContainerSize packageSize) {
		final Product product = new Product(productName, new Date(),
				null);

		product.setPackageSize(packageSize);

		return product;
	}

}
