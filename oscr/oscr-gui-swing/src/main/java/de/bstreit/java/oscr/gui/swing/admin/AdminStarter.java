package de.bstreit.java.oscr.gui.swing.admin;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.bstreit.java.oscr.SpringConfigurationDoesComponentScan;

public class AdminStarter {

	public static void main(String[] args) {

		final ConfigurableApplicationContext context = getContext();

		final SwingAdminController swingAdminController = context
				.getBean(SwingAdminController.class);
		try {
			swingAdminController.launchApplication();
		} catch (final RuntimeException e) {
			if (!"Aborted".equals(e.getMessage())) {
				throw e;
			}
		}
	}

	private static ConfigurableApplicationContext getContext() {
		return new AnnotationConfigApplicationContext(
				SpringConfigurationDoesComponentScan.class);
	}
}
