package com.sbr.userapi.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;

import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.CollectionUtils;

import com.sbr.userapi.dto.UserDTO;
import com.sbr.userapi.model.User;

/**
 * Utilities for unit / integration tests
 * 
 * @author sbrouet
 *
 */
public class TestUtils {

	/**
	 * A {@link com.sbr.userapi.model.User#getName()} which does not exist in
	 * database
	 */
	public static final String UNKNOWN_USER_NAME = "I DO NOT EXIST IN DATABASE";

	/**
	 * A {@link com.sbr.userapi.model.User#getEmail()} which does not exist in
	 * database
	 */
	public static final String UNKNOWN_EMAIL = "notexist@noemail.com";

	/**
	 * A {@link com.sbr.userapi.model.User#getId()} which does not exist in database
	 */
	public static final Long UNKNOWN_USER_ID = -999L;

	public static final Long USER_MICHAEL_ID = 1L;
	public static final String USER_MICHAEL_FIRST_NAME = "Michael";
	public static final String USER_MICHAEL_EMAIL = "mfaraday@userapi.sbr";
	public static final String USER_MICHAEL_PASSWORD = "dummyTestPwd1";

	public static final Long USER_MARIE_ID = 2L;
	public static final String USER_MARIE_FIRST_NAME = "Marie";
	public static final String USER_MARIE_EMAIL = "mcurie@userapi.sbr";
	public static final String USER_MARIE_PASSWORD = "dummyTestPwd2";

	public static final Long USER_CHARLES_ID = 3L;
	public static final String USER_CHARLES_FIRST_NAME = "Charles-Edouard";
	public static final String USER_CHARLES_EMAIL = "lecorbusier@userapi.sbr";
	public static final String USER_CHARLES_PASSWORD = "dummyTestPwd3";

	public static User createTestUserMichaelNoId() {
		return new User(USER_MICHAEL_FIRST_NAME, USER_MICHAEL_EMAIL, USER_MICHAEL_PASSWORD);
	}

	public static User createTestUserMichaelWithId() {
		return new User(USER_MICHAEL_ID, USER_MICHAEL_FIRST_NAME, USER_MICHAEL_EMAIL, USER_MICHAEL_PASSWORD);
	}

	public static UserDTO createTestUserDTOMichaelNoId() {
		return new UserDTO(null, USER_MICHAEL_FIRST_NAME, USER_MICHAEL_EMAIL, USER_MICHAEL_PASSWORD);
	}

	public static UserDTO createTestUserDTOMichaelWithId() {
		return new UserDTO(USER_MICHAEL_ID, USER_MICHAEL_FIRST_NAME, USER_MICHAEL_EMAIL, USER_MICHAEL_PASSWORD);
	}

	public static User createTestUserMarieNoId() {
		return new User(USER_MARIE_FIRST_NAME, USER_MARIE_EMAIL, USER_MARIE_PASSWORD);
	}

	public static User createTestUserMarieWithId() {
		return new User(USER_MARIE_ID, USER_MARIE_FIRST_NAME, USER_MARIE_EMAIL, USER_MARIE_PASSWORD);
	}

	public static User createTestUserCharlesNoId() {
		return new User(USER_CHARLES_FIRST_NAME, USER_CHARLES_EMAIL, USER_CHARLES_PASSWORD);
	}

	public static User createTestUserCharlesWithId() {
		return new User(USER_CHARLES_ID, USER_CHARLES_FIRST_NAME, USER_CHARLES_EMAIL, USER_CHARLES_PASSWORD);
	}

	public static void assertEqualsUserMichaelNoId(final User user) {
		assertThat(user.getFirstName()).isEqualTo(USER_MICHAEL_FIRST_NAME);
		assertThat(user.getEmail()).isEqualTo(USER_MICHAEL_EMAIL);
		assertThat(user.getPassword()).isEqualTo(USER_MICHAEL_PASSWORD);
	}

	public static void assertEqualsUserMarieId(final User user) {
		assertThat(user.getFirstName()).isEqualTo(USER_MARIE_FIRST_NAME);
		assertThat(user.getEmail()).isEqualTo(USER_MARIE_EMAIL);
		assertThat(user.getPassword()).isEqualTo(USER_MARIE_PASSWORD);
	}

	public static void assertEqualsUserCharles(final User user) {
		assertThat(user.getFirstName()).isEqualTo(USER_CHARLES_FIRST_NAME);
		assertThat(user.getEmail()).isEqualTo(USER_CHARLES_EMAIL);
		assertThat(user.getPassword()).isEqualTo(USER_CHARLES_PASSWORD);
	}

	/**
	 * Check expectations that user at requested index in a Json list is user
	 * Charles by testing all its fields
	 * 
	 * @param resultActions to chain expectations on
	 * @param atIndex       index in list where the user is expected to be present
	 * @return a {@link ResultActions} that can be used to chain more expectations
	 * @throws Exception
	 */
	public static final ResultActions andExpectAllFieldsInJsonListIsUserCharles(final ResultActions resultActions,
			final int atIndex) throws Exception {
		return andExpectAllFieldsInJsonListIsUser(resultActions, atIndex, USER_CHARLES_ID, USER_CHARLES_FIRST_NAME,
				USER_CHARLES_EMAIL, USER_CHARLES_PASSWORD);
	}

	public static final ResultActions andExpectAllFieldsInJsonListIsUserMichael(final ResultActions resultActions,
			final int atIndex) throws Exception {
		return andExpectAllFieldsInJsonListIsUser(resultActions, atIndex, USER_MICHAEL_ID, USER_MICHAEL_FIRST_NAME,
				USER_MICHAEL_EMAIL, USER_MICHAEL_PASSWORD);
	}

	public static final ResultActions andExpectAllFieldsInJsonListIsUserMarie(final ResultActions resultActions,
			final int atIndex) throws Exception {
		return andExpectAllFieldsInJsonListIsUser(resultActions, atIndex, USER_MARIE_ID, USER_MARIE_FIRST_NAME,
				USER_MARIE_EMAIL, USER_MARIE_PASSWORD);
	}

	/**
	 * Check expectations that user at requested index in a Json list has expected
	 * field values
	 * 
	 * @param resultActions to chain expectations on
	 * @param userId        expected user's id
	 * @param userFirstName expected users's first name
	 * @param userEmail     expected user's email
	 * @param userPassword  expected user's password
	 * @return a {@link ResultActions} that can be used to chain more expectations
	 * @throws Exception
	 */
	public static final ResultActions andExpectAllFieldsInJsonListIsUser(final ResultActions resultActions,
			final int atIndex, final Long userId, final String userFirstName, final String userEmail,
			final String userPassword) throws Exception {
		return resultActions.andExpect(jsonPath("$.[" + atIndex + "].id", is(userId.intValue())))
				.andExpect(jsonPath("$.[" + atIndex + "].firstName", is(userFirstName)))
				.andExpect(jsonPath("$.[" + atIndex + "].email", is(userEmail)))
				.andExpect(jsonPath("$.[" + atIndex + "].password", is(userPassword)));
	}

	/**
	 * Check expectations that user in json object is user Michael
	 * 
	 * @param resultActions to chain expectations on
	 * @return a {@link ResultActions} that can be used to chain more expectations
	 * @throws Exception
	 */
	public static final ResultActions andExpectAllFieldsInJsonObjectIsUserMichael(final ResultActions resultActions)
			throws Exception {
		return andExpectAllFieldsInJsonObjectIsUser(resultActions, USER_MICHAEL_ID, USER_MICHAEL_FIRST_NAME,
				USER_MICHAEL_EMAIL, USER_MICHAEL_PASSWORD);
	}

	/**
	 * Check expectations that user in json object has expected field values
	 * 
	 * @param resultActions to chain expectations on
	 * @param userId        expected user's id
	 * @param userFirstName expected users's first name
	 * @param userEmail     expected user's email
	 * @param userPassword  expected user's password
	 * @return a {@link ResultActions} that can be used to chain more expectations
	 * @throws Exception
	 */
	public static final ResultActions andExpectAllFieldsInJsonObjectIsUser(final ResultActions resultActions,
			final Long userId, final String userFirstName, final String userEmail, final String userPassword)
			throws Exception {
		return resultActions.andExpect(jsonPath("$.id", is(userId.intValue())))
				.andExpect(jsonPath("$.firstName", is(userFirstName))).andExpect(jsonPath("$.email", is(userEmail)))
				.andExpect(jsonPath("$.password", is(userPassword)));
	}

	/**
	 * Check expectations that user in json object contains an
	 * {@link com.sbr.userapi.web.error.ErrorDetails}
	 * 
	 * @param resultActions  to chain expectations on
	 * @param timestamp      expected
	 *                       {@link com.sbr.userapi.web.error.ErrorDetails#getTimestamp()}
	 * @param message        expected
	 *                       {@link com.sbr.userapi.web.error.ErrorDetails#getMessage()}
	 * @param details        expected
	 *                       {@link com.sbr.userapi.web.error.ErrorDetails#getDetails()}
	 * @param requestDetails expected
	 *                       {@link com.sbr.userapi.web.error.ErrorDetails#getRequestDetails()}
	 * 
	 * @return a {@link ResultActions} that can be used to chain more expectations
	 * @throws Exception
	 */
	public static final ResultActions andExpectJsonObjectErrorDetails(final ResultActions resultActions,
			final long timestamp, final String message, final List<String> details, final String requestDetails)
			throws Exception {
		ResultActions newActions = resultActions.andExpect(jsonPath("$.timestamp", is(timestamp)))
				.andExpect(jsonPath("$.message", is(message)))
				.andExpect(jsonPath("$.requestDetails", is(requestDetails)));
		if (!CollectionUtils.isEmpty(details)) {
			int i = 0;
			for (String detailMsg : details) {
				newActions = resultActions.andExpect(jsonPath("$.details[" + i + "]", is(detailMsg)));
				i++;
			}
		}
		return newActions;
	}

}
