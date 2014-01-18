package de.bstreit.java.oscr.gui.swing;

import java.awt.EventQueue;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.bstreit.java.oscr.SpringConfiguration;


public class SwingStarter {

  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SwingStarter.class);


  public static void main(String[] args) {

    final ConfigurableApplicationContext context = getContext();
    final SwingAppController controller = context.getBean(SwingAppController.class);

    EventQueue.invokeLater(new Runnable() {

      public void run() {
        try {
          controller.showMainwindow();

        } catch (Exception e) {
          logger.warn("Uncaught exception from mainWindow.show()", e);
        }
      }
    });

  }


  private static ConfigurableApplicationContext getContext() {
    return new AnnotationConfigApplicationContext(SpringConfiguration.class);
  }

}
