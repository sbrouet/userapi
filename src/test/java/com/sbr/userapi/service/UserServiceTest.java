package com.sbr.userapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sbr.userapi.exception.InvalidValueException;
import com.sbr.userapi.exception.UserNotFoundException;
import com.sbr.userapi.exception.location.CannotComputeLocationException;
import com.sbr.userapi.exception.location.LocationNotAuthorizedException;
import com.sbr.userapi.model.User;
import com.sbr.userapi.repository.UserRepository;
import com.sbr.userapi.service.location.LocationService;
import com.sbr.userapi.test.TestUtils;

// TODO test all methods (create + mock IP, ...)
// TODO ? user other Mock framework (Powermock)

// TODO ? add Mockito.verify / reset ? see https://github.com/eugenp/tutorials/blob/master/spring-boot-modules/spring-boot/src/test/java/com/baeldung/demo/boottest/EmployeeServiceImplIntegrationTest.java

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

	private static final Long USER_MICHAEL_ID = 1L;
	private static final Long USER_MARIE_ID = 2L;
	private static final Long USER_CHARLES_ID = 3L;

	private static final String NO_SEARCH_CRITERIA = null;

	/** IP of the ricardo.ch website */
	private static final String RICARDO_CH_IP = "104.18.31.124";

	/** An IP adress outside of Switzerland */
	private static final String NOT_IN_SWITZERLAND_IP = "1.1.1.1";

	@TestConfiguration
	static class UserServiceTestContextConfiguration {
		/**
		 * Create a UserService with mocked dependencies
		 * 
		 * @param userRepository  mock is automatically injected by Spring
		 * @param locationService mock is automatically injected by Spring
		 * @return
		 */
		@Bean
		public UserService userService(UserRepository userRepository, LocationService locationService) {
			return new UserService(userRepository, locationService);
		}
	}

	@Autowired
	private UserService userService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private LocationService locationService;

	/**
	 * Create test users and mock methods on {@link UserService}
	 * 
	 * @throws CannotComputeLocationException
	 */
	@BeforeEach
	public void setUp() throws CannotComputeLocationException {
		// Create test users. Force Id as the database calls are mocked so the id will
		// not be automatically set (the objects returned by the repository are the ones
		// prepared here instead of the actual ORM system returned ones)
		final User userMichael = TestUtils.createTestUserMichael();
		userMichael.setId(USER_MICHAEL_ID);

		final User userMarie = TestUtils.createTestUserMarie();
		userMarie.setId(USER_MARIE_ID);

		final User userCharles = TestUtils.createTestUserCharles();
		userCharles.setId(USER_CHARLES_ID);

		List<User> allUsers = Arrays.asList(userMichael, userMarie);

		// Mock methods on userRepository
		Mockito.when(userRepository.findAll()).thenReturn(allUsers);

		Mockito.when(userRepository.findByFirstName(userMichael.getFirstName())).thenReturn(List.of(userMichael));
		Mockito.when(userRepository.findByFirstName(userMarie.getFirstName())).thenReturn(List.of(userMarie));

		Mockito.when(userRepository.findByFirstName(TestUtils.UNKNOWN_USER_NAME))
				.thenReturn(Collections.<User>emptyList());

		Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(userCharles);

		// Mock result of finAll() when called with only first name
		final User exampleUserSearchByFirstNameOnly = new User();
		exampleUserSearchByFirstNameOnly.setFirstName(userMichael.getFirstName());
		final Example<User> exampleSearchByFirstNameOnly = Example.of(exampleUserSearchByFirstNameOnly);
		Mockito.when(userRepository.findAll(argThat(new ExampleMatcher(exampleSearchByFirstNameOnly))))
				.thenReturn(List.of(userMichael));

		// Mock result of finAll() when called with first name AND email
		final User exampleUserSearchByFirstNameAndEmail = new User();
		exampleUserSearchByFirstNameAndEmail.setFirstName(userMarie.getFirstName());
		exampleUserSearchByFirstNameAndEmail.setEmail(userMarie.getEmail());
		final Example<User> exampleSearchByFirstNameAndEmail = Example.of(exampleUserSearchByFirstNameAndEmail);
		Mockito.when(userRepository.findAll(argThat(new ExampleMatcher(exampleSearchByFirstNameAndEmail))))
				.thenReturn(List.of(userMarie));

		// Mock the responses of locationService
		Mockito.when(locationService.isCallerFromSwitzerland(RICARDO_CH_IP)).thenReturn(true);
		Mockito.when(locationService.isCallerFromSwitzerland(NOT_IN_SWITZERLAND_IP)).thenReturn(false);

		// TODO check if needed
		Mockito.when(userRepository.findById(userMichael.getId())).thenReturn(Optional.of(userMichael));

		Mockito.when(userRepository.findById(TestUtils.UNKNOWN_USER_ID)).thenReturn(Optional.empty());
	}

	@Test
	public void getUserById_whenValidIdThenUserShouldBeFound() throws UserNotFoundException {
		final User found = userService.getUserById(USER_MICHAEL_ID);
		assertThat(found.getFirstName()).isEqualTo(TestUtils.USER_MICHAEL_FIRST_NAME);
	}

	@Test
	public void getUserById_whenUnknownIdThenUserShouldBeFound() throws UserNotFoundException {
		assertThrows(UserNotFoundException.class, () -> userService.getUserById(TestUtils.UNKNOWN_USER_ID));
	}

	@Test
	public void findUser_whenValidFirstNameThenUserShouldBeFound() {
		final List<User> found = userService.findUser(TestUtils.USER_MICHAEL_FIRST_NAME, NO_SEARCH_CRITERIA);
		assertThat(found.size()).isEqualTo(1);

		final User michael = found.get(0);
		assertThat(michael.getId()).isEqualTo(USER_MICHAEL_ID);
		TestUtils.assertEqualsUserMichael(michael);
	}

	@Test
	public void findUser_whenInvalidFirstNameThenNoUserShouldNotBeFound() {
		final List<User> found = userService.findUser(TestUtils.UNKNOWN_USER_NAME, NO_SEARCH_CRITERIA);
		assertThat(found.size()).isEqualTo(0);
	}

	@Test
	public void findUser_whenValidFirstNameAndEmailThenUserShouldBeFound() {
		final List<User> found = userService.findUser(TestUtils.USER_MARIE_FIRST_NAME, TestUtils.USER_MARIE_EMAIL);
		assertThat(found.size()).isEqualTo(1);

		final User foundUser = found.get(0);
		assertThat(foundUser.getId()).isEqualTo(USER_MARIE_ID);
		TestUtils.assertEqualsUserMarie(foundUser);
	}

	@Test
	public void findUser_whenValidFirstNameAndInvalidEmailThenUserShouldNotBeFound() {
		final List<User> found = userService.findUser(TestUtils.USER_MARIE_FIRST_NAME, TestUtils.UNKNOWN_EMAIL);
		assertThat(found.size()).isEqualTo(0);
	}

	@Test
	public void findUser_whenInvalidFirstNameAndValidEmailThenUserShouldNotBeFound() {
		final List<User> found = userService.findUser(TestUtils.UNKNOWN_USER_NAME, TestUtils.USER_MARIE_EMAIL);
		assertThat(found.size()).isEqualTo(0);
	}

	@Test
	public void findUser_whenBothInvalidFirstNameAndEmailThenUserShouldNotBeFound() {
		final List<User> found = userService.findUser(TestUtils.UNKNOWN_USER_NAME, TestUtils.UNKNOWN_EMAIL);
		assertThat(found.size()).isEqualTo(0);
	}

	@Test
	public void createUser_whenValidUserAndClientRequestFromSwitzerland_userShouldBeCreated()
			throws InvalidValueException, CannotComputeLocationException, LocationNotAuthorizedException {
		final User user = userService.createUser(TestUtils.createTestUserCharles(), RICARDO_CH_IP);
		assertThat(user).isNotNull();
		assertThat(user.getId()).isEqualTo(USER_CHARLES_ID);
		TestUtils.assertEqualsUserCharles(user);
	}

	@Test
	public void createUser_whenValidUserAndClientRequestNotFromSwitzerland_anExceptionShouldBeRaised()
			throws InvalidValueException, CannotComputeLocationException, LocationNotAuthorizedException {
		assertThrows(LocationNotAuthorizedException.class,
				() -> userService.createUser(TestUtils.createTestUserCharles(), NOT_IN_SWITZERLAND_IP));
	}

	/**
	 * Matcher used to compare two {@link Example<User>} in order for mock to return
	 * a different value depending on the argument received by a method
	 * 
	 * @author sbrouet
	 *
	 */
	public static class ExampleMatcher implements ArgumentMatcher<Example<User>> {

		private Example<User> expected;

		public ExampleMatcher(Example<User> expected) {
			this.expected = expected;
		}

		@Override
		public boolean matches(final Example<User> right) {
			if (null == expected && null == right) {
				return true;
			} else if (null != expected && null == right || null == expected && null != right) {
				return false;
			}

			final User leftUser = expected.getProbe();
			final User rightUser = right.getProbe();

			return Objects.equals(leftUser.getId(), rightUser.getId())
					&& Objects.equals(leftUser.getFirstName(), rightUser.getFirstName())
					&& Objects.equals(leftUser.getEmail(), rightUser.getEmail())
					&& Objects.equals(leftUser.getPassword(), rightUser.getPassword());
		}
	}
}
