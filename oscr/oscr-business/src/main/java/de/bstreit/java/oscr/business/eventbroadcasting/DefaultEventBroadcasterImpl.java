package de.bstreit.java.oscr.business.eventbroadcasting;

import java.util.Set;

import javax.inject.Named;

import com.google.common.collect.Sets;

import de.bstreit.java.oscr.business.offers.AbstractOffer;

@Named
public class DefaultEventBroadcasterImpl implements EventBroadcaster {

	public Set<OfferChangeListener> offerChangeListeners = Sets.newHashSet();
	public Set<SalesItemChangeListener> salesItemChangeListeners = Sets
			.newHashSet();

	@Override
	public void addListener(OfferChangeListener offerChangeListener) {
		offerChangeListeners.add(offerChangeListener);
	}

	@Override
	public void addListener(SalesItemChangeListener salesItemChangeListener) {
		salesItemChangeListeners.add(salesItemChangeListener);
	}

	@Override
	public void notifyOfferUpdated(Object sender, AbstractOffer<?> oldItem,
			AbstractOffer<?> newItem) {

		for (final OfferChangeListener offerChangeListener : offerChangeListeners) {

			if (offerChangeListener == sender) {
				continue;
			}

			offerChangeListener.offerUpdated(oldItem, newItem);
		}

	}

}
