package com.sbr.userapi.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.sbr.userapi.exception.InvalidValueException;
import com.sbr.userapi.exception.UserNotFoundException;
import com.sbr.userapi.model.User;
import com.sbr.userapi.repository.UserRepository;

@Service
public class UserService {
	final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	@Autowired
	UserRepository repository;

	/**
	 * Find all existing users in database
	 * 
	 * @return list of users, may be empty but never <code>null</code>
	 */
	public List<User> findAll() {
		final List<User> users = repository.findAll();
		return null == users ? Collections.<User>emptyList() : users;
	}

	/**
	 * Get user from database by its {@link User#getId()}. When user is not found,
	 * an exception is thrown
	 * 
	 * @param id id of the requested user
	 * @return a {@link User}, never <code>null</code>
	 * @throws UserNotFoundException when user could not be found
	 */
	public User getUserById(final Long id) throws UserNotFoundException {
		Optional<User> user = repository.findById(id);
		if (user.isPresent()) {
			return user.get();
		} else {
			throw new UserNotFoundException("No user found with id [" + id + "]");
		}
	}

	/**
	 * Find all existing users in database with exact matching on user fields. Each
	 * criterion is optional, criteria are combined with an "and" logic. When no
	 * criterion is specified, all users are returned
	 * 
	 * @param firstName first name to search for. <code>null</code> means no
	 *                  criterion on first name field
	 * @arapm email email to search for. <code>null</code> means no criterion on
	 *        email field
	 * @return list of users, may be empty but never <code>null</code>
	 */
	public List<User> findUser(final String firstName, final String email) {
		final User exampleUser = new User(firstName, email, null /* no search by password */);
		return repository.findAll(Example.of(exampleUser));
	}

	/**
	 * Create a new {@link User} with provided information
	 * 
	 * @param newUser the new user to be created. If an id is set on the user it
	 *                will be ignored
	 * @throws a {@link RuntimeException} or subclass is thrown when user could not
	 *           be created
	 * @return the newly created {@link User} having an {@link User#getId()} set
	 * @throws InvalidValueException when at least one user field value is invalid
	 */
	public User createUser(final User newUser) throws InvalidValueException {
		// Id is computed by the ORM -> force id to null here to avoid a value being set
		// by caller and let the ORM provide one
		newUser.setId(null);
		validateUserFieldsExceptId(newUser);
		final User createdUser = repository.save(newUser);
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("New user created : " + createdUser.toString());
		}
		return createdUser;
	}

	/**
	 * Fully update an existing user : all fields are updated (except the user id).
	 * When user is not found by its id, an exception is thrown<BR/>
	 * WARNING : the user id cannot be updated
	 * 
	 * @param user the user containing updated information
	 * @return the updated user
	 * @throws UserNotFoundException when user could not be found
	 * @throws InvalidValueException when at least one user field value is invalid
	 */
	public User updateUser(final User user) throws UserNotFoundException, InvalidValueException {
		final User existingUser = getUserById(user.getId());

		// Update fields EXCEPT the id
		existingUser.setFirstName(user.getFirstName());
		existingUser.setEmail(user.getEmail());
		existingUser.setPassword(user.getPassword());

		validateUserFieldsExceptId(user);

		// Update user in database
		return repository.save(existingUser);
	}

	/**
	 * Delete an existing user. When user is not found by its id, a
	 * {@link UserNotFoundException} is thrown
	 * 
	 * @param id id of the user to be deleted
	 * @throws UserNotFoundException when user could not be found
	 */
	public void deleteUserById(final Long id) throws UserNotFoundException {
		if (!repository.existsById(id)) {
			throw new UserNotFoundException("No user found with id [" + id + "]");
		}
		repository.deleteById(id);
	}

	/**
	 * Validate that user fields are valid according to data model constraints,
	 * except the {@link User#getId()} field. Ensures that mandatory fields have a
	 * value and that all fields size is within data model limits
	 * 
	 * @param user to be checked
	 * @throws InvalidValueException when at least one user field value is invalid
	 */
	private static void validateUserFieldsExceptId(final User user) throws InvalidValueException {
		if (StringUtils.isEmpty(user.getFirstName()) || StringUtils.length(user.getFirstName()) > 100) {
			throw new InvalidValueException("First name must be not empty and less than 100 characters");
		}
		if (StringUtils.isEmpty(user.getEmail()) || StringUtils.length(user.getEmail()) > 50) {
			throw new InvalidValueException("Email must be not empty and less than 50 characters");
		}
		if (StringUtils.isEmpty(user.getPassword()) || StringUtils.length(user.getPassword()) > 50) {
			throw new InvalidValueException("Password must be not empty and less than 50 characters");
		}
	}
}