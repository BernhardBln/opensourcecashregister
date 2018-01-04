package de.bstreit.java.oscr.gui.swing.cashregister.ui.factories;

import de.bstreit.java.oscr.business.eventbroadcasting.EventBroadcaster;
import de.bstreit.java.oscr.business.offers.AbstractOffer;
import de.bstreit.java.oscr.business.offers.ExtraOffer;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.offers.VariationOffer;
import de.bstreit.java.oscr.business.offers.dao.IExtraOfferRepository;
import de.bstreit.java.oscr.business.offers.dao.IProductOfferRepository;
import de.bstreit.java.oscr.business.offers.dao.IPromoOfferRepository;
import de.bstreit.java.oscr.business.offers.dao.IVariationOfferRepository;
import de.bstreit.java.oscr.business.products.category.ProductCategory;
import de.bstreit.java.oscr.business.products.category.dao.IProductCategoryRepository;
import de.bstreit.java.oscr.gui.swing.cashregister.ui.MainWindowController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.google.common.collect.Lists.newArrayList;
import static javax.swing.SwingConstants.LEFT;

@Named
@Profile("UI")
public class ButtonPanelFactory {

  @Inject
  private EventBroadcaster eventBroadcaster;

  @Inject
  private MainWindowController appController;

  @Inject
  private IProductOfferRepository productOfferRep;

  @Inject
  private IExtraOfferRepository extraOfferRepository;

  @Inject
  private IPromoOfferRepository promoOfferRepository;

  @Inject
  private IVariationOfferRepository variationOfferRepository;

  @Inject
  private IProductCategoryRepository productCategoryRepository;

  @Inject
  private ButtonFactory buttonFactory;

  @Inject
  private ShowOpenBillsAction showOpenBillsAction;

  @Value("${weeklyProductCategory}")
  private String weeklyCategoryAsString;
  @Value("${favourites}")
  private String favourites;

  private ProductCategory weeklyCategory;

  private JPanel buttonPanel;
  private JTabbedPane weeklyAndDrinksPanel;
  private JPanel favouritesPanel;
  private JPanel drinksPanel;
  private JPanel weeklyPanel;

  @Inject
  private EditWeeklyButonsAction editWeeklyButonsAction;

  public JPanel createButtonPanel() {

    buttonPanel = new JPanel();

    try {
      if (StringUtils.isNotBlank(weeklyCategoryAsString)) {
        weeklyCategory = productCategoryRepository
          .findByName(weeklyCategoryAsString);
      }

      initJPanel();

      buildAndAddFavouritesPanelToMainPanel();
      buildAndAddDrinksPanelToMainPanel();
      buildAndAddWeeklyPanelToMainPanel();

      return buttonPanel;

    } finally {
      buttonPanel = null;
      drinksPanel = null;
      // Keep weekly panel so we can refresh it
      // weeklyPanel = null;
    }
  }

  private void initJPanel() {
    buttonPanel.removeAll();
    buttonPanel.setLayout(new BorderLayout(0, 0));
    buttonPanel.setPreferredSize(new Dimension(10, 340));

    weeklyAndDrinksPanel = new JTabbedPane(LEFT);

    buttonPanel.add(weeklyAndDrinksPanel, BorderLayout.CENTER);


    eventBroadcaster.addBillChangeListener(billOpt -> {
      if (!billOpt.isPresent()) {
        // bill cleared, set favourite buttons
        weeklyAndDrinksPanel.setSelectedIndex(0);
      }
    });

  }

  private void buildAndAddWeeklyPanelToMainPanel() {

    if (weeklyCategory == null) {
      return;
    }

    final Collection<ProductOffer> allActiveOffers = productOfferRep
      .findActiveOffersByProductCategory(weeklyCategory);

    if (allActiveOffers.isEmpty()) {
      return;
    }

    weeklyPanel = new JPanel();
    weeklyAndDrinksPanel.addTab("Food", weeklyPanel);

    final int rows = 4;
    final int cols = 2;
    weeklyPanel.setLayout(new GridLayout(rows, cols, 4, 3));

    final JPopupMenu editWeeklyOffersPopupMenu = new JPopupMenu();
    editWeeklyOffersPopupMenu.add(editWeeklyButonsAction);
    weeklyPanel.setComponentPopupMenu(editWeeklyOffersPopupMenu);

    for (final ProductOffer offer : allActiveOffers) {
      weeklyPanel.add(buttonFactory.createButtonFor(offer));
    }

  }

