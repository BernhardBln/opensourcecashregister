package de.bstreit.java.oscr.gui.swing;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.bstreit.java.oscr.SpringConfiguration;


public class SwingStarter {

  public static void main(String[] args) {

    try (final ConfigurableApplicationContext context = getContext()) {

      final AppController controller = context.getBean(AppController.class);
      controller.startApplication();

    }

  }


  private static ConfigurableApplicationContext getContext() {
    return new AnnotationConfigApplicationContext(SpringConfiguration.class);
  }

}
