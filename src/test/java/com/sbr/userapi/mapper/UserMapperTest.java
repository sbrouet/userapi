package com.sbr.userapi.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.sbr.userapi.dto.UserDTO;
import com.sbr.userapi.model.User;
import com.sbr.userapi.test.TestUtils;

/**
 * Test for class {@link UserMapper}
 * 
 * @author sbrouet
 *
 */
public class UserMapperTest {
	private static final UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);

	@Test
	public void userDTOtoUser() {
		final UserDTO dto = TestUtils.createTestUserDTOMichaelWithId();

		final User entity = USER_MAPPER.userDTOtoUser(dto);

		assertThat(dto.getId()).isEqualTo(entity.getId());
		assertThat(dto.getFirstName()).isEqualTo(entity.getFirstName());
		assertThat(dto.getEmail()).isEqualTo(entity.getEmail());
		assertThat(dto.getPassword()).isEqualTo(entity.getPassword());
	}

	@Test
	public void userToUserDTO() {
		final User entity = TestUtils.createTestUserCharlesWithId();

		final UserDTO dto = USER_MAPPER.userToUserDTO(entity);

		assertThat(entity.getId()).isEqualTo(dto.getId());
		assertThat(entity.getFirstName()).isEqualTo(dto.getFirstName());
		assertThat(entity.getEmail()).isEqualTo(dto.getEmail());
		assertThat(entity.getPassword()).isEqualTo(dto.getPassword());
	}

	/**
	 * Test method {@link UserMapper#userListToUserDTOList(java.util.Collection)} :
	 * ensure that when list contains {@link Users} the result list contains
	 * equivalent DTOs with same values, and users being in same order
	 */
	@Test
	public void userListToUserDTOList_whenListContainsUsers_thenShouldReturnEquivalentDTOs() {
		final User userMichael = TestUtils.createTestUserMichaelWithId();
		final User userMarie = TestUtils.createTestUserMarieWithId();

		final List<User> usersList = List.of(userMichael, userMarie);

		final List<UserDTO> userDTOList = USER_MAPPER.userListToUserDTOList(usersList);

		assertThat(userDTOList.size()).isEqualTo(2);
		assertThat(userDTOList.get(0).getId()).isEqualTo(userMichael.getId());
		assertThat(userDTOList.get(0).getFirstName()).isEqualTo(userMichael.getFirstName());
		assertThat(userDTOList.get(0).getEmail()).isEqualTo(userMichael.getEmail());
		assertThat(userDTOList.get(0).getPassword()).isEqualTo(userMichael.getPassword());
	}

	/**
	 * Test method {@link UserMapper#userListToUserDTOList(java.util.Collection)} :
	 * ensure that when list is empty, the result list is empty too
	 */
	@Test
	public void userListToUserDTOList_whenListIsEmpty_thenShouldReturnEmptyList() {
		final List<UserDTO> userDTOList = USER_MAPPER.userListToUserDTOList(Collections.emptyList());
		assertThat(userDTOList.size()).isEqualTo(0);
	}

	/**
	 * Test method {@link UserMapper#userListToUserDTOList(java.util.Collection)} :
	 * ensure that when list is null, the result list is null and there is no
	 * failure
	 */
	@Test
	public void userListToUserDTOList_whenListIsNull_thenShouldReturnNull() {
		final List<UserDTO> userDTOList = USER_MAPPER.userListToUserDTOList(null);
		assertThat(userDTOList).isNull();
	}
}
