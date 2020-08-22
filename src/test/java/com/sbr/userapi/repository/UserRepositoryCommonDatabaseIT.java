package com.sbr.userapi.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.transaction.TestTransaction;

import com.sbr.userapi.model.User;
import com.sbr.userapi.test.TestUtils;

/**
 * Common base class for running integration tests of the JPA repository
 * {@link UserRepository} on an actual database<BR/>
 * Subclasses may need specific configuration
 * 
 * @author sbrouet
 *
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
public abstract class UserRepositoryCommonDatabaseIT {

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
		final User user = TestUtils.createTestUserMichaelNoId();
		userRepository.save(user);

		final List<User> foundUsers = userRepository.findAll();
		assertThat(foundUsers).isNotNull();
		assertThat(foundUsers.size()).isEqualTo(1);
		TestUtils.assertEqualsUserMichaelNoId(foundUsers.get(0));
	}

	@Test
	public void save_whenInvalidDataThenAnExceptionIsThrown_firstName() {
		final User user = TestUtils.createTestUserMichaelNoId();
		user.setFirstName(null);
		doTest_save_whenInvalidDataThenAnExceptionIsThrown(user);
	}

	@Test
	public void save_whenInvalidDataThenAnExceptionIsThrown_email() {
		final User user = TestUtils.createTestUserMichaelNoId();
		user.setEmail(null);
		doTest_save_whenInvalidDataThenAnExceptionIsThrown(user);
	}

	@Test
	public void save_whenInvalidDataThenAnExceptionIsThrown_password() {
		final User user = TestUtils.createTestUserMichaelNoId();
		user.setPassword(null);
		doTest_save_whenInvalidDataThenAnExceptionIsThrown(user);
	}

	private void doTest_save_whenInvalidDataThenAnExceptionIsThrown(User user) {
		assertThrows(javax.validation.ConstraintViolationException.class, () -> {
			userRepository.save(user);
			// WARNING : validations are executed on flushing the session, so force flush
			userRepository.flush();
		});

		assertThat(TestTransaction.isFlaggedForRollback()).isTrue();
	}

	/**
	 * Test {@link UserRepository#findByFirstName(String)}. Check that when a user
	 * is created in database, it can be found when searching by first name
	 */
	@Test
	public void findByFirstName_whenUserInDbItCanBeFound() {
		final User user = TestUtils.createTestUserMichaelNoId();
		userRepository.save(user);

		final List<User> foundUsers = userRepository.findByFirstName(TestUtils.USER_MICHAEL_FIRST_NAME);
		assertThat(foundUsers).isNotNull();
		assertThat(foundUsers.size()).isEqualTo(1);
		TestUtils.assertEqualsUserMichaelNoId(foundUsers.get(0));
	}

}
