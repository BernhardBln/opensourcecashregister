package de.bstreit.java.oscr.business.eventbroadcasting;

import de.bstreit.java.oscr.business.offers.AbstractOffer;

public interface OfferChangeListener {

	public void offerUpdated(AbstractOffer<?> oldItem, AbstractOffer<?> newItem);

	public void offerCreated(AbstractOffer<?> newItem);

	public void offerDeleted(AbstractOffer<?> item);

}
