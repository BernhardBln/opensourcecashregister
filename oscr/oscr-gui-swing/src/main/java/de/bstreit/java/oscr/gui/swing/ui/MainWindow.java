package de.bstreit.java.oscr.gui.swing.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.views.AbstractView;

import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.offers.VariationOffer;
import de.bstreit.java.oscr.business.offers.dao.IProductOfferRepository;
import de.bstreit.java.oscr.business.offers.dao.IVariationOfferRepository;

@Named
public class MainWindow implements IBillDisplay {

	private static final Logger log = LoggerFactory.getLogger(MainWindow.class);

	private JFrame jFrame;

	// Just for testing, remove later
	@Inject
	private IProductOfferRepository productOfferRep;

	@Inject
	private IVariationOfferRepository variationOfferRepository;

	@Inject
	private MainWindowController appController;

	private JTextPane billView;

	private JPanel mainPanel;
	private JPanel drinksPanel;

	private JToggleButton btnToGo;
	private JScrollPane scrollPane;

	@Override
	public void printBill(String billAsText) {
		billView.setText(billAsText);
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	// @Override
	protected JComponent buildPanel() {
		final JSplitPane splitPane = new JSplitPane();
		splitPane.setBounds(100, 100, 757, 555);
		splitPane.setResizeWeight(1.0);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

		billView = new JTextPane();
		billView.setFont(new Font("Courier New", Font.PLAIN, 12));
		billView.setPreferredSize(new Dimension(6, 150));

		scrollPane = new JScrollPane(billView);
		splitPane.setLeftComponent(scrollPane);

		mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension(10, 340));
		mainPanel.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				System.out.println(mainPanel.getSize());
			}
		});

		splitPane.setRightComponent(mainPanel);
		mainPanel.setLayout(new BorderLayout(0, 0));

		buildMainPanel();

		return splitPane;
	}

	private void buildMainPanel() {
		mainPanel.removeAll();

		buildAndAddDrinksPanelToMainPanel();
		buildAndAddControlButtonsPanelToMainPanel();

		mainPanel.validate();
	}

	/**
   * 
   */
	private void buildAndAddControlButtonsPanelToMainPanel() {
		final JPanel controlButtonsPanel = new JPanel();
		mainPanel.add(controlButtonsPanel, BorderLayout.EAST);
		controlButtonsPanel.setLayout(new GridLayout(4, 1, 0, 0));

		final JButton undoButton = new JButton("Undo");
		undoButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				appController.undoLastAction();
			}
		});
		undoButton.setMinimumSize(new Dimension(0, 40));
		controlButtonsPanel.add(undoButton);

		final JButton payButton = new JButton("Pay");
		payButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				appController.closeBill();
			}
		});
		payButton.setMinimumSize(new Dimension(0, 40));
		controlButtonsPanel.add(payButton);

		btnToGo = new JToggleButton("To go");
		btnToGo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				appController.setBillToGo(btnToGo.isSelected());
			}
		});
		btnToGo.setMinimumSize(new Dimension(0, 40));
		controlButtonsPanel.add(btnToGo);

		final JButton btnKassenstand = new JButton("Balance");
		btnKassenstand.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				appController.printTodaysTotal();
			}
		});
		btnKassenstand.setMinimumSize(new Dimension(0, 40));
		controlButtonsPanel.add(btnKassenstand);
	}

	/**
   * 
   */
	private void buildAndAddDrinksPanelToMainPanel() {

		drinksPanel = new JPanel();
		mainPanel.add(drinksPanel);
		drinksPanel.setLayout(new GridLayout(3, 5, 3, 3));

		// get sample product - remove later!
		final Collection<ProductOffer> allOffers = productOfferRep
				.findAllActiveOffers();

		for (final ProductOffer offer : allOffers) {
			createAndAddProductOfferButton(offer);
		}

		final Collection<VariationOffer> allVariationOffers = variationOfferRepository
				.findAllActiveOffers();

		for (final VariationOffer variationOffer : allVariationOffers) {
			createAndAddVariationOfferButton(variationOffer);
		}

	}

	private void createAndAddProductOfferButton(final ProductOffer productOffer) {
		final JButton button = new JButton(productOffer.getLabel());
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				appController.addToBill(productOffer);
			}
		});
		button.setMinimumSize(new Dimension(0, 40));
		drinksPanel.add(button);
	}

	private void createAndAddVariationOfferButton(final VariationOffer offer) {
		final JButton button = new JButton(offer.getLabel());
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				appController.setVariationOffer(offer);
			}
		});
		button.setMinimumSize(new Dimension(0, 40));
		drinksPanel.add(button);
	}

	@Override
	public void show() {
		jFrame = new JFrame();
		jFrame.setBounds(100, 100, 757, 555);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

		// if child of abstractview, use getPanel!
		if (AbstractView.class.isAssignableFrom(getClass())) {
			// jFrame.getContentPane().add(getPanel());
		} else {
			jFrame.getContentPane().add(buildPanel());
		}

		jFrame.setVisible(true);
	}

	@Override
	public void resetGui() {
		resetToggleButtons();
	}

	private void resetToggleButtons() {
		btnToGo.setSelected(false);
	}

}
