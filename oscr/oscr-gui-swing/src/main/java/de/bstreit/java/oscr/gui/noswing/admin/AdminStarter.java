package de.bstreit.java.oscr.gui.noswing.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import de.bstreit.java.oscr.SpringConfigurationDoesComponentScan;

@SpringBootApplication
public class AdminStarter {

	public static void main(String[] args) {

		final ApplicationContext context = SpringApplication.run(
				SpringConfigurationDoesComponentScan.class, args);

		context.getBean(NoswingAdminController.class).launchApplication();
	}

}
