package de.bstreit.java.oscr.gui.swing.cashregister.ui.factories;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Predicate;

import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.eventbroadcasting.EventBroadcaster;
import de.bstreit.java.oscr.business.eventbroadcasting.OfferChangeListener;
import de.bstreit.java.oscr.business.offers.AbstractOffer;
import de.bstreit.java.oscr.business.offers.ExtraOffer;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.offers.VariationOffer;
import de.bstreit.java.oscr.business.products.Product;
import de.bstreit.java.oscr.business.staff.User;
import de.bstreit.java.oscr.business.staff.dao.IUserRepository;
import de.bstreit.java.oscr.gui.swing.cashregister.ui.MainWindowController;

@Named
public class ButtonFactory {

	@Inject
	private MainWindowController appController;

	@Inject
	private EventBroadcaster eventBroadcaster;

	@Inject
	private IUserRepository userRepository;

	public JButton createButtonFor(AbstractOffer<?> offer) {

		if (offer instanceof ProductOffer) {
			return createProductOfferButton((ProductOffer) offer);
		} else if (offer instanceof VariationOffer) {
			return createVariationOfferButton((VariationOffer) offer);
		} else if (offer instanceof ExtraOffer) {
			return createExtraOfferButton((ExtraOffer) offer);
		}

		throw new OfferClassNotImplementedException();
	}

	private JButton createProductOfferButton(final ProductOffer productOffer) {

		final JButton button = new JButton(productOffer.getLabel());
		setDefaults(button);

		button.addActionListener(e -> appController.addToBill(productOffer));

		eventBroadcaster.addListener(new OfferChangeListener() {

			@Override
			public void offerUpdated(AbstractOffer<?> oldItem,
					AbstractOffer<?> newItem) {

				// TODO: remove old action listener!!
				button.setText(newItem.getLabel());
			}

			@Override
			public void offerDeleted(AbstractOffer<?> item) {
				button.setEnabled(false);
				button.setToolTipText("This offer has been deleted");
			}

			@Override
			public void offerCreated(AbstractOffer<?> newItem) {

			}
		});

		setColourIfNotEmpty(button, productOffer);

		return button;
	}

	private void setDefaults(JButton button) {
		button.setMinimumSize(new Dimension(0, 40));
		button.setPreferredSize(new Dimension(120, 40));
		button.setMaximumSize(new Dimension(120, 40));
		button.setInheritsPopupMenu(true);
	}

	private void setColourIfNotEmpty(final JButton button,
			final ProductOffer productOffer) {

		final Product product = productOffer.getOfferedItem();

		if (product.getProductCategory() != null) {

			final String colourAsString = product.getProductCategory()
					.getColour();

			if (StringUtils.isNotBlank(colourAsString)) {
				final Color colour = Color.decode(colourAsString);
				button.setBackground(colour);
			}
		}

	}

	private JButton createVariationOfferButton(final VariationOffer offer) {

		final JButton button = new JButton(offer.getLabel());
		setDefaults(button);

		button.addActionListener(e -> appController.setVariationOffer(offer));

		return button;
	}

	private JButton createExtraOfferButton(final ExtraOffer offer) {

		final JButton button = new JButton(offer.getLabel());
		setDefaults(button);

		button.addActionListener(e -> appController.addExtraOffer(offer));

		return button;
	}

	public Component createFreePromotionButton() {
		final JToggleButton freePromotionButton = new JToggleButton(
				"Free / Promo");

		addFfreePromotionActionListener(freePromotionButton);

		setToggleButtonUponBillChangeAndDisableUponMissingBill(
				freePromotionButton, bill -> bill.isFreePromotionOffer());

		freePromotionButton.setMinimumSize(new Dimension(0, 40));
		freePromotionButton.setEnabled(false);

		return freePromotionButton;
	}

	private void setToggleButtonUponBillChangeAndDisableUponMissingBill(
			final JToggleButton toggleButton,
			final Predicate<Bill> selectedPredicate) {

		eventBroadcaster.addBillChangeListener(newBill -> {

			toggleButton.setEnabled(newBill.isPresent());

			final boolean selected;

			if (newBill.isPresent()) {
				selected = selectedPredicate.apply(newBill.get());
			} else {
				selected = false;
			}

			if (toggleButton.isSelected() != selected) {
				toggleButton.setSelected(selected);
			}

		});
	}

	private void addFfreePromotionActionListener(
			final JToggleButton freePromotionButton) {
		freePromotionButton.addActionListener(e -> {
			if (freePromotionButton.isSelected()) {
				appController.setFreePromotion();
			} else {
				appController.clearFreePromotion();
			}
		});
	}

	public Component createStaffConsumptionButton() {
		final JToggleButton staffConsumptionButton = new JToggleButton("Staff");

		addStaffConsumptionActionListener(staffConsumptionButton);
		addPopupMenuForOtherStaffMembers(staffConsumptionButton);

		setToggleButtonUponBillChangeAndDisableUponMissingBill(
				staffConsumptionButton, bill -> bill.isConsumedByStaff());

		staffConsumptionButton.setMinimumSize(new Dimension(0, 40));
		staffConsumptionButton.setEnabled(false);

		return staffConsumptionButton;
	}

	private void addStaffConsumptionActionListener(
			final JToggleButton staffConsumptionButton) {

		staffConsumptionButton.addActionListener(e -> {
			if (staffConsumptionButton.isSelected()) {
				appController.setStaffConsumption();
			} else {
				appController.clearStaffConsumption();
			}
		});

	}

	private void addPopupMenuForOtherStaffMembers(
			final JToggleButton staffConsumptionButton) {

		final JPopupMenu popupMenu = new JPopupMenu();

		for (final User staffMember : userRepository.findAll()) {
			popupMenu.add(createMenuItem(staffMember));
		}

		final PopupListener popupListener = new PopupListener(popupMenu);

		eventBroadcaster.addBillChangeListener(newBill -> popupListener
				.setActive(newBill.isPresent()));

		staffConsumptionButton.addMouseListener(popupListener);
	}

	private JMenuItem createMenuItem(final User staffMember) {

		final JMenuItem menuItem = new JMenuItem(new AbstractAction(
				staffMember.getFullname()) {

			@Override
			public void actionPerformed(ActionEvent e) {
				appController.setStaffConsumption(staffMember);
			}
		});

		return menuItem;
	}

	public Component createToGoButton() {
		final JToggleButton btnToGo = new JToggleButton("To go");
		btnToGo.addActionListener(e -> appController.setBillToGo(btnToGo
				.isSelected()));

		setToggleButtonUponBillChangeAndDisableUponMissingBill(btnToGo,
				bill -> appController.isBillToGo());

		btnToGo.setMinimumSize(new Dimension(0, 40));
		btnToGo.setEnabled(false);

		return btnToGo;
	}

}
