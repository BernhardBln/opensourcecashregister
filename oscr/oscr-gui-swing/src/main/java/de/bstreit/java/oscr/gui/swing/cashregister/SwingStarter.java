package de.bstreit.java.oscr.gui.swing.cashregister;

import java.awt.EventQueue;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.bstreit.java.oscr.SpringConfigurationDoesComponentScan;
import de.bstreit.java.oscr.business.export.IExportService;
import de.bstreit.java.oscr.gui.swing.cashregister.ui.MainWindowController;

public class SwingStarter {

	public static void main(String[] args) {

		final ConfigurableApplicationContext context = getContext();

		final IExportService exportService = context
				.getBean(IExportService.class);
		exportService.runInBackground();

		showMainWindowInEventLoop(context.getBean(MainWindowController.class));
	}

	public static void showMainWindowInEventLoop(
			final MainWindowController mainWindowController) {

		// Launch the application on the Swing thread
		EventQueue.invokeLater(() -> mainWindowController.showMainwindow());
	}

	private static ConfigurableApplicationContext getContext() {
		return new AnnotationConfigApplicationContext(
				SpringConfigurationDoesComponentScan.class);
	}

}
