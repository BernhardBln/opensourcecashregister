package de.bstreit.java.oscr.gui.swing.cashregister.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import org.springframework.context.annotation.Profile;
import org.w3c.dom.views.AbstractView;

import de.bstreit.java.oscr.gui.swing.cashregister.ui.factories.ButtonPanelFactory;

@Named
@Profile("with-ui")
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

	// @Override
	protected JComponent buildPanel() {
		final JSplitPane mainSplitPane = new JSplitPane();
		mainSplitPane.setBounds(100, 100, 757, 555);
		mainSplitPane.setResizeWeight(1.0);
		mainSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

		setTopPanel(mainSplitPane);

		buttonPanel = buttonPanelFactory.createButtonPanel();

		buttonPanel.validate();

		mainSplitPane.setRightComponent(buttonPanel);

		return mainSplitPane;
	}

	private void setTopPanel(final JSplitPane mainSplitPane) {
		billView = new JTextPane();
		billView.setFont(new Font("Courier New", Font.PLAIN, 12));
		billView.setPreferredSize(new Dimension(6, 150));

		scrollPane = new JScrollPane(billView);

		final JSplitPane splitPane = new JSplitPane();
		// splitPane.setBounds(100, 100, 757, 555);
		splitPane.setResizeWeight(1.0);
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

		splitPane.setLeftComponent(scrollPane);

		splitPane.setRightComponent(buttonPanelFactory
				.createControlButtonsPanel());

		mainSplitPane.setLeftComponent(splitPane);
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void show() {
		jFrame = new JFrame();
		jFrame.setBounds(100, 100, 757, 555);
		jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		jFrame.setExtendedState(Frame.MAXIMIZED_BOTH);

		jFrame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent arg0) {
				appController.notifyShutdown();
			}

		});

		// if child of abstractview, use getPanel!
		if (AbstractView.class.isAssignableFrom(getClass())) {
			// jFrame.getContentPane().add(getPanel());
		} else {
			jFrame.getContentPane().add(buildPanel());
		}

		jFrame.setVisible(true);

		// init
		appController.guiLaunched();
	}

	@Override
	public void scrollToBeginning() {
		billView.setCaretPosition(0);
	}

	@Override
	public void clear() {
		billView.setText("");
	}

}
