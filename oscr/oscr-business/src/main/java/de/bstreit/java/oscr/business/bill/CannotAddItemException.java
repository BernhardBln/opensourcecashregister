package de.bstreit.java.oscr.business.bill;

/**
 * Created by bernhard on 27.05.17.
 */
public class CannotAddItemException extends Exception {
    public CannotAddItemException(String message) {
        super(message);
    }
}
