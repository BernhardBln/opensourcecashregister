package de.bstreit.java.oscr.gui.swing.cashregister.ui.factories;

import java.awt.event.ActionEvent;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.AbstractAction;

import de.bstreit.java.oscr.gui.swing.cashregister.ui.MainWindowController;

@Named
public class EditWeeklyButonsAction extends AbstractAction {

	@Inject
	private MainWindowController mainWindowController;

	public EditWeeklyButonsAction() {
		super("Edit weekly offers...");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mainWindowController.editWeeklyOffers();
	}

}
