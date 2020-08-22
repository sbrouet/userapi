package com.sbr.userapi.repository;

import org.springframework.test.context.ActiveProfiles;

import com.sbr.userapi.TestConstants;

/**
 * Integration test for JPA repository {@link UserRepository} that operates on
 * an in-memory H2 database to search and create users
 * 
 * @author sbrouet
 *
 */
@ActiveProfiles(TestConstants.SPRING_PROFILE_IN_MEMORY_DATABASE_H2)
public class UserRepositoryInMemoryH2DatabaseIT extends UserRepositoryCommonDatabaseIT {
	// All tests methods are in base class
}
