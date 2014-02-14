/*
 * Open Source Cash Register
 * 
 * Copyright (C) 2013, 2014 Bernhard Streit
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
 * See oscr/licenses/gpl-3.txt for a copy of the GNU GPL.
 * See oscr/README.txt for more information about the software and the author(s).
 * 
 */
package de.bstreit.java.oscr;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.vendor.Database;

/**
 * Configuration with file-based H2 database. Can be used to start the
 * application while developing.
 * 
 * @author streit
 */
@Configuration
@PropertySource("classpath:database.properties")
public class SwingSpringConfigurationSandbox {

  private static final Logger logger = LoggerFactory.getLogger(SwingSpringConfigurationSandbox.class);

  @Value("${database.type}")
  private String databaseType;

  @Value("${database.url}")
  private String databaseURL;

  @Value("${database.username}")
  private String username;

  @Value("${database.password}")
  private String password;


  @Bean
  public DataSource dataSource() {
    return new DriverManagerDataSource(databaseURL, username, password);
  }

  @Bean
  protected Database getDatabaseForVendorAdapter() {

    try {

      return Database.valueOf(databaseType);

    } catch (NullPointerException | IllegalArgumentException e) {

      logger.warn("Database type " + databaseType + " is unknown! Check database.properties!");
      return Database.DEFAULT;

    }

  }

  /**
   * Needed to make the {@link Value} annotations work with the property file
   * given by {@link PropertySource}.
   */
  @Bean
  public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

}
