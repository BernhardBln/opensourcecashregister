package de.bstreit.java.oscr.gui.noswing.admin;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.bstreit.java.oscr.SpringConfigurationDoesComponentScan;

public class AdminStarter {

	public static void main(String[] args) {

		final ConfigurableApplicationContext context = getContext();

		final NoswingAdminController swingAdminController = context
				.getBean(NoswingAdminController.class);
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
