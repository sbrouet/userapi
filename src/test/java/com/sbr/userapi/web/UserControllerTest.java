package com.sbr.userapi.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.sbr.userapi.dto.UserDTO;
import com.sbr.userapi.exception.UserNotFoundException;
import com.sbr.userapi.model.User;
import com.sbr.userapi.service.UserService;
import com.sbr.userapi.service.time.TimeService;
import com.sbr.userapi.test.JsonUtils;
import com.sbr.userapi.test.TestUtils;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

	private static final String EXCEPTION_MESSAGE_NO_USER_FOUND_ID_1 = "No user found with id [1]";

	private static final long EXCEPTION_TIMESTAMP = 1596256892335L;

	private static final String EXCEPTION_URL_USER_NOT_FOUND_ID_1 = "uri=/users/1";

	@Autowired
	private MockMvc mvc;

	@MockBean
	private UserService userService;

	@MockBean
	private TimeService timeService;

	/**
	 * Test method {@link UserController#getUserById(Long)}. User should be returned
	 * by controller in the response with same values as provided by the (mocked)
	 * user service
	 * 
	 * @throws Exception not expected
	 */
	@Test
	public void getUserById_responseContainsUserDetails() throws Exception {
		final User userMichael = TestUtils.createTestUserMichaelWithId();

		// Mock repository response
		given(userService.getUserById(1L)).willReturn(userMichael);

		final ResultActions resultActions = mvc
				.perform(get(UserControllerConstants.REST_API_ROOT_URL + "/1").accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
		TestUtils.andExpectAllFieldsInJsonObjectIsUserMichael(resultActions);
		verify(userService, VerificationModeFactory.times(1)).getUserById(1L);
	}

	@Test
	public void getUserById_whenUserDoesNotExist_thenResponseIsNotFoundAndHasExceptionDetailsInBody() throws Exception {
		given(timeService.getCurrentDateTimeTimestamp()).willReturn(EXCEPTION_TIMESTAMP);

		// Mock repository response : throw exc
		doThrow(new UserNotFoundException(EXCEPTION_MESSAGE_NO_USER_FOUND_ID_1)).when(userService).getUserById(1L);

		final ResultActions resultActions = mvc
				.perform(get(UserControllerConstants.REST_API_ROOT_URL + "/1").accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

		// Check error details are present in response body
		TestUtils.andExpectJsonObjectErrorDetails(resultActions, EXCEPTION_TIMESTAMP,
				EXCEPTION_MESSAGE_NO_USER_FOUND_ID_1, EXCEPTION_URL_USER_NOT_FOUND_ID_1);

		verify(userService, VerificationModeFactory.times(1)).getUserById(1L);
	}

	@Test
	public void createUser_whenPostUser_thenResponseContainsUserDetails() throws Exception {
		final User userMichael = TestUtils.createTestUserMichaelWithId();

		// Mock repository response
		given(userService.createUser(any(), any())).willReturn(userMichael);

		final ResultActions resultActions = mvc
				.perform(post(UserControllerConstants.REST_API_ROOT_URL).contentType(MediaType.APPLICATION_JSON)
						.content(JsonUtils.toJson(userMichael)))
				.andDo(print()).andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
		TestUtils.andExpectAllFieldsInJsonObjectIsUserMichael(resultActions);
		verify(userService, VerificationModeFactory.times(1)).createUser(any(), any());
	}

	@Test
	public void findAll_whenNoUsers_thenResponseIsOKWithEmptyContent() throws Exception {
		final List<User> usersList = Collections.emptyList();

		// Mock repository response
		given(userService.findAll()).willReturn(usersList);

		final MvcResult mvcResult = mvc
				.perform(get(UserControllerConstants.REST_API_ROOT_URL).contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		assertThat(mvcResult.getResponse().getContentLength()).isEqualTo(0);
		verify(userService, VerificationModeFactory.times(1)).findAll();
	}

	@Test
	public void findAll_whenUsersExist_thenResponseIsOKAndContainsAllTestUsersDetails() throws Exception {
		final User userMichael = TestUtils.createTestUserMichaelWithId();
		final User userMarie = TestUtils.createTestUserMarieWithId();
		final User userCharles = TestUtils.createTestUserCharlesWithId();

		final List<User> allUsers = List.of(userMichael, userMarie, userCharles);

		// Mock repository response
		given(userService.findAll()).willReturn(allUsers);

		final ResultActions resultActions = mvc
				.perform(get(UserControllerConstants.REST_API_ROOT_URL).contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
		// Check users in response
		TestUtils.andExpectAllFieldsInJsonListIsUserMichael(resultActions, 0);
		TestUtils.andExpectAllFieldsInJsonListIsUserMarie(resultActions, 1);
		TestUtils.andExpectAllFieldsInJsonListIsUserCharles(resultActions, 2);

	}

	/**
	 * Test method {@link UserController#findUser(String, String)}. When searching
	 * by firstName only and user exists, it should be returned by controller in the
	 * response with same values as provided by the (mocked) user service
	 * 
	 * @throws Exception not expected
	 * 
	 */
	@Test
	public void findUser_whenByFirstNameOnlyAndUsersExist_thenResponseIsOKAndContainsUserDetails() throws Exception {
		final User userCharles = TestUtils.createTestUserCharlesWithId();

		// Mock repository response
		given(userService.findUser(TestUtils.USER_CHARLES_FIRST_NAME, null)).willReturn(List.of(userCharles));

		final ResultActions resultActions = mvc
				.perform(get(UserControllerConstants.REST_API_ROOT_URL + UserControllerConstants.PATH_FIND)
						.param(UserControllerConstants.PARAM_FIRST_NAME, TestUtils.USER_CHARLES_FIRST_NAME)
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
		// Check user Charles in response
		TestUtils.andExpectAllFieldsInJsonListIsUserCharles(resultActions, 0);
	}

	/**
	 * Test method {@link UserController#findUser(String, String)}. When searching
	 * by firstName only and user does NOT exist, an empty list should be returned
	 * by controller in the response
	 * 
	 * @throws Exception not expected
	 * 
	 */
	@Test
	public void findUser_whenByFirstNameOnlyAndUserDoesNotExist_thenResponseIsOKAndEmptyList() throws Exception {
		// Mock repository response
		given(userService.findUser("DOESNOTEXIST", null)).willReturn(Collections.emptyList());

		mvc.perform(get(UserControllerConstants.REST_API_ROOT_URL + UserControllerConstants.PATH_FIND)
				.param(UserControllerConstants.PARAM_FIRST_NAME, TestUtils.USER_CHARLES_FIRST_NAME)
				.contentType(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(content().string("[]"));
	}

	/**
	 * Test method {@link UserController#findUser(String, String)}. When searching
	 * by email only and user exists, it should be returned by controller in the
	 * response with same values as provided by the (mocked) user service
	 * 
	 * @throws Exception not expected
	 * 
	 */
	@Test
	public void findUser_whenByEmailOnlyAndUsersExist_thenResponseIsOKAndContainsUserDetails() throws Exception {
		final User userCharles = TestUtils.createTestUserCharlesWithId();

		// Mock repository response
		given(userService.findUser(null, TestUtils.USER_CHARLES_EMAIL)).willReturn(List.of(userCharles));

		final ResultActions resultActions = mvc
				.perform(get(UserControllerConstants.REST_API_ROOT_URL + UserControllerConstants.PATH_FIND)
						.param(UserControllerConstants.PARAM_EMAIL, TestUtils.USER_CHARLES_EMAIL)
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
		// Check user Charles in response
		TestUtils.andExpectAllFieldsInJsonListIsUserCharles(resultActions, 0);
	}

	/**
	 * Test method {@link UserController#findUser(String, String)}. When searching
	 * by email only and user does NOT exist, an empty list should be returned by
	 * controller in the response
	 * 
	 * @throws Exception not expected
	 * 
	 */
	@Test
	public void findUser_whenByEmailOnlyAndUserDoesNotExist_thenResponseIsOKAndContainsEmptyList() throws Exception {
		// Mock repository response
		given(userService.findUser(null, "DOESNOTEXIST")).willReturn(Collections.emptyList());

		mvc.perform(get(UserControllerConstants.REST_API_ROOT_URL + UserControllerConstants.PATH_FIND)
				.param(UserControllerConstants.PARAM_EMAIL, "DOESNOTEXIST").contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(content().string("[]"));
	}

	/**
	 * Test method {@link UserController#deleteUserById(Long)}. When user exists,
	 * method should return with status {@link HttpStatus#NO_CONTENT
	 * HttpStatus.NO_CONTENT (204)}
	 * 
	 * @throws Exception not expected
	 */
	@Test
	public void deleteUserById_whenUserExists_thenItShouldBeDeletedAndResponseNoContentAndHasEmptyBody()
			throws Exception {
		// Mock repository response
		doNothing().when(userService).deleteUserById(1L);

		mvc.perform(delete(UserControllerConstants.REST_API_ROOT_URL + "/1").contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isNoContent()).andExpect(content().string(""));
		verify(userService, VerificationModeFactory.times(1)).deleteUserById(1L);
	}

	/**
	 * Test method {@link UserController#deleteUserById(Long)}. When user does NOT
	 * exist, method should return with status {@link HttpStatus#NOT_FOUND
	 * HttpStatus.NOT_FOUND (404)}
	 * 
	 * @throws Exception not expected
	 */
	@Test
	public void deleteUserById_whenUserDoesNotExist_thenResponseIsNotFoundAndHasExceptionDetailsInBody()
			throws Exception {
		given(timeService.getCurrentDateTimeTimestamp()).willReturn(EXCEPTION_TIMESTAMP);

		// Mock repository response : throw exc
		doThrow(new UserNotFoundException(EXCEPTION_MESSAGE_NO_USER_FOUND_ID_1)).when(userService).deleteUserById(1L);

		final ResultActions resultActions = mvc.perform(
				delete(UserControllerConstants.REST_API_ROOT_URL + "/1").contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isNotFound());

		// Check error details are present in response body
		TestUtils.andExpectJsonObjectErrorDetails(resultActions, EXCEPTION_TIMESTAMP,
				EXCEPTION_MESSAGE_NO_USER_FOUND_ID_1, EXCEPTION_URL_USER_NOT_FOUND_ID_1);

		verify(userService, VerificationModeFactory.times(1)).deleteUserById(1L);
	}

	/**
	 * Test method {@link UserController#updateExistingUser(Long, UserDTO)}. All
	 * updated fields should be present also in the response with updated value
	 * 
	 * @throws Exception not expected
	 */
	@Test
	public void updateExistingUser_whenUserExists_thenItShouldBeUpdatedAndResponseStatusOK() throws Exception {
		final String newFirstName = "newFirstName";
		final String newEmail = "new@email.com";
		final String newPassword = "new password";

		final User userMichaelAfterUpdate = TestUtils.createTestUserMichaelWithId();
		userMichaelAfterUpdate.setFirstName(newFirstName);
		userMichaelAfterUpdate.setEmail(newEmail);
		userMichaelAfterUpdate.setPassword(newPassword);

		// User DTO with values changed
		final UserDTO userMichaelDTO = TestUtils.createTestUserDTOMichaelWithId();
		userMichaelDTO.setFirstName(userMichaelAfterUpdate.getFirstName());
		userMichaelDTO.setEmail(userMichaelAfterUpdate.getEmail());
		userMichaelDTO.setPassword(userMichaelAfterUpdate.getPassword());

		// Mock repository response
		given(userService.updateUser(userMichaelAfterUpdate)).willReturn(userMichaelAfterUpdate);

		final ResultActions resultActions = mvc
				.perform(put(UserControllerConstants.REST_API_ROOT_URL + "/" + userMichaelDTO.getId())
						.contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(userMichaelDTO)))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

		TestUtils.andExpectAllFieldsInJsonObjectIsUser(resultActions, userMichaelDTO.getId(), newFirstName, newEmail,
				newPassword);
		verify(userService, VerificationModeFactory.times(1)).updateUser(any());
	}

	@Test
	public void updateExistingUser_whenUserDoesNotExist_thenResponseIsNotFoundAndHasExceptionDetailsInBody()
			throws Exception {
		given(timeService.getCurrentDateTimeTimestamp()).willReturn(EXCEPTION_TIMESTAMP);

		// Mock repository response : throw exc
		doThrow(new UserNotFoundException(EXCEPTION_MESSAGE_NO_USER_FOUND_ID_1)).when(userService)
				.updateUser(any(User.class));

		final UserDTO userMichaelDTO = TestUtils.createTestUserDTOMichaelWithId();

		final ResultActions resultActions = mvc
				.perform(put(UserControllerConstants.REST_API_ROOT_URL + "/" + userMichaelDTO.getId())
						.contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(userMichaelDTO)))
				.andDo(print()).andExpect(status().isNotFound());

		// Check error details are present in response body
		TestUtils.andExpectJsonObjectErrorDetails(resultActions, EXCEPTION_TIMESTAMP,
				EXCEPTION_MESSAGE_NO_USER_FOUND_ID_1, EXCEPTION_URL_USER_NOT_FOUND_ID_1);

		verify(userService, VerificationModeFactory.times(1)).updateUser(any());
	}
}
