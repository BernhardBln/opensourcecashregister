package de.bstreit.java.oscr.gui.swing.cashregister.ui.favourites;

import java.util.Collection;

import de.bstreit.java.oscr.business.offers.AbstractOffer;

public interface IFavouritesService {

	public Collection<AbstractOffer<?>> getFavouriteOffers();

}
