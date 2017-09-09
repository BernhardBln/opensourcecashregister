package de.bstreit.java.oscr.gui.swing.cashregister.ui.factories;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.eventbroadcasting.EventBroadcaster;
import de.bstreit.java.oscr.business.eventbroadcasting.OfferChangeListener;
import de.bstreit.java.oscr.business.offers.*;
import de.bstreit.java.oscr.business.products.Product;
import de.bstreit.java.oscr.business.products.category.ProductCategory;
import de.bstreit.java.oscr.business.products.category.dao.IProductCategoryRepository;
import de.bstreit.java.oscr.business.staff.User;
import de.bstreit.java.oscr.business.staff.dao.IUserRepository;
import de.bstreit.java.oscr.gui.swing.cashregister.ui.MainWindowController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Set;

@Profile("UI")
@Named
public class ButtonFactory {

  private static final Color DEFAULT_BACKGROUND = (Color) UIManager.get("ToggleButton.select");
  private static final Color WARNING_BACKGROUND = new Color(255, 204, 153);

  @Inject
  private MainWindowController appController;

  @Inject
  private EventBroadcaster eventBroadcaster;

  @Inject
  private IUserRepository userRepository;

  @Inject
  private IProductCategoryRepository productCategoryRepository;

  @Value("${foodCategories:''}")
  private String foodCategoryNamesStrList;

  private Set<ProductCategory> foodCategories = Sets.newHashSet();


  @PostConstruct
  public void initFoodCategory() {
    if (StringUtils.isBlank(foodCategoryNamesStrList)) {
      return;
    }

    final String[] foodCategoriesStr = StringUtils.splitByWholeSeparator
      (foodCategoryNamesStrList, ",");

    for (final String foodCategoryStr : foodCategoriesStr) {

      if (StringUtils.isBlank(foodCategoryStr)) {
        continue;
      }

      final ProductCategory category = productCategoryRepository.findByName(foodCategoryStr.trim());

      if (category != null) {
        foodCategories.add(category);
      }

    }
  }

