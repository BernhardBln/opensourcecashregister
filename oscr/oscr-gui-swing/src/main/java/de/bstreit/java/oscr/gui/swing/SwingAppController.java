package de.bstreit.java.oscr.gui.swing;

import java.awt.EventQueue;

import javax.inject.Inject;
import javax.inject.Named;

import de.bstreit.java.oscr.gui.swing.ui.MainWindowController;

@Named
public class SwingAppController {

  private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SwingAppController.class);

  @Inject
  private MainWindowController mainWindowController;


  /**
   * Launch the application on the Swing thread
   */
  public void launchApplication() {
    EventQueue.invokeLater(new Runnable() {

      public void run() {
        try {
          mainWindowController.showMainwindow();

        } catch (Exception e) {
          logger.error("Uncaught exception when trying to show main window.", e);
        }
      }

    });
  }


}
