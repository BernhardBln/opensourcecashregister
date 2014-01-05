/*
 * Open Source Cash Register
 * 
 * Copyright (C) 2013 Bernhard Streit
 * 
 * This file is part of the Open Source Cash Register program.
 * 
 * Open Source Cash Register is free software: you can redistribute it 
 * and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of 
 * the License, or (at your option) any later version.
 * 
 * Open Source Cash Register is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied 
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *  
 * --------------------------------------------------------------------------
 *  
 * See /licenses/gpl-3.txt for a copy of the GNU GPL.
 * See /README.txt for more information about the software and the author(s).
 * 
 */
package de.bstreit.java.oscr.initialdata;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.orm.jpa.vendor.Database;

import de.bstreit.java.oscr.SpringConfiguration;


/**
 * Load initial data. Throw exception and exit if database already exists and is
 * not empty.
 * 
 * @author streit
 */
@Named
public class LoadInitialDataApp {

  @Inject
  private Database database;
  @Inject
  private DataSource dataSource;


  @Inject
  private DataLoader dataLoader;


  public static void main(String[] args) throws BeansException, SQLException {
    try (
        final AbstractApplicationContext context = new AnnotationConfigApplicationContext(
            SpringConfiguration.class)) {

      context.getBean(LoadInitialDataApp.class).start();

    }
  }

  private void start() throws SQLException {
    allowUserToAbort();

    dataLoader.populateDatabase();

    System.out.println("Done.");

  }

  private void allowUserToAbort() throws SQLException {
    printQuestion();
    askUserForAbort();
  }

  private void printQuestion() throws SQLException {
    System.out.println("");
    System.out.println("Going to fill database " + database + " - "
        + dataSource.getConnection().getMetaData().toString());
    System.out.println("");
    System.out.println("Continue? [Y/N]");
    System.out.println("");
    System.out.print("> ");
  }


  private void askUserForAbort() {
    String choice;

    try (final Scanner scanner = new Scanner(System.in)) {

      while (true) {
        choice = readFromPrompt(scanner);

        if (choice == null) {
          continue;
        }

        if (choice.toUpperCase().equals("Y")) {
          return;
        }

        throw new RuntimeException("Aborted");

      }

    }

  }

  private String readFromPrompt(final Scanner scanner) {

    try {
      return String.valueOf(scanner.next("\\w"));

    } catch (InputMismatchException e) {

      return "";
    }

  }


}
