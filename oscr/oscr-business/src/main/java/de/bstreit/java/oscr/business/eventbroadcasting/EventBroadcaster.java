package de.bstreit.java.oscr.business.eventbroadcasting;

import de.bstreit.java.oscr.business.offers.AbstractOffer;

public interface EventBroadcaster {

	public abstract void addListener(OfferChangeListener offerChangeListener);

	public abstract void addListener(
			SalesItemChangeListener salesItemChangeListener);

	public abstract void notifyOfferUpdated(Object sender,
			AbstractOffer<?> oldItem, AbstractOffer<?> newItem);

}