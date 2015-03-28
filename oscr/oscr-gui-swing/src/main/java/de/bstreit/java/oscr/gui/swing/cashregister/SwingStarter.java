package de.bstreit.java.oscr.gui.swing.cashregister;

import java.awt.EventQueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import de.bstreit.java.oscr.SpringConfigurationDoesComponentScan;
import de.bstreit.java.oscr.business.export.IExportService;
import de.bstreit.java.oscr.gui.swing.cashregister.ui.MainWindowController;

@SpringBootApplication
public class SwingStarter {

	public static void main(String[] args) {

		SpringApplication app = new SpringApplicationBuilder(
				SpringConfigurationDoesComponentScan.class)
		.addCommandLineProperties(true).headless(false).build();

		final ConfigurableApplicationContext context = app.run(args);

		createAndLaunchExportService(context);
		createAndShowMainWindow(context);
	}

	private static void createAndLaunchExportService(
			final ConfigurableApplicationContext context) {

		final IExportService exportService = context
				.getBean(IExportService.class);
		exportService.runInBackground();
	}

	private static void createAndShowMainWindow(
			final ConfigurableApplicationContext context) {

		MainWindowController mainWindowController = context
				.getBean(MainWindowController.class);

		// Make sure context gets closed when the application is shut down
		mainWindowController.setShutdownHookForLauncher(() -> context.close());

		EventQueue.invokeLater(mainWindowController::showMainwindow);
	}

}
