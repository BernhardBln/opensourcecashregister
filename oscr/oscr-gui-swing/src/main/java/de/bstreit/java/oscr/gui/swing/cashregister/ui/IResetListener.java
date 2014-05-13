package de.bstreit.java.oscr.gui.swing.cashregister.ui;

/**
 * In case we finish or reset a bill, all gui elements should be set to their
 * original state.
 * 
 * @author bernhard
 * 
 */
public interface IResetListener {

	/** Reset state of gui element */
	public void resetState();
}
