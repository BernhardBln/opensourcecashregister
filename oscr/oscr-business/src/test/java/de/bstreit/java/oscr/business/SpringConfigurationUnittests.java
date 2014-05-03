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
package de.bstreit.java.oscr.business;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.orm.jpa.vendor.Database;

import de.bstreit.java.oscr.business.taxation.TaxInfo;
import de.bstreit.java.oscr.business.taxation.dao.ITaxInfoRepository;

/**
 * Configuration using an in-memory H2 database, for unittests (when mocks are
 * inappropriate) and for integration tests.
 * 
 * @author streit
 */
@Configuration
public class SpringConfigurationUnittests {

  @Bean
  public DataSource dataSource() {
    return new EmbeddedDatabaseBuilder().setType(H2).build();
  }

  @Bean
  protected Database getDatabaseForVendorAdapter() {
    return Database.H2;
  }


  @Bean
  protected TaxInfo getDefaultGlobalTaxInfoForNewBills(ITaxInfoRepository taxInfoRepository) {
    return taxInfoRepository.findByDenotationAndValidToIsNull("in-house");
  }

}
