package de.bstreit.java.oscr.gui.swing.cashregister.ui.factories;

import java.awt.event.ActionEvent;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.AbstractAction;

import com.google.common.base.Optional;

import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.eventbroadcasting.BillChangeListener;
import de.bstreit.java.oscr.business.eventbroadcasting.EventBroadcaster;
import de.bstreit.java.oscr.gui.swing.cashregister.ui.MainWindowController;

@Named
public class ShowOpenBillsAction extends AbstractAction implements
BillChangeListener {

	private static final String DESCRIPTION = "Show open Bills";

	@Inject
	private MainWindowController appController;

	@Inject
	private EventBroadcaster eventBroadcaster;

	@PostConstruct
	public void init() {
		eventBroadcaster.addBillChangeListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		appController.showOpenBills();
	}

	@Override
	public void billUpdated(Optional<Bill> newBill) {
		putValue(NAME, createCaption());
	}

	@Override
	public void applicationLaunched() {
		putValue(NAME, createCaption());
	}

	private String createCaption() {
		final int numberOfOpenBills = appController.getNumberOfOpenBills();

		if (numberOfOpenBills <= 0) {
			// additional blanks - cheap hack to get buttons sized correctly
			return "<html><body>&nbsp;&nbsp;&nbsp;&nbsp;" + DESCRIPTION
					+ "&nbsp;&nbsp;&nbsp;&nbsp;</body></html>";
		}

		return DESCRIPTION + " (" + numberOfOpenBills + ")";
	}

}
