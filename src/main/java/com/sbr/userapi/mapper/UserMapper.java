package com.sbr.userapi.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import com.sbr.userapi.dto.UserDTO;
import com.sbr.userapi.model.User;

@Mapper
public interface UserMapper {
	User userDTOtoUser(UserDTO dto);

	UserDTO userToUserDTO(User user);

	abstract List<UserDTO> userListToUserDTOList(Collection<User> users);
}
