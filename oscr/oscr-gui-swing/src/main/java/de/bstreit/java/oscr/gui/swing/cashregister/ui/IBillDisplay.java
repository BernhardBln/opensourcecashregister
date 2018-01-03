package de.bstreit.java.oscr.gui.swing.cashregister.ui;

import java.util.List;

public interface IBillDisplay {

  void printBill(String billAsText);

  void show();

  void scrollToBeginning();

  void clear();

  void showError(String message);

  void showWarnings(List<String> billService);
}
