package com.sbr.userapi.repository;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import com.sbr.userapi.TestConstants;
import com.sbr.userapi.test.container.MariaDbContainerExtension;
import com.sbr.userapi.test.container.MariaDbContainerForIntegrationTests;

/**
 * Integration test for JPA repository {@link UserRepository} that operates an
 * actual containerized MariaDb database to search and create users
 * 
 * @author sbrouet
 *
 */
@ActiveProfiles(TestConstants.SPRING_PROFILE_CONTAINER_DATABASE_MARIADB)
@Sql({ TestConstants.SCHEMA_ITEST_CONTAINER_DATABASE_MARIADB_SQL })
@ExtendWith(MariaDbContainerExtension.class)
@ContextConfiguration(initializers = { UserRepositoryMariaDbDatabaseContainerIT.Initializer.class })
public class UserRepositoryMariaDbDatabaseContainerIT extends UserRepositoryCommonDatabaseIT {

	/**
	 * Update the spring.datasource.url/username/password properties with actual
	 * values from running database container before the application context is
	 * created, so the test does target the database inside the container
	 * 
	 */
	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		@Override
		public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
			final MariaDbContainerForIntegrationTests mariaDBContainer = MariaDbContainerForIntegrationTests
					.getInstance();
			TestPropertyValues
					.of("spring.datasource.url=" + mariaDBContainer.getJdbcUrl(),
							"spring.datasource.username=" + mariaDBContainer.getUsername(),
							"spring.datasource.password=" + mariaDBContainer.getPassword())
					.applyTo(configurableApplicationContext.getEnvironment());
		}
	}

	// All tests methods are in base class
}
