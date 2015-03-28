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

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.bstreit.java.oscr.business.taxation.TaxInfo;
import de.bstreit.java.oscr.business.taxation.TaxUsage;
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
@EntityScan
@EnableJpaRepositories
@EnableTransactionManagement
public class SpringConfigurationDoesComponentScan {

	// @Inject
	// private Database database;

	@Value("${defaultGlobalTax}")
	private TaxUsage defaultGlobalTax;

	// @Bean
	// public LocalContainerEntityManagerFactoryBean entityManagerFactory(
	// DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
	//
	// final LocalContainerEntityManagerFactoryBean lef = new
	// LocalContainerEntityManagerFactoryBean();
	//
	// lef.setDataSource(dataSource);
	// lef.setJpaVendorAdapter(jpaVendorAdapter);
	//
	// // Packages to scan for hibernate entities
	// final String thisPackageAndSubpackages = this.getClass().getPackage()
	// .getName();
	// lef.setPackagesToScan(thisPackageAndSubpackages);
	//
	// return lef;
	// }

	//
	// @Bean
	// public JpaVendorAdapter jpaVendorAdapter() {
	//
	// final HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new
	// HibernateJpaVendorAdapter();
	//
	// hibernateJpaVendorAdapter.setShowSql(false);
	// hibernateJpaVendorAdapter.setGenerateDdl(true);
	// hibernateJpaVendorAdapter.setDatabase(database);
	//
	// return hibernateJpaVendorAdapter;
	// }
	//
	// @Bean
	// public PlatformTransactionManager transactionManager() {
	// return new JpaTransactionManager();
	// }

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

	/**
	 * There is typically standard and reduced VAT. One of them is the default
	 * for new bills, and returned here.
	 * <p>
	 * The other one is returned by
	 * {@link #getOtherGlobalTaxInfo(ITaxInfoRepository)}.
	 * <p>
	 * In the GUI, the user can toggle between the two of them.
	 */
	@Bean(name = "defaultGlobalTaxInfoForNewBills")
	protected TaxInfo getDefaultGlobalTaxInfoForNewBills(
			ITaxInfoRepository taxInfoRepository) {

		return taxInfoRepository.findByTaxUsage(defaultGlobalTax);
	}

	/**
	 * There is typically standard and reduced VAT. One of them is the default
	 * for new bills, and returned by
	 * {@link #getDefaultGlobalTaxInfoForNewBills(ITaxInfoRepository)}.
	 * <p>
	 * The other one is returned here.
	 * <p>
	 * In the GUI, the user can toggle between the two of them.
	 */
	@Bean(name = "otherGlobalTaxInfo")
	protected TaxInfo getOtherGlobalTaxInfo(ITaxInfoRepository taxInfoRepository) {

		if (defaultGlobalTax == TaxUsage.GLOBAL_REDUCED_VAT) {
			return taxInfoRepository
					.findByTaxUsage(TaxUsage.GLOBAL_STANDARD_VAT);
		} else {
			return taxInfoRepository
					.findByTaxUsage(TaxUsage.GLOBAL_REDUCED_VAT);
		}

	}
}