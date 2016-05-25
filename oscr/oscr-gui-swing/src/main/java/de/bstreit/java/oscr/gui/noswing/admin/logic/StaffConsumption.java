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
    System.out.println("#################################################################");
    System.out.println("########################## THIS MONTH ###########################");
    System.out.println("#################################################################");
    System.out.println("");

    consumptionExporter.setThisMonth(true);
    consumptionExporter.export();

    System.out.println("");


    System.out.println("#################################################################");
    System.out.println("########################## LAST MONTH ###########################");
    System.out.println("#################################################################");
    System.out.println("");

    consumptionExporter.setThisMonth(false);
    consumptionExporter.export();

    System.out.println("");
  }


  @Override
  public void setScanner(Scanner scanner) {

  }

  @Override
  public String toString() {
    return "Staff Consumption for this and for previous month";
  }


}
