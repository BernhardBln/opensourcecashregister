package de.bstreit.java.oscr.business.bill;

import de.bstreit.java.oscr.business.base.DisplayableException;

/**
 * Created by bernhard on 27.05.17.
 */
public class CannotAddItemException extends DisplayableException {
    public CannotAddItemException(String message) {
        super(message);
    }
}
