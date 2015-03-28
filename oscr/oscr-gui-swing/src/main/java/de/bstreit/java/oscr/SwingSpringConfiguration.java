package de.bstreit.java.oscr;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@EnableAutoConfiguration
@Configuration
// Order is important: TEMPLATES first so they are used as defaults, then local
// property files
// @PropertySource({ "classpath:database.properties.TEMPLATE",
// "classpath:general.properties.TEMPLATE",
// "classpath:database.properties", "classpath:general.properties"
// })
// @ConfigurationProperties
public class SwingSpringConfiguration {

	// private static final Logger logger = LoggerFactory
	// .getLogger(SwingSpringConfiguration.class);
	//
	// @Value("${database.type}")
	// private String databaseType;
	//
	// @Value("${database.url}")
	// private String databaseURL;
	//
	// @Value("${database.username}")
	// private String username;
	//
	// @Value("${database.password}")
	// private String password;
	//
	// @Bean
	// public DataSource dataSource() {
	//
	// BasicDataSource basicDataSource = new BasicDataSource();
	//
	// basicDataSource.setUrl(databaseURL);
	// basicDataSource.setUsername(username);
	// basicDataSource.setPassword(password);
	//
	// return basicDataSource;
	// }
	//
	// @Bean
	// protected Database getDatabaseForVendorAdapter() {
	//
	// try {
	//
	// return Database.valueOf(databaseType);
	//
	// } catch (NullPointerException | IllegalArgumentException e) {
	//
	// logger.warn("Database type " + databaseType
	// + " is unknown! Check database.properties!");
	// return Database.DEFAULT;
	//
	// }
	//
	// }

	/**
	 * Needed to make the {@link Value} annotations work with the property file
	 * given by {@link PropertySource}.
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
		// configurer.setIgnoreUnresolvablePlaceholders(true);
		// configurer.setProperties(new );
		return configurer;
	}
}
