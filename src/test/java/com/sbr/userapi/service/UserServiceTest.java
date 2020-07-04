package com.sbr.userapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Example;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sbr.userapi.exception.CouldNotSendMessageBusMessage;
import com.sbr.userapi.exception.InvalidValueException;
import com.sbr.userapi.exception.UserNotFoundException;
import com.sbr.userapi.exception.location.CannotComputeLocationException;
import com.sbr.userapi.exception.location.LocationNotAuthorizedException;
import com.sbr.userapi.messaging.processor.MessageProcessor;
import com.sbr.userapi.model.User;
import com.sbr.userapi.model.messaging.Message;
import com.sbr.userapi.repository.UserRepository;
import com.sbr.userapi.service.location.LocationService;
import com.sbr.userapi.test.TestUtils;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

	private static final Long USER_MICHAEL_ID = 1L;
	private static final Long USER_MARIE_ID = 2L;
	private static final Long USER_CHARLES_ID = 3L;

	private static final String NO_SEARCH_CRITERIA = null;

	/** An IP address outside of Switzerland */
	private static final String NOT_IN_SWITZERLAND_IP = "1.1.1.1";

	private static final String INVALID_FIRST_NAME_TOO_LONG = "way_too_long_firstname_aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

	@TestConfiguration
	static class UserServiceTestContextConfiguration {
		/**
		 * Create a UserService with mocked dependencies
		 * 
		 * @param userRepository   mock is automatically injected by Spring
		 * @param locationService  mock is automatically injected by Spring
		 * @param messageProcessor mock is automatically injected by Spring
		 * @return an initialized {@link UserService}
		 */
		@Bean
		public UserService userService(UserRepository userRepository, LocationService locationService,
				MessageProcessor messageProcessor) {
			return new UserService(userRepository, locationService, messageProcessor);
		}
	}

	@Autowired
	private UserService userService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private LocationService locationService;

	@MockBean
	private MessageProcessor messageProcessor;

	/**
	 * Mock for messageProcessor channel, allows avoiding actual attempts to send
	 * messages through the cloud (that would fail) and verifying the messages sent
	 */
	@Mock
	private MessageChannel mockMessageChannel;

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

		final List<User> allUsers = List.of(userMichael, userMarie);

		// Mock methods on userRepository
		Mockito.when(userRepository.findAll()).thenReturn(allUsers);

		Mockito.when(userRepository.findByFirstName(userMichael.getFirstName())).thenReturn(List.of(userMichael));
		Mockito.when(userRepository.findByFirstName(userMarie.getFirstName())).thenReturn(List.of(userMarie));

		Mockito.when(userRepository.findByFirstName(TestUtils.UNKNOWN_USER_NAME))
				.thenReturn(Collections.<User>emptyList());

		Mockito.when(userRepository.save(any(User.class))).thenReturn(userCharles);

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
		Mockito.when(locationService.isCallerFromSwitzerland(TestUtils.SWISSCOM_CH_IP)).thenReturn(true);
		Mockito.when(locationService.isCallerFromSwitzerland(NOT_IN_SWITZERLAND_IP)).thenReturn(false);

		// Mock the channel used to send messages
		Mockito.when(messageProcessor.mainChannel()).thenReturn(mockMessageChannel);
		Mockito.when(mockMessageChannel.send(any(org.springframework.messaging.Message.class), anyLong()))
				.thenReturn(true);

		// Mock findById and existsById
		Mockito.when(userRepository.findById(userMichael.getId())).thenReturn(Optional.of(userMichael));
		Mockito.when(userRepository.existsById(userMichael.getId())).thenReturn(true);
		Mockito.when(userRepository.existsById(TestUtils.UNKNOWN_USER_ID)).thenReturn(false);

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

	/**
	 * Test method {@link UserService#createUser(User, String)} called fron an IP
	 * address from Switzerland : user should be created and a message should be
	 * sent to the message bus
	 * 
	 * @throws UserNotFoundException         not expected
	 * @throws CouldNotSendMessageBusMessage not expected
	 */
	@Test
	public void createUser_whenValidUserAndClientRequestFromSwitzerland_userShouldBeCreated()
			throws InvalidValueException, CannotComputeLocationException, LocationNotAuthorizedException,
			CouldNotSendMessageBusMessage {
		// Create user
		final User user = userService.createUser(TestUtils.createTestUserCharles(), TestUtils.SWISSCOM_CH_IP);

		// Verify created user
		assertThat(user).isNotNull();
		assertThat(user.getId()).isEqualTo(USER_CHARLES_ID);
		TestUtils.assertEqualsUserCharles(user);

		// Verify the contents of message send to the message bus
		assertMessageWasSentToBus(Message.Type.USER_CREATED, user.getId());
	}

	/**
	 * Test method {@link UserService#createUser(User, String)} called fron an IP
	 * address from Switzerland : method should raise an exception and no message
	 * should be sent to the message bus
	 * 
	 * @throws UserNotFoundException         not expected
	 * @throws CouldNotSendMessageBusMessage not expected
	 */
	@Test
	public void createUser_whenValidUserAndClientRequestNotFromSwitzerland_anExceptionShouldBeRaised()
			throws InvalidValueException, CannotComputeLocationException, LocationNotAuthorizedException {
		assertThrows(LocationNotAuthorizedException.class,
				() -> userService.createUser(TestUtils.createTestUserCharles(), NOT_IN_SWITZERLAND_IP));
		assertNoMessageWasSentToBus();
	}

	@Test
	public void createUser_whenInValidUserDatad_anExceptionShouldBeRaised()
			throws InvalidValueException, CannotComputeLocationException, LocationNotAuthorizedException {
		assertThrows(InvalidValueException.class,
				() -> userService.createUser(buildUserWithInvalidFirstNameTooLong(), TestUtils.SWISSCOM_CH_IP));
		assertNoMessageWasSentToBus();
	}

	/**
	 * Test method {@link UserService#deleteUserById(Long)} with an existing user
	 * 
	 * @throws UserNotFoundException         not expected
	 * @throws CouldNotSendMessageBusMessage not expected
	 */
	@Test
	public void deleteUser_whenValidUserItShouldBeDeleted()
			throws UserNotFoundException, CouldNotSendMessageBusMessage {
		// Find a user
		final List<User> found = userService.findUser(TestUtils.USER_MICHAEL_FIRST_NAME, NO_SEARCH_CRITERIA);
		assertThat(found.size()).isEqualTo(1);

		final User user = found.get(0);

		// Delete user
		userService.deleteUserById(user.getId());

		// Check the delete has actually been called on the repository with the correct
		// user id
		Mockito.verify(userRepository, times(1)).deleteById(user.getId());

		// Verify the contents of message send to the message bus
		assertMessageWasSentToBus(Message.Type.USER_DELETED, user.getId());
	}

	/**
	 * Test method {@link UserService#deleteUserById(Long)} with a NON existing user
	 * : method should raise an exception and no message should be sent to the
	 * message bus
	 * 
	 * @throws UserNotFoundException         not expected
	 * @throws CouldNotSendMessageBusMessage not expected
	 */
	@Test
	public void deleteUser_whenUnknownUserMethodShouldRaiseAndException()
			throws UserNotFoundException, CouldNotSendMessageBusMessage {
		assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(TestUtils.UNKNOWN_USER_ID));

		// Check message has NOT been send to the message bus
		assertNoMessageWasSentToBus();
	}

	private User buildUserWithInvalidFirstNameTooLong() {
		return new User(INVALID_FIRST_NAME_TOO_LONG, "fake@email.com", "dummypassword");
	}

	/**
	 * Assert that a {@link Message} has been sent to the message bus with expected
	 * contents
	 * 
	 * @param expectedMessageType expected {@link Message.Type}
	 * @param expectedUserId      expected user id
	 */
	private void assertMessageWasSentToBus(final Message.Type expectedMessageType, final Long expectedUserId) {
		final ArgumentCaptor<org.springframework.messaging.Message<Message>> argument = ArgumentCaptor
				.forClass(org.springframework.messaging.Message.class);
		Mockito.verify(mockMessageChannel, times(1)).send(argument.capture(), anyLong());
		final Message msgSent = argument.getValue().getPayload();
		assertThat(msgSent.getType()).isEqualTo(expectedMessageType);
		assertThat(msgSent.getUserId()).isEqualTo(expectedUserId);
		assertThat(msgSent.getTimeStamp() > 0);
		Mockito.verifyNoMoreInteractions(mockMessageChannel);
	}

	/**
	 * Assert that no {@link Message} has been sent to the message bus with expected
	 */
	private void assertNoMessageWasSentToBus() {
		Mockito.verify(messageProcessor, times(0)).mainChannel();
		Mockito.verifyNoMoreInteractions(messageProcessor);
		Mockito.verify(mockMessageChannel, times(0)).send(any(), anyLong());
		Mockito.verifyNoMoreInteractions(mockMessageChannel);
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
