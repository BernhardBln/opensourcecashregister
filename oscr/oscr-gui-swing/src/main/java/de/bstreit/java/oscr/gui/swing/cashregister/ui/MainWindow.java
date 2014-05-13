package de.bstreit.java.oscr.gui.swing.cashregister.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

import org.w3c.dom.views.AbstractView;

import de.bstreit.java.oscr.gui.swing.cashregister.ui.factories.ButtonPanelFactory;

@Named
public class MainWindow implements IBillDisplay {

	private JFrame jFrame;

	@Inject
	private MainWindowController appController;

	@Inject
	private ButtonPanelFactory buttonPanelFactory;

	private JTextPane billView;

	private JPanel buttonPanel;
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

		buttonPanel = buttonPanelFactory.createButtonPanel();

		buttonPanel.validate();

		splitPane.setRightComponent(buttonPanel);

		return splitPane;
	}

	@Override
	public void show() {
		jFrame = new JFrame();
		jFrame.setBounds(100, 100, 757, 555);
		jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

		jFrame.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				appController.notifyShutdown();
			}

			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		// if child of abstractview, use getPanel!
		if (AbstractView.class.isAssignableFrom(getClass())) {
			// jFrame.getContentPane().add(getPanel());
		} else {
			jFrame.getContentPane().add(buildPanel());
		}

		jFrame.setVisible(true);
	}

	@Override
	public void scrollToBeginning() {
		billView.setCaretPosition(0);
	}

}
