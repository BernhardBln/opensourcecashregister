package de.bstreit.java.oscr.gui.swing.cashregister.ui.factories;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import de.bstreit.java.oscr.business.offers.ExtraOffer;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.offers.VariationOffer;
import de.bstreit.java.oscr.business.offers.dao.IExtraOfferRepository;
import de.bstreit.java.oscr.business.offers.dao.IProductOfferRepository;
import de.bstreit.java.oscr.business.offers.dao.IVariationOfferRepository;
import de.bstreit.java.oscr.business.products.category.ProductCategory;
import de.bstreit.java.oscr.business.products.category.dao.IProductCategoryRepository;
import de.bstreit.java.oscr.gui.swing.cashregister.ui.MainWindowController;

@Named
public class ButtonPanelFactory {

  @Inject
  private MainWindowController appController;

  @Inject
  private IProductOfferRepository productOfferRep;

  @Inject
  private IExtraOfferRepository extraOfferRepository;

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

  private ProductCategory weeklyCategory;

  private JPanel buttonPanel;
  private JPanel weeklyAndDrinksPanel;
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

			buildAndAddWeeklyPanelToMainPanel();
			buildAndAddDrinksPanelToMainPanel();

      buildAndAddWeeklyPanelToMainPanel();
      buildAndAddDrinksPanelToMainPanel();
      buildAndAddControlButtonsPanelToMainPanel();

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

    weeklyAndDrinksPanel = new JPanel();
    weeklyAndDrinksPanel.setLayout(new BoxLayout(weeklyAndDrinksPanel,
        BoxLayout.X_AXIS));

    buttonPanel.add(weeklyAndDrinksPanel, BorderLayout.CENTER);
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
    weeklyAndDrinksPanel.add(weeklyPanel);

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

  private void buildAndAddDrinksPanelToMainPanel() {

    drinksPanel = new JPanel();
    weeklyAndDrinksPanel.add(drinksPanel);

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
		addStaffConsumptionButton(controlButtonsPanel);
		addKassenstandButton(controlButtonsPanel);

		return controlButtonsPanel;
	}

	private void addUndoButton(final JPanel controlButtonsPanel) {
		final JButton undoButton = new JButton("Undo");
		undoButton.addActionListener(e-> appController.undoLastAction());
		undoButton.setMinimumSize(new Dimension(0, 40));
		controlButtonsPanel.add(undoButton);
	}

	private void addShowOpenBillsButton(final JPanel controlButtonsPanel) {
		final JButton payButton = new JButton(showOpenBillsAction);
		payButton.setMinimumSize(new Dimension(0, 40));
		controlButtonsPanel.add(payButton);
	}

	private void addNewBillButton(final JPanel controlButtonsPanel) {
		final JButton payButton = new JButton("New Bill");
		payButton.addActionListener(e -> appController.newBill());
		payButton.setMinimumSize(new Dimension(0, 40));
		controlButtonsPanel.add(payButton);
	}

	private void addPayButton(final JPanel controlButtonsPanel) {
		final JButton payButton = new JButton("Pay");
		payButton.addActionListener(e -> appController.closeBill());
		payButton.setMinimumSize(new Dimension(0, 40));
		controlButtonsPanel.add(payButton);
	}

	private void addToGoButton(final JPanel controlButtonsPanel) {
		controlButtonsPanel.add(buttonFactory.createToGoButton());
	}

	private void addFreePromotionButton(JPanel controlButtonsPanel) {
		controlButtonsPanel.add(buttonFactory.createFreePromotionButton());
	}

	private void addStaffConsumptionButton(JPanel controlButtonsPanel) {
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
