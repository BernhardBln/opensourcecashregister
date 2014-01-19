package de.bstreit.java.oscr.gui.swing;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.bstreit.java.oscr.SpringConfiguration;


public class SwingStarter {


  public static void main(String[] args) {

    final ConfigurableApplicationContext context = getContext();
    final SwingAppController swingAppController = context.getBean(SwingAppController.class);

    swingAppController.launchApplication();
  }


  private static ConfigurableApplicationContext getContext() {
    return new AnnotationConfigApplicationContext(SpringConfiguration.class);
  }

}
