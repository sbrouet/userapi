package com.sbr.userapi.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sbr.userapi.model.User;
import com.sbr.userapi.test.TestUtils;

/**
 * Integration test for JPA repository {@link UserRepository}. The repository
 * operates the in-memory H2 database to search and create users
 * 
 * @author sbrouet
 *
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserRepositoryIntegrationTest {

	@Autowired
	private UserRepository userRepository;

	/**
	 * Test {@link UserRepository#findAll()}. Allows to check that context and
	 * database are correctly loaded and that the repository is correctly working
	 * when database data is empty
	 */
	@Test
	public void findAll_whenEmptyDatabaseReturnNonNullEmptyList() {
		final List<User> foundUsers = userRepository.findAll();
		assertThat(foundUsers).isNotNull();
		assertThat(foundUsers.size()).isEqualTo(0);
	}

	/**
	 * Test {@link UserRepository#save(User)}. Check that when a user is created in
	 * database via the save method, it can be found back in database with other
	 * method such as {@link UserRepository#findAll()}
	 */
	@Test
	public void save_whenSaveUserThenItCanBeFoundInDb() {
		final User user = TestUtils.createTestUserMichael();
		userRepository.save(user);

		final List<User> foundUsers = userRepository.findAll();
		assertThat(foundUsers).isNotNull();
		assertThat(foundUsers.size()).isEqualTo(1);
		TestUtils.assertEqualsUserMichael(foundUsers.get(0));
	}

	/**
	 * Test {@link UserRepository#findByFirstName(String)}. Check that when a user
	 * is created in database, it can be found when searching by first name
	 */
	@Test
	public void findByFirstName_whenUserInDbItCanBeFound() {
		final User user = TestUtils.createTestUserMichael();
		userRepository.save(user);

		final List<User> foundUsers = userRepository.findByFirstName(TestUtils.USER_MICHAEL_FIRST_NAME);
		assertThat(foundUsers).isNotNull();
		assertThat(foundUsers.size()).isEqualTo(1);
		TestUtils.assertEqualsUserMichael(foundUsers.get(0));
	}

}
