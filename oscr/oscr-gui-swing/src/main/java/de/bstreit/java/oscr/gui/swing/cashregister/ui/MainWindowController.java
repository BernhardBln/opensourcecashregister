package de.bstreit.java.oscr.gui.swing.cashregister.ui;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;

import com.google.common.base.Optional;

import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillItem;
import de.bstreit.java.oscr.business.bill.BillService;
import de.bstreit.java.oscr.business.eventbroadcasting.BillChangeListener;
import de.bstreit.java.oscr.business.eventbroadcasting.EventBroadcaster;
import de.bstreit.java.oscr.business.offers.ExtraOffer;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.offers.PromoOffer;
import de.bstreit.java.oscr.business.offers.VariationOffer;
import de.bstreit.java.oscr.business.staff.IUserService;
import de.bstreit.java.oscr.business.staff.User;
import de.bstreit.java.oscr.text.formatting.BillFormatter;

@Named
@Profile("with-ui")
public class MainWindowController implements BillChangeListener {

	private static final Logger logger = LoggerFactory
			.getLogger(MainWindowController.class);

	private final DateFormat df = SimpleDateFormat.getInstance();

	@Inject
	private IBillDisplay billDisplay;

	@Inject
	private BillFormatter billFormatter;

	@Inject
	private BillService billService;

	@Inject
	private IUserService userService;

	@Inject
	private EventBroadcaster eventBroadcaster;

	/**
	 * This is a shutdown hook that can be registered by the starter app that
	 * launches the application, e.g.
	 * {@link de.bstreit.java.oscr.gui.swing.cashregister.SwingStarter}
	 */
	private Optional<Runnable> shutdownHookForLauncher = Optional.absent();

	private JFrame openBillsFrame;

	@PostConstruct
	private void initController() {
		eventBroadcaster.addBillChangeListener(this);

		openBillsFrame = new JFrame("Open Bills");
		openBillsFrame.getContentPane().setLayout(
				new FlowLayout(FlowLayout.LEFT));
		openBillsFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		openBillsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}

	public void guiLaunched() {
		eventBroadcaster.notifyApplicationLaunched(this);
	}

	public void addToBill(ProductOffer offer) {
		billService.addProductOffer(offer);
	}

	public void setVariationOffer(VariationOffer variationOffer) {
		billService.setVariationOffer(variationOffer);
	}

	public void setPromoOffer(PromoOffer offer) {
		billService.setPromoOffer(offer);
	}

	public void addExtraOffer(ExtraOffer offer) {
		billService.addExtraOffer(offer);
	}

	public void showMainwindow() {
		billDisplay.show();
	}

	public void closeBill() {
		billService.closeBill();
	}

	public void printTodaysTotal() {
		billDisplay.printBill(billService.getTotalAsString());

		billDisplay.scrollToBeginning();
	}

	public void toggleStandardAndReducedVAT() {
		billService.toggleStandardAndReducedVAT();
	}

	public boolean isReducedVAT() {
		return billService.isReducedVAT();
	}

	public void undoLastAction() {
		billService.undoLastAction();
	}

	/**
	 * Notify that the app is supposed to shut down
	 */
	public void notifyShutdown() {
		logger.debug("notifying bill service of shutdown");
		billService.notifyShutdown();
		logger.debug("closing open bill frame");
		openBillsFrame.dispose();
		logger.debug("closing context");
		shutdownHookForLauncher.get().run();
	}

	public void editWeeklyOffers() {
		// TODO Auto-generated method stub
		System.out.println("EDIT WEEKLY OFFERS");
	}

	public void setStaffConsumption() {
		setStaffConsumption(userService.getCurrentUser());
	}

	public void setStaffConsumption(User staffMember) {
		billService.setStaffConsumer(staffMember);
	}

	public void clearStaffConsumption() {
		billService.clearStaffConsumer();
	}

	@Override
	public void billUpdated(Optional<Bill> newBill) {
		if (newBill.isPresent()) {
			billDisplay.printBill(billFormatter.formatBill(newBill.get()));
		} else {
			billDisplay.clear();
		}
	}

	public void setFreePromotion() {
		billService.setFreePromotion();
	}

	public void clearFreePromotion() {
		billService.clearFreePromotion();

	}

	public void showOpenBills() {
		final Collection<Bill> openBills = billService.getOpenBills();

		final Container contentPane = openBillsFrame.getContentPane();
		contentPane.removeAll();

		for (final Bill bill : openBills) {
			contentPane.add(new JButton(createBillButtonAction(bill)));
		}

		openBillsFrame.setVisible(true);
		openBillsFrame.pack();

	}

	private Action createBillButtonAction(final Bill bill) {
		final StringBuilder sb = new StringBuilder();

		sb.append("<html><body>");
		sb.append("<b>Bill opened ").append(df.format(bill.getBillOpened()))
				.append("</b><BR><br>");

		for (final BillItem bi : bill) {
			sb.append(bi.getName()).append("<BR>");
		}

		sb.append("</body></html>");

		final String label = sb.toString();

		return new AbstractAction(label) {

			@Override
			public void actionPerformed(ActionEvent e) {
				billService.loadBill(bill);
				openBillsFrame.setVisible(false);
			}
		};
	}

	public void newBill() {
		billService.newBill();
	}

	public int getNumberOfOpenBills() {
		return billService.getOpenBills().size();
	}

	public boolean hasOpenBills() {
		return !billService.getOpenBills().isEmpty();
	}

	public void setShutdownHookForLauncher(Runnable shutdownHookForLauncher) {

		if (this.shutdownHookForLauncher.isPresent()) {
			throw new IllegalStateException(
					"Shutdown hook can only be set once, and shut only be set by the main() function that launches the application.");
		}

		this.shutdownHookForLauncher = Optional.of(shutdownHookForLauncher);
	}

}