  public JButton createButtonFor(final AbstractOffer<?> offer) {

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
      public void offerUpdated(final AbstractOffer<?> oldItem,
                               final AbstractOffer<?> newItem) {

        // TODO: remove old action listener!!
        button.setText(newItem.getLabel());
      }

      @Override
      public void offerDeleted(final AbstractOffer<?> item) {
        button.setEnabled(false);
        button.setToolTipText("This offer has been deleted");
      }

      @Override
      public void offerCreated(final AbstractOffer<?> newItem) {

      }
    });

    setColourIfNotEmpty(button, productOffer);

    return button;
  }

  private void setDefaults(final JButton button) {
    button.setMinimumSize(new Dimension(0, 40));
    button.setPreferredSize(new Dimension(120, 40));
    button.setMaximumSize(new Dimension(120, 40));
    button.setInheritsPopupMenu(true);
    button.setMargin(new Insets(0, 0, 0, 0));

    final Font oldFont = button.getFont();
    final Font newFont = new Font(oldFont.getName(), oldFont.getStyle(), 11);
    button.setFont(newFont);
  }

  private void setColourIfNotEmpty(final JButton button,
                                   final ProductOffer productOffer) {

    final Product product = productOffer.getOfferedItem();

    if (product.getProductCategory() != null) {

      final String colourAsString = product
        .getProductCategory()
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
      .setEnabled(newBill.isPresent() && !newBill
        .get()
        .isEmpty()));
    button.setEnabled(false);

    return button;
  }

  private JButton createPromoOfferButton(final PromoOffer offer) {
    final JButton button = new JButton(offer.getLabel());
    setDefaults(button);

    button.addActionListener(e -> appController.setPromoOffer(offer));

    eventBroadcaster.addBillChangeListener(newBill -> button
      .setEnabled(newBill.isPresent() && !newBill
        .get()
        .isEmpty()
        && !newBill
        .get()
        .isConsumedByStaff()
        && !newBill
        .get()
        .isFreePromotionOffer()
        && !newBill
        .get()
        .hasReduction()
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
      .setEnabled(newBill.isPresent() && !newBill
        .get()
        .isEmpty()));
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
    final JButton button = new JButton("10% / 20% / 40% off");

    addReductionButton(button);

    eventBroadcaster.addBillChangeListener(newBill -> button
      .setEnabled(getReductionEnabledLambda().apply(newBill)));

    button.setMinimumSize(new Dimension(0, 40));
    button.setEnabled(false);

    return button;
  }

  private Predicate<Optional<Bill>> getFreePromotionIsEnabledLambda() {
    return billOpt -> billOpt.isPresent()
      && !billOpt
      .get()
      .isConsumedByStaff()
      && hasNoPromoOffer(billOpt)
      && !billOpt
      .get()
      .hasReduction()
      && !noReductionItems(billOpt.get());
  }

  private boolean noReductionItems(final Bill bill) {
    return bill
      .getBillItems()
      .stream()
      .anyMatch(b -> b
        .getOffer()
        .getOfferedItem()
        .isNoReduction());
  }

  private Predicate<Optional<Bill>> getReductionEnabledLambda() {
    return billOpt -> billOpt.isPresent()
      && !billOpt
      .get()
      .isConsumedByStaff() && !billOpt
      .get()
      .isFreePromotionOffer();
  }

  private boolean hasNoPromoOffer(final Optional<Bill> billOpt) {
    return billOpt
      .get()
      .getBillItems()
      .stream()
      .map(item -> item.getExtraAndVariationOffers())
      .noneMatch(
        offers -> offers
          .stream()
          .anyMatch(
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

  private void addReductionButton(
    final JButton button) {

    button.addActionListener(e -> appController.toggleReduction());

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
      && !billOpt
      .get()
      .isFreePromotionOffer()

      && !billOpt
      .get()
      .hasReduction()

      && hasNoPromoOffer(billOpt)

      && !billOpt
      .get()
      .hasPfand();
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
      public void actionPerformed(final ActionEvent e) {
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

    eventBroadcaster.addBillChangeListener(billOpt -> setToGoWarning(btnToGo, billOpt));

    btnToGo.setMinimumSize(new Dimension(0, 40));
    btnToGo.setEnabled(false);

    return btnToGo;
  }

  private void setToGoWarning(final JToggleButton btnToGo, final Optional<Bill> billOpt) {

    final boolean showWarning = billOpt.isPresent() && containsFood(billOpt.get());

    UIManager.put("ToggleButton.select", showWarning ? WARNING_BACKGROUND : DEFAULT_BACKGROUND);
    SwingUtilities.updateComponentTreeUI(btnToGo);
  }

  private boolean containsFood(final Bill bill) {
    return bill
      .getBillItems()
      .stream()//
      .map(b -> b
        .getOffer()
        .getOfferedItem()
        .getProductCategory())//
      .anyMatch(cat -> foodCategories.contains(cat));
  }

  public Component createPayButton() {
    final JButton payButton = new JButton("Pay");
    payButton.addActionListener(e -> appController.closeBill());
    payButton.setMinimumSize(new Dimension(0, 40));

    eventBroadcaster.addBillChangeListener(newBill -> payButton
      .setEnabled(newBill.isPresent() && !newBill
        .get()
        .isEmpty()));
    payButton.setEnabled(false);

    return payButton;
  }

  public Component createNewBillButton() {
    final JButton newBillButton = new JButton("New Bill");
    newBillButton.addActionListener(e -> appController.newBill());
    newBillButton.setMinimumSize(new Dimension(0, 40));

    eventBroadcaster.addBillChangeListener(newBill -> newBillButton
      .setEnabled(newBill.isPresent() && !newBill
        .get()
        .isEmpty()));
    newBillButton.setEnabled(false);

    return newBillButton;
  }

  public Component createShowOpenBillsButton(final Action showOpenBillsAction) {
    final JButton showOpenBillsButton = new JButton(showOpenBillsAction);
    showOpenBillsButton.setMinimumSize(new Dimension(0, 40));

    eventBroadcaster.addBillChangeListener(newBill -> showOpenBillsButton
      .setEnabled(appController.hasOpenBills()));
    showOpenBillsButton.setEnabled(appController.hasOpenBills());

    return showOpenBillsButton;
  }

}
