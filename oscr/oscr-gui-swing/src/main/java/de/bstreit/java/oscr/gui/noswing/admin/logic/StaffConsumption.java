package de.bstreit.java.oscr.gui.noswing.admin.logic;

import java.util.Scanner;

import javax.inject.Inject;
import javax.inject.Named;

import de.bstreit.java.oscr.business.export.consumption.ConsumptionExporter;

@Named
public class StaffConsumption implements IAdminBean {

  @Inject
  private ConsumptionExporter consumptionExporter;


  @Override
  public void performTask() {
    consumptionExporter.export();
  }


  @Override
  public void setScanner(Scanner scanner) {

  }

  @Override
  public String toString() {
    return "Staff Consumption for previous month";
  }


}
