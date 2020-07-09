package com.sbr.userapi.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.sbr.userapi.model.User;
import com.sbr.userapi.service.UserService;
import com.sbr.userapi.test.JsonUtils;
import com.sbr.userapi.test.TestUtils;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {
	private static final Long USER_MICHAEL_ID = 1L;
	private static final Long USER_MARIE_ID = 2L;
	private static final Long USER_CHARLES_ID = 3L;

	@Autowired
	private MockMvc mvc;

	@MockBean
	private UserService userService;

	@Test
	public void createUser_whenPostUser_thenResponseContainsUserDetails() throws Exception {
		final User userMichael = TestUtils.createTestUserMichael();

		// Mock repository response
		given(userService.createUser(Mockito.any(), Mockito.any())).willReturn(userMichael);

		mvc.perform(post(UserControllerConstants.REST_API_ROOT_URL).contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtils.toJson(userMichael))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.firstName", is(TestUtils.USER_MICHAEL_FIRST_NAME)))
				.andExpect(jsonPath("$.email", is(TestUtils.USER_MICHAEL_EMAIL)))
				.andExpect(jsonPath("$.password", is(TestUtils.USER_MICHAEL_PASSWORD)));
		verify(userService, VerificationModeFactory.times(1)).createUser(Mockito.any(), Mockito.any());
	}

	@Test
	public void findAll_whenNoUsers_thenResponseIsOKWithEmptyContent() throws Exception {
		final List<User> usersList = Collections.emptyList();

		// Mock repository response
		given(userService.findAll()).willReturn(usersList);

		MvcResult mvcResult = mvc
				.perform(get(UserControllerConstants.REST_API_ROOT_URL).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		assertThat(mvcResult.getResponse().getContentLength()).isEqualTo(0);
		verify(userService, VerificationModeFactory.times(1)).findAll();
	}

	@Test
	public void findAll_whenUsersExist_thenResponseIsOKAndContainsAllTestUsersDetails() throws Exception {
		final User userMichael = TestUtils.createTestUserMichael();
		userMichael.setId(USER_MICHAEL_ID);

		final User userMarie = TestUtils.createTestUserMarie();
		userMarie.setId(USER_MARIE_ID);

		final User userCharles = TestUtils.createTestUserCharles();
		userCharles.setId(USER_CHARLES_ID);

		final List<User> allUsers = Arrays.asList(userMichael, userMarie, userCharles);

		// Mock repository response
		given(userService.findAll()).willReturn(allUsers);

		mvc.perform(get(UserControllerConstants.REST_API_ROOT_URL).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				// Check user Michael
				.andExpect(jsonPath("$.[0].id", is(USER_MICHAEL_ID.intValue())))
				.andExpect(jsonPath("$.[0].firstName", is(TestUtils.USER_MICHAEL_FIRST_NAME)))
				.andExpect(jsonPath("$.[0].email", is(TestUtils.USER_MICHAEL_EMAIL)))
				.andExpect(jsonPath("$.[0].password", is(TestUtils.USER_MICHAEL_PASSWORD)))
				// Check user Marie
				.andExpect(jsonPath("$.[1].id", is(USER_MARIE_ID.intValue())))
				.andExpect(jsonPath("$.[1].firstName", is(TestUtils.USER_MARIE_FIRST_NAME)))
				.andExpect(jsonPath("$.[1].email", is(TestUtils.USER_MARIE_EMAIL)))
				.andExpect(jsonPath("$.[1].password", is(TestUtils.USER_MARIE_PASSWORD)))
				// Check user Charles
				.andExpect(jsonPath("$.[2].id", is(USER_CHARLES_ID.intValue())))
				.andExpect(jsonPath("$.[2].firstName", is(TestUtils.USER_CHARLES_FIRST_NAME)))
				.andExpect(jsonPath("$.[2].email", is(TestUtils.USER_CHARLES_EMAIL)))
				.andExpect(jsonPath("$.[2].password", is(TestUtils.USER_CHARLES_PASSWORD)));
	}

	// TODO junit update patch findXXX
}
