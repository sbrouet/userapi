package com.sbr.userapi.web.utils;

import java.util.List;

import org.mapstruct.factory.Mappers;

import com.sbr.userapi.dto.UserDTO;
import com.sbr.userapi.mapper.UserMapper;
import com.sbr.userapi.model.User;

/**
 * Utilities for the controller layer
 * 
 * @author sbrouet
 *
 */
public class ControllerUtils {

	/** Mapper between {@link User} and {@link UserDTO} */
	private static final UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);

	/**
	 * <B>Thread safe.</B> Convert a user entity to a DTO by copying all its fields
	 * 
	 * @param user to be converted
	 * @return <code>null</code> when <code>user</code> is <code>null</code>,
	 *         otherwise a newly created {@link UserDTO} instance
	 */
	public static UserDTO convertUserEntityToDTO(final User user) {
		return USER_MAPPER.userToUserDTO(user);
	}

	/**
	 * <B>Thread safe.</B> Convert a user DTO to an entity by copying all its fields
	 * 
	 * @param userDTO to be converted
	 * @return <code>null</code> when <code>user</code> is <code>null</code>,
	 *         otherwise a newly created {@link User} instance
	 */
	public static User convertUserDTOToEntity(final UserDTO userDTO) {
		return USER_MAPPER.userDTOtoUser(userDTO);
	}

	/**
	 * <B>Thread safe.</B> Convert a user entities to a list of DTOs
	 * 
	 * @param users to be converted
	 * @return <code>null</code> when <code>users</code> is <code>null</code>,
	 *         otherwise a newly created list of {@link UserDTO}
	 */
	public static List<UserDTO> convertUserListToUserDTOList(final List<User> users) {
		return USER_MAPPER.userListToUserDTOList(users);
	}
}
