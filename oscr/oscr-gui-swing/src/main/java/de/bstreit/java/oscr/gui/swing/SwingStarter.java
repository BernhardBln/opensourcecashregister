package de.bstreit.java.oscr.gui.swing;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.bstreit.java.oscr.SpringConfiguration;


public class SwingStarter {

  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SwingStarter.class);


  public static void main(String[] args) {

    // Context wird im onClose des Hauptfensters geschlossen
    final ConfigurableApplicationContext context = getContext();

    final AppController controller = context.getBean(AppController.class);
    controller.startApplication();
  }


  private static ConfigurableApplicationContext getContext() {
    return new AnnotationConfigApplicationContext(SpringConfiguration.class);
  }

}