  private void buildAndAddFavouritesPanelToMainPanel() {

    favouritesPanel = new JPanel();
    weeklyAndDrinksPanel.addTab("Favourites", favouritesPanel);


    final int rows = 4;
    final int cols = 5;
    favouritesPanel.setLayout(new GridLayout(rows, cols, 3, 3));

    final List<String> selectedFavourites = newArrayList(this.favourites.split(";"));

    final List<AbstractOffer> allActiveOffers = newArrayList();

    allActiveOffers.addAll(productOfferRep.findAllActiveOffers());
    allActiveOffers.addAll(variationOfferRepository.findAllActiveOffers());
    allActiveOffers.addAll(extraOfferRepository.findAllActiveOffers());

    selectedFavourites
      .stream()
      .map(f -> findOffer(f, allActiveOffers))
      .filter(Objects::nonNull)
      .map(buttonFactory::createButtonFor)
      .forEach(favouritesPanel::add);

  }

  private AbstractOffer findOffer(String offerName, List<AbstractOffer> allActiveOffers) {

    return allActiveOffers
      .stream()
      .filter(o -> o
        .getOfferedItem()
        .getName()
        .equals(offerName))
      .findFirst()
      .orElse(null);
  }

  private void buildAndAddDrinksPanelToMainPanel() {

    drinksPanel = new JPanel();
    weeklyAndDrinksPanel.addTab("Drinks", drinksPanel);

    final int rows = 4;
    final int cols = 5;
    drinksPanel.setLayout(new GridLayout(rows, cols, 3, 3));

    final Collection<ProductOffer> allActiveOffers;
    if (weeklyCategory != null) {
      allActiveOffers = productOfferRep
        .findActiveOffersByIsNotProductCategory(weeklyCategory);
    } else {
      allActiveOffers = productOfferRep.findAllActiveOffers();
    }

    for (final ProductOffer offer : allActiveOffers) {
      drinksPanel.add(buttonFactory.createButtonFor(offer));
    }

    final Collection<VariationOffer> allVariationOffers = variationOfferRepository
      .findAllActiveOffers();

    for (final VariationOffer variationOffer : allVariationOffers) {
      drinksPanel.add(buttonFactory.createButtonFor(variationOffer));
    }

    final Collection<ExtraOffer> allExtraOffers = extraOfferRepository
      .findAllActiveOffers();

    for (final ExtraOffer extraOffer : allExtraOffers) {
      drinksPanel.add(buttonFactory.createButtonFor(extraOffer));
    }

    promoOfferRepository
      .findAllActiveOffers()
      .stream()
      .forEach(o -> drinksPanel.add(buttonFactory.createButtonFor(o)));

  }

  public JPanel createControlButtonsPanel() {
    final JPanel controlButtonsPanel = new JPanel();

    controlButtonsPanel.setLayout(new GridLayout(6, 1, 0, 0));

    addUndoButton(controlButtonsPanel);
    addShowOpenBillsButton(controlButtonsPanel);
    addNewBillButton(controlButtonsPanel);
    addPayButton(controlButtonsPanel);
    addToGoButton(controlButtonsPanel);
    addFreePromotionButton(controlButtonsPanel);
    addTwentyPercentPromotionButton(controlButtonsPanel);
    addStaffConsumptionButton(controlButtonsPanel);
    addKassenstandButton(controlButtonsPanel);

    return controlButtonsPanel;
  }

  private void addUndoButton(final JPanel controlButtonsPanel) {
    final JButton undoButton = new JButton("Undo");
    undoButton.addActionListener(e -> appController.undoLastAction());
    undoButton.setMinimumSize(new Dimension(0, 40));
    controlButtonsPanel.add(undoButton);
  }

  private void addShowOpenBillsButton(final JPanel controlButtonsPanel) {
    controlButtonsPanel.add(buttonFactory
      .createShowOpenBillsButton(showOpenBillsAction));
  }

  private void addNewBillButton(final JPanel controlButtonsPanel) {
    controlButtonsPanel.add(buttonFactory.createNewBillButton());
  }

  private void addPayButton(final JPanel controlButtonsPanel) {
    controlButtonsPanel.add(buttonFactory.createPayButton());
  }

  private void addToGoButton(final JPanel controlButtonsPanel) {
    controlButtonsPanel.add(buttonFactory.createToGoButton());
  }

  private void addFreePromotionButton(final JPanel controlButtonsPanel) {
    controlButtonsPanel.add(buttonFactory.createFreePromotionButton());
  }

  private void addTwentyPercentPromotionButton(final JPanel controlButtonsPanel) {
    controlButtonsPanel.add(buttonFactory.createTwentyPercentPromotionButton());
  }

  private void addStaffConsumptionButton(final JPanel controlButtonsPanel) {
    controlButtonsPanel.add(buttonFactory.createStaffConsumptionButton());
  }

  private void addKassenstandButton(final JPanel controlButtonsPanel) {
    final JButton btnKassenstand = new JButton("Balance");
    btnKassenstand.addActionListener(arg0 -> appController
      .printTodaysTotal());
    btnKassenstand.setMinimumSize(new Dimension(0, 40));
    controlButtonsPanel.add(btnKassenstand);
  }

}
