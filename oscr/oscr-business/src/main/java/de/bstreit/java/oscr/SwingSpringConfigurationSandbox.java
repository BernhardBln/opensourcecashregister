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

@Configuration
@PropertySource("classpath:database.properties")
public class SwingSpringConfigurationSandbox {

	private static final Logger logger = LoggerFactory
			.getLogger(SwingSpringConfigurationSandbox.class);

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

			logger.warn("Database type " + databaseType
					+ " is unknown! Check database.properties!");
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
