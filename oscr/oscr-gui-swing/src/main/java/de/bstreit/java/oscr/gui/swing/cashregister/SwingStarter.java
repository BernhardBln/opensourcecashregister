package de.bstreit.java.oscr.gui.swing.cashregister;

import java.awt.EventQueue;
import java.util.Map;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.bstreit.java.oscr.SpringConfigurationDoesComponentScan;
import de.bstreit.java.oscr.business.export.IService;
import de.bstreit.java.oscr.gui.swing.cashregister.ui.MainWindowController;

public class SwingStarter {


  private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SwingStarter.class);


  public static void main(String[] args) {

    final ConfigurableApplicationContext context = getContext();

    Map<String, IService> services = context
        .getBeansOfType(IService.class);

    for (IService service : services.values()) {
      logger.info("Starting service " + service);
      service.runInBackground();
    }

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
