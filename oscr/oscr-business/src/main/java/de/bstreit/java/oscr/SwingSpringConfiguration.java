package de.bstreit.java.oscr;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.orm.jpa.vendor.Database;

@Configuration
// Order is important: TEMPLATES first so they are used as defaults, then local
// property files
@PropertySource({ "classpath:database.properties.TEMPLATE", "classpath:general.properties.TEMPLATE",
    "classpath:database.properties", "classpath:general.properties"
})
public class SwingSpringConfiguration {

  private static final Logger logger = LoggerFactory
      .getLogger(SwingSpringConfiguration.class);

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

    BasicDataSource basicDataSource = new BasicDataSource();

    basicDataSource.setUrl(databaseURL);
    basicDataSource.setUsername(username);
    basicDataSource.setPassword(password);

    return basicDataSource;
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
    PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
    // configurer.setIgnoreUnresolvablePlaceholders(true);
    // configurer.setProperties(new );
    return configurer;
  }
}
