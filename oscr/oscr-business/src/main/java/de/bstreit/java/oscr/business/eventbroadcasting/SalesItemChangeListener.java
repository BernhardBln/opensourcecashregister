package de.bstreit.java.oscr.business.eventbroadcasting;

import de.bstreit.java.oscr.business.products.AbstractSalesItem;

public interface SalesItemChangeListener {

	public void salesItemUpdated(AbstractSalesItem oldItem,
			AbstractSalesItem newItem);

	public void salesItemCreated(AbstractSalesItem newItem);

	public void salesItemDeleted(AbstractSalesItem item);

}
