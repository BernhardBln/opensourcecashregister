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

import java.util.Currency;
import java.util.Locale;

import javax.inject.Inject;
import javax.sql.DataSource;

import liquibase.integration.spring.SpringLiquibase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.bstreit.java.oscr.business.taxation.TaxInfo;
import de.bstreit.java.oscr.business.taxation.dao.ITaxInfoRepository;

/**
 * <p>
 * Spring configuration. Append your own configuration by placing a
 * configuration somewhere inside of this package or below.
 * </p>
 * <p>
 * You will need to provide at least a {@link Database} and a {@link DataSource}
 * .
 * </p>
 * 
 * @author streit
 */
@Configuration
@ComponentScan
@EnableJpaRepositories
@EnableTransactionManagement
public class SpringConfigurationDoesComponentScan {

  @Inject
  private Database database;


  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
      JpaVendorAdapter jpaVendorAdapter) {

    final LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();

    lef.setDataSource(dataSource);
    lef.setJpaVendorAdapter(jpaVendorAdapter);

    // Packages to scan for hibernate entities
    final String thisPackageAndSubpackages =
        this.getClass().getPackage().getName();
    lef.setPackagesToScan(thisPackageAndSubpackages);

    return lef;
  }


  @Bean
  public SpringLiquibase springLiquibase(DataSource dataSource) {
    SpringLiquibase springLiquibase = new SpringLiquibase();

    springLiquibase.setChangeLog("classpath:liquibase/db-changelog.xml");
    springLiquibase.setDataSource(dataSource);

    return springLiquibase;
  }

  @Bean
  public JpaVendorAdapter jpaVendorAdapter() {

    final HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();

    hibernateJpaVendorAdapter.setShowSql(false);
    hibernateJpaVendorAdapter.setGenerateDdl(true);
    hibernateJpaVendorAdapter.setDatabase(database);

    return hibernateJpaVendorAdapter;
  }


  @Bean
  public PlatformTransactionManager transactionManager() {
    return new JpaTransactionManager();
  }

  /**
   * TODO [11]: Maybe make this a setting that is saved in the database?
   * 
   * @return the default currency
   */
  @Bean
  public Currency getDefaultCurrency() {
    return Currency.getInstance(getLocale());
  }

  @Bean
  public Locale getLocale() {
    return Locale.getDefault();
  }

  @Bean(name = "togoTaxInfo")
  public TaxInfo togoTaxInfo(ITaxInfoRepository taxInfoRepository) {
    return taxInfoRepository.findByDenotationAndValidToIsNull("to go");
  }


  @Bean(name = "defaultGlobalTaxInfoForNewBills")
  protected TaxInfo getDefaultGlobalTaxInfoForNewBills(ITaxInfoRepository taxInfoRepository) {
    return taxInfoRepository.findByDenotationAndValidToIsNull("to go");
  }

}