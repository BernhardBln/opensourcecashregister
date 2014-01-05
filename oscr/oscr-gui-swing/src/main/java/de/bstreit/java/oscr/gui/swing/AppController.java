package de.bstreit.java.oscr.gui.swing;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.orm.jpa.vendor.Database;

@Named
public class AppController {

  @Inject
  private Database database;


  public void startApplication() {
    System.out.println("database: " + database);
  }

}
