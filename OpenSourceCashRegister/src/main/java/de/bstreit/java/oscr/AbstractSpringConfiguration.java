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
package de.bstreit.java.oscr;

import javax.sql.DataSource;

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

/**
 * <p>
 * Abstract configuration for spring incl. Hibernate.
 * </p>
 * <p>
 * Uses {@link ComponentScan} to search for services to be instantiated by
 * spring, and
 * {@link LocalContainerEntityManagerFactoryBean#setPackagesToScan(String...)}
 * to search for entities for persistence.
 * </p>
 * 
 * @author streit
 */
@Configuration
@ComponentScan(basePackageClasses = AbstractSpringConfiguration.class)
@EnableJpaRepositories
public abstract class AbstractSpringConfiguration {

	AbstractSpringConfiguration() {
		super();
	}

	@Bean
	public abstract DataSource dataSource();

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
	public JpaVendorAdapter jpaVendorAdapter() {

		final HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();

		hibernateJpaVendorAdapter.setShowSql(false);
		hibernateJpaVendorAdapter.setGenerateDdl(true);
		hibernateJpaVendorAdapter.setDatabase(getDatabaseForVendorAdapter());

		return hibernateJpaVendorAdapter;
	}

	protected abstract Database getDatabaseForVendorAdapter();

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new JpaTransactionManager();
	}

}