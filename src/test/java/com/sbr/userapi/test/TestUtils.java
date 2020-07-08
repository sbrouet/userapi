package com.sbr.userapi.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.sbr.userapi.model.User;

/**
 * Utilities for unit / integration tests
 * 
 * @author sbrouet
 *
 */
public class TestUtils {

	/** IP of the Swisscom.ch website */
	public static final String SWISSCOM_CH_IP = "195.186.208.154";
	
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

	public static final String USER_MICHAEL_FIRST_NAME = "Michael";
	public static final String USER_MICHAEL_EMAIL = "mfaraday@userapi.sbr";
	public static final String USER_MICHAEL_PASSWORD = "dummyPwd1";

	public static final String USER_MARIE_FIRST_NAME = "Marie";
	public static final String USER_MARIE_EMAIL = "mcurie@userapi.sbr";
	public static final String USER_MARIE_PASSWORD = "dummyPwd2";

	public static final String USER_CHARLES_FIRST_NAME = "Charles-Edouard";
	public static final String USER_CHARLES_EMAIL = "lecorbusier@userapi.sbr";
	public static final String USER_CHARLES_PASSWORD = "dummyPwd3";

	public static User createTestUserMichael() {
		return new User(USER_MICHAEL_FIRST_NAME, USER_MICHAEL_EMAIL, USER_MICHAEL_PASSWORD);
	}

	public static User createTestUserMarie() {
		return new User(USER_MARIE_FIRST_NAME, USER_MARIE_EMAIL, USER_MARIE_PASSWORD);
	}

	public static User createTestUserCharles() {
		return new User(USER_CHARLES_FIRST_NAME, USER_CHARLES_EMAIL, USER_CHARLES_PASSWORD);
	}

	public static void assertEqualsUserMichael(final User user) {
		assertThat(user.getFirstName()).isEqualTo(USER_MICHAEL_FIRST_NAME);
		assertThat(user.getEmail()).isEqualTo(USER_MICHAEL_EMAIL);
		assertThat(user.getPassword()).isEqualTo(USER_MICHAEL_PASSWORD);
	}

	public static void assertEqualsUserMarie(final User user) {
		assertThat(user.getFirstName()).isEqualTo(USER_MARIE_FIRST_NAME);
		assertThat(user.getEmail()).isEqualTo(USER_MARIE_EMAIL);
		assertThat(user.getPassword()).isEqualTo(USER_MARIE_PASSWORD);
	}

	public static void assertEqualsUserCharles(final User user) {
		assertThat(user.getFirstName()).isEqualTo(USER_CHARLES_FIRST_NAME);
		assertThat(user.getEmail()).isEqualTo(USER_CHARLES_EMAIL);
		assertThat(user.getPassword()).isEqualTo(USER_CHARLES_PASSWORD);
	}

}
