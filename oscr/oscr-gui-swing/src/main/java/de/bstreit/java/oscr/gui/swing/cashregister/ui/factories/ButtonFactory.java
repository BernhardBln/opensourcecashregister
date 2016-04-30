package de.bstreit.java.oscr.gui.swing.cashregister.ui.factories;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.eventbroadcasting.EventBroadcaster;
import de.bstreit.java.oscr.business.eventbroadcasting.OfferChangeListener;
import de.bstreit.java.oscr.business.offers.AbstractOffer;
import de.bstreit.java.oscr.business.offers.ExtraOffer;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.offers.PromoOffer;
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
    } else if (offer instanceof PromoOffer) {
      return createPromoOfferButton((PromoOffer) offer);
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
    button.setMargin(new Insets(0, 0, 0, 0));
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

    eventBroadcaster.addBillChangeListener(newBill -> button
        .setEnabled(newBill.isPresent() && !newBill.get().isEmpty()));
    button.setEnabled(false);

    return button;
  }

  private JButton createPromoOfferButton(PromoOffer offer) {
    final JButton button = new JButton(offer.getLabel());
    setDefaults(button);

    button.addActionListener(e -> appController.setPromoOffer(offer));

    eventBroadcaster.addBillChangeListener(newBill -> button
        .setEnabled(newBill.isPresent() && !newBill.get().isEmpty()
            && !newBill.get().isConsumedByStaff()
            && !newBill.get().isFreePromotionOffer()
            && !newBill.get().isTwentyPercentOff()
            // only allow one promo per Bill:
            && hasNoPromoOffer(newBill)));
    button.setEnabled(false);

    return button;
  }

  private JButton createExtraOfferButton(final ExtraOffer offer) {

    final JButton button = new JButton(offer.getLabel());
    setDefaults(button);

    button.addActionListener(e -> appController.addExtraOffer(offer));

    eventBroadcaster.addBillChangeListener(newBill -> button
        .setEnabled(newBill.isPresent() && !newBill.get().isEmpty()));
    button.setEnabled(false);

    return button;
  }

  public Component createFreePromotionButton() {
    final JToggleButton freePromotionButton = new JToggleButton(
        "Free / Promo");

    addFreePromotionActionListener(freePromotionButton);

    setToggleButtonUponBillChangeAndDisableUponMissingBill(
        freePromotionButton, getFreePromotionIsEnabledLambda(),
        bill -> bill.isFreePromotionOffer());

    freePromotionButton.setMinimumSize(new Dimension(0, 40));
    freePromotionButton.setEnabled(false);

    return freePromotionButton;
  }

  public Component createTwentyPercentPromotionButton() {
    final JToggleButton button = new JToggleButton("20% off");

    addTwentyPercentPromotion(button);

    setToggleButtonUponBillChangeAndDisableUponMissingBill(button,
        getTwentyPercentPromotionIsEnabledLambda(),
        bill -> bill.isTwentyPercentOff());

    button.setMinimumSize(new Dimension(0, 40));
    button.setEnabled(false);

    return button;
  }

  private Predicate<Optional<Bill>> getFreePromotionIsEnabledLambda() {
    return billOpt -> billOpt.isPresent()
        && !billOpt.get().isConsumedByStaff()
        && hasNoPromoOffer(billOpt) && !billOpt.get().isTwentyPercentOff();
  }

  private Predicate<Optional<Bill>> getTwentyPercentPromotionIsEnabledLambda() {
    return billOpt -> billOpt.isPresent()
        && !billOpt.get().isConsumedByStaff() && !billOpt.get().isFreePromotionOffer();
  }

  private boolean hasNoPromoOffer(Optional<Bill> billOpt) {
    return billOpt
        .get()
        .getBillItems()
        .stream()
        .map(item -> item.getExtraAndVariationOffers())
        .noneMatch(
            offers -> offers.stream().anyMatch(
                o -> o instanceof PromoOffer));
  }

  private void setToggleButtonUponBillChangeAndDisableUponMissingBill(
      final JToggleButton toggleButton,
      final Predicate<Optional<Bill>> enabledPredicate,
      final Predicate<Bill> selectedPredicate) {

    eventBroadcaster.addBillChangeListener(newBill -> {

      toggleButton.setEnabled(enabledPredicate.apply(newBill));

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

  private void addFreePromotionActionListener(
      final JToggleButton freePromotionButton) {

    freePromotionButton.addActionListener(e -> {
      if (freePromotionButton.isSelected()) {
        appController.setFreePromotion();
      } else {
        appController.clearFreePromotion();
      }
    });

  }

  private void addTwentyPercentPromotion(
      final JToggleButton freePromotionButton) {

    freePromotionButton.addActionListener(e -> {
      if (freePromotionButton.isSelected()) {
        appController.setTwentyPercentPromotion();
      } else {
        appController.clearTwentyPercentPromotion();
      }
    });

  }

  public Component createStaffConsumptionButton() {
    final JToggleButton staffConsumptionButton = new JToggleButton("Staff");

    addStaffConsumptionActionListener(staffConsumptionButton);
    addPopupMenuForOtherStaffMembers(staffConsumptionButton);

    setToggleButtonUponBillChangeAndDisableUponMissingBill(
        staffConsumptionButton, getStaffConsumptionIsEnabledLambda(),
        bill -> bill.isConsumedByStaff());

    staffConsumptionButton.setMinimumSize(new Dimension(0, 40));
    staffConsumptionButton.setEnabled(false);

    return staffConsumptionButton;
  }

  private Predicate<Optional<Bill>> getStaffConsumptionIsEnabledLambda() {

    return billOpt -> billOpt.isPresent()
        && !billOpt.get().isFreePromotionOffer() && !billOpt.get().isTwentyPercentOff()
        && hasNoPromoOffer(billOpt);
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

    for (final User staffMember : userRepository.findByValidToIsNull()) {
      popupMenu.add(createMenuItem(staffMember));
    }

    final PopupListener popupListener = new PopupListener(popupMenu);

    eventBroadcaster
        .addBillChangeListener(newBill -> popupListener
            .setActive(getStaffConsumptionIsEnabledLambda().apply(
                newBill)));

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
        billOpt -> billOpt.isPresent(),
        bill -> appController.isBillToGo());

    btnToGo.setMinimumSize(new Dimension(0, 40));
    btnToGo.setEnabled(false);

    return btnToGo;
  }

  public Component createPayButton() {
    final JButton payButton = new JButton("Pay");
    payButton.addActionListener(e -> appController.closeBill());
    payButton.setMinimumSize(new Dimension(0, 40));

    eventBroadcaster.addBillChangeListener(newBill -> payButton
        .setEnabled(newBill.isPresent() && !newBill.get().isEmpty()));
    payButton.setEnabled(false);

    return payButton;
  }

  public Component createNewBillButton() {
    final JButton newBillButton = new JButton("New Bill");
    newBillButton.addActionListener(e -> appController.newBill());
    newBillButton.setMinimumSize(new Dimension(0, 40));

    eventBroadcaster.addBillChangeListener(newBill -> newBillButton
        .setEnabled(newBill.isPresent() && !newBill.get().isEmpty()));
    newBillButton.setEnabled(false);

    return newBillButton;
  }

  public Component createShowOpenBillsButton(Action showOpenBillsAction) {
    final JButton showOpenBillsButton = new JButton(showOpenBillsAction);
    showOpenBillsButton.setMinimumSize(new Dimension(0, 40));

    eventBroadcaster.addBillChangeListener(newBill -> showOpenBillsButton
        .setEnabled(appController.hasOpenBills()));
    showOpenBillsButton.setEnabled(appController.hasOpenBills());

    return showOpenBillsButton;
  }

}
