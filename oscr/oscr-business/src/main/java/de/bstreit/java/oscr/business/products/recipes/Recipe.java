package de.bstreit.java.oscr.business.products.recipes;

import java.util.Date;
import java.util.Map;

import javax.persistence.Entity;

import de.bstreit.java.oscr.business.products.AbstractSalesItem;

@Entity
public class Recipe extends AbstractSalesItem {

	private Recipe() {
		super(null, null, null);
	}

	public Recipe(String name, Date validFrom, Date validTo) {
		super(name, validFrom, validTo);
	}
	
	//Map<Ingred, V>

}
