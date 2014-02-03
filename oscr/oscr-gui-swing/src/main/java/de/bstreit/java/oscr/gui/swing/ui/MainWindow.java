package de.bstreit.java.oscr.gui.swing.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.views.AbstractView;

import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.offers.dao.IProductOfferRepository;
import de.bstreit.java.oscr.business.products.dao.IProductRepository;

@Named
public class MainWindow implements IBillDisplay {

  private static final Logger log = LoggerFactory.getLogger(MainWindow.class);

  private JFrame jFrame;


  // Just for testing, remove later
  @Inject
  private IProductOfferRepository productOfferRep;

  @Inject
  private IProductRepository productRep;

  @Inject
  private MainWindowController appController;


  private JTextPane billView;


  @Override
  public void printBill(String billAsText) {
    billView.setText(billAsText);
  }

  /**
   * @wbp.parser.entryPoint
   */
  // @Override
  protected JComponent buildPanel() {
    JSplitPane splitPane = new JSplitPane();
    splitPane.setBounds(100, 100, 757, 555);
    splitPane.setResizeWeight(1.0);
    splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

    billView = new JTextPane();
    billView.setFont(new Font("Courier New", Font.PLAIN, 12));
    billView.setPreferredSize(new Dimension(6, 150));
    splitPane.setLeftComponent(billView);

    JPanel panel = new JPanel();
    splitPane.setRightComponent(panel);
    panel.setLayout(new BorderLayout(0, 0));

    JPanel drinksPanel = new JPanel();
    panel.add(drinksPanel);
    drinksPanel.setLayout(new GridLayout(3, 5, 3, 3));

    JButton button = new JButton("Single Espresso");
    // get sample product - remove later!
    final ProductOffer espressoOffer = productOfferRep.findActiveOfferByProductName("Espresso");
    button.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        appController.addToBill(espressoOffer);
      }
    });
    button.setMinimumSize(new Dimension(0, 40));
    drinksPanel.add(button);

    JButton button_1 = new JButton("Double Espresso");
    button_1.setMinimumSize(new Dimension(0, 40));
    drinksPanel.add(button_1);


    // get sample product - remove later!
    final ProductOffer cappuOffer = productOfferRep.findActiveOfferByProductName("Cappuccino");
    JButton button_2 = new JButton("Single Cappuccino");
    button_2.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        appController.addToBill(cappuOffer);
      }
    });
    button_2.setMinimumSize(new Dimension(0, 40));
    drinksPanel.add(button_2);

    JButton button_3 = new JButton("Double Cappuccino");
    final ProductOffer hario = productOfferRep.findActiveOfferByProductName("Hario V60 Papierfilter 01 weiß");
    button_3.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        appController.addToBill(hario);
      }
    });
    button_3.setMinimumSize(new Dimension(0, 40));
    drinksPanel.add(button_3);

    JButton button_4 = new JButton("Latte");
    button_4.setMinimumSize(new Dimension(0, 40));
    drinksPanel.add(button_4);

    JButton button_5 = new JButton("Flat White");
    button_5.setMinimumSize(new Dimension(0, 40));
    drinksPanel.add(button_5);

    JPanel controlButtonsPanel = new JPanel();
    panel.add(controlButtonsPanel, BorderLayout.EAST);
    controlButtonsPanel.setLayout(new GridLayout(3, 1, 0, 0));

    JButton payButton = new JButton("Pay");
    payButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        appController.closeBill();
      }
    });
    payButton.setMinimumSize(new Dimension(0, 40));
    controlButtonsPanel.add(payButton);

    final JToggleButton btnToGo = new JToggleButton("To go");
    btnToGo.addChangeListener(new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        appController.setBillToGo(btnToGo.isSelected());
      }
    });
    btnToGo.setMinimumSize(new Dimension(0, 40));
    controlButtonsPanel.add(btnToGo);

    JButton btnKassenstand = new JButton("Balance");
    btnKassenstand.setMinimumSize(new Dimension(0, 40));
    controlButtonsPanel.add(btnKassenstand);

    return splitPane;
  }

  public void show() {
    jFrame = new JFrame();
    jFrame.setBounds(100, 100, 757, 555);
    jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // if child of abstractview, use getPanel!
    if (AbstractView.class.isAssignableFrom(getClass())) {
      // jFrame.getContentPane().add(getPanel());
    } else {
      jFrame.getContentPane().add(buildPanel());
    }

    jFrame.setVisible(true);
  }


}
