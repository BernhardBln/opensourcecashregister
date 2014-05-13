package de.bstreit.java.oscr.gui.swing.cashregister;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.bstreit.java.oscr.SpringConfigurationDoesComponentScan;
import de.bstreit.java.oscr.business.export.IExportService;


public class SwingStarter {


  public static void main(String[] args) {

    final ConfigurableApplicationContext context = getContext();

    final IExportService exportService = context.getBean(IExportService.class);
    exportService.runInBackground();

    final SwingAppController swingAppController = context.getBean(SwingAppController.class);
    swingAppController.launchApplication();
  }


  private static ConfigurableApplicationContext getContext() {
    return new AnnotationConfigApplicationContext(SpringConfigurationDoesComponentScan.class);
  }

}
