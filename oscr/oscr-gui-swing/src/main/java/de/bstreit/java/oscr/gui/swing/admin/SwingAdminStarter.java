package de.bstreit.java.oscr.gui.swing.admin;

import java.awt.EventQueue;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.bstreit.java.oscr.SpringConfigurationDoesComponentScan;
import de.bstreit.java.oscr.gui.swing.admin.ui.SwingAdminWindowController;

public class SwingAdminStarter {

  public static void main(String[] args) {
    // We don't need a UI here, so don't let this program explode
    // when started in a non-UI environment (e.g. via SSH)
    System.setProperty("java.awt.headless", "true");

    final ConfigurableApplicationContext context = getContext();

    showMainWindowInEventLoop(context
        .getBean(SwingAdminWindowController.class));
  }

  public static void showMainWindowInEventLoop(
      final SwingAdminWindowController mainWindowController) {

    // Launch the application on the Swing thread
    EventQueue.invokeLater(new Runnable() {

      @Override
      public void run() {
        mainWindowController.showMainwindow();
      }

    });
  }

  private static ConfigurableApplicationContext getContext() {
    return new AnnotationConfigApplicationContext(
        SpringConfigurationDoesComponentScan.class);
  }
}
