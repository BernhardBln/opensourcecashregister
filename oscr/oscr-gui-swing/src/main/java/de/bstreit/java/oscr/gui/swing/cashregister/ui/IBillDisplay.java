package de.bstreit.java.oscr.gui.swing.cashregister.ui;

public interface IBillDisplay {

	void printBill(String billAsText);

	public void show();

	void scrollToBeginning();

	void clear();

    void showError(String message);
}
