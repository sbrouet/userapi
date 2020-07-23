package com.sbr.userapi.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.sbr.userapi.dto.UserDTO;
import com.sbr.userapi.exception.CouldNotSendMessageBusMessage;
import com.sbr.userapi.exception.InvalidValueException;
import com.sbr.userapi.exception.UserNotFoundException;
import com.sbr.userapi.exception.location.CannotComputeLocationException;
import com.sbr.userapi.exception.location.LocationNotAuthorizedException;
import com.sbr.userapi.model.User;
import com.sbr.userapi.service.UserService;
import com.sbr.userapi.web.utils.ControllerUtils;

/**
 * A REST controller which is able to receive client requests through the HTTP
 * protocol and methods. Calls the service layer in order to get the actions
 * actually executed.
 * 
 * @author sbrouet
 *
 */
@RestController
@RequestMapping(UserControllerConstants.REST_API_ROOT_URL)
public class UserController {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserService service;

	/**
	 * ObjectMapper provides functionality for reading and writing JSON. Used for
	 * implementing the PATCH HTTP method
	 */
	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Find all existing users in database
	 * 
	 * @return an HTTP response with a status, the list of users may be empty
	 */
	@GetMapping
	public ResponseEntity<List<UserDTO>> findAllUsers() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("findAllUsers() called");
		}
		final List<User> usersList = service.findAll();
		return new ResponseEntity<List<UserDTO>>(ControllerUtils.convertUserListToUserDTOList(usersList),
				new HttpHeaders(), HttpStatus.OK);
	}

	/**
	 * Get user from database by its {@link User#getId()}. When user is not found,
	 * an exception is thrown which is mapped to {@link HttpStatus#NOT_FOUND}
	 * 
	 * @param id id of the requested user
	 * @return a response with its body containing the found user
	 * @throws UserNotFoundException when user could not be found
	 */
	@GetMapping("/{id}")
	public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id) throws UserNotFoundException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getUserById() id=" + id);
		}
		return new ResponseEntity<UserDTO>(ControllerUtils.convertUserEntityToDTO(service.getUserById(id)),
				new HttpHeaders(), HttpStatus.OK);
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
	 * @return a response with its body containing the list of found users, if any,
	 *         or an empty list
	 */
	@GetMapping(UserControllerConstants.PATH_FIND)
	public ResponseEntity<List<UserDTO>> findUser(
			@RequestParam(name = UserControllerConstants.PARAM_FIRST_NAME, required = false) String firstName,
			@RequestParam(name = UserControllerConstants.PARAM_EMAIL, required = false) String email)
			throws UserNotFoundException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("findUser() firstName=" + firstName + ", email=" + email);
		}
		return new ResponseEntity<List<UserDTO>>(
				ControllerUtils.convertUserListToUserDTOList(service.findUser(firstName, email)), new HttpHeaders(),
				HttpStatus.OK);
	}

	/**
	 * Create a new {@link User} with provided information from the request
	 * body.<BR/>
	 * WARNINGS :
	 * <UL>
	 * <LI>this method is NOT idempotent. When called N times, it will create N
	 * users with N different Ids/URIs</LI>
	 * <LI>only callers with an IP address in Switzerland are authorized to create
	 * new users, otherwise the request is rejected</LI>
	 * </UL>
	 * 
	 * @param newUser the new user to be created. If an id is set on the user it
	 *                will be ignored
	 * @throws a {@link RuntimeException} or subclass is thrown when user could not
	 *           be created
	 * 
	 * @return a response with its body containing the newly created {@link User}
	 *         having an {@link User#getId()} set
	 * @throws InvalidValueException          when at least one user field value is
	 *                                        invalid
	 * @throws CannotComputeLocationException when the location of a client could
	 *                                        not be computed
	 * @throws LocationNotAuthorizedException when the location of the client is not
	 *                                        authorized
	 * @throws CouldNotSendMessageBusMessage  when message could not be sent to the
	 *                                        message bus
	 */
	@PostMapping
	public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO newUser, HttpServletRequest request)
			throws InvalidValueException, CannotComputeLocationException, LocationNotAuthorizedException,
			CouldNotSendMessageBusMessage {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("createUser() user firstName=" + newUser.getFirstName());
		}
		// TODO HACK here : the IP check for local execution -> to be removed and use
		// actual request remote address
		final String callerIP = request.getRemoteAddr();
		// final String callerIP = "195.186.208.154"; // wwww.swisscom.ch

		return new ResponseEntity<UserDTO>(
				ControllerUtils.convertUserEntityToDTO(
						service.createUser(ControllerUtils.convertUserDTOToEntity(newUser), callerIP)),
				new HttpHeaders(), HttpStatus.CREATED);
	}

	/**
	 * Fully update an existing user : all fields are updated (except the user id).
	 * When user is not found by its id, an exception is thrown<BR/>
	 * 
	 * @param user the user containing updated information
	 * @return a response with its body containing the updated user data
	 * @throws UserNotFoundException         when user could not be found
	 * @throws InvalidValueException         when at least one user field value is
	 *                                       invalid
	 * @throws CouldNotSendMessageBusMessage when message could not be sent to the
	 *                                       message bus
	 */
	@PutMapping
	public ResponseEntity<UserDTO> updateExistingUser(@RequestBody UserDTO userDTO)
			throws UserNotFoundException, InvalidValueException, CouldNotSendMessageBusMessage {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("updateExistingUser() user firstName=" + userDTO.getFirstName());
		}
		final User updatedUser = service.updateUser(ControllerUtils.convertUserDTOToEntity(userDTO));
		return new ResponseEntity<UserDTO>(ControllerUtils.convertUserEntityToDTO(updatedUser), new HttpHeaders(),
				HttpStatus.OK);
	}

	/**
	 * Partially update a {@link User} using the HTTP PATCH method
	 * 
	 * @param id id of the requested user
	 * @return a response with status code {@link HttpStatus#OK} and its body
	 *         containing the new user contents
	 * @throws UserNotFoundException         when user could not be found
	 * @throws InvalidValueException         when at least one user field value is
	 *                                       invalid
	 * @throws CouldNotSendMessageBusMessage when message could not be sent to the
	 *                                       message bus
	 */
	@PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
	public ResponseEntity<UserDTO> patchExistingUser(@PathVariable final Long id, @RequestBody JsonPatch patch)
			throws UserNotFoundException, InvalidValueException, CouldNotSendMessageBusMessage {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("patchExistingUser() id=" + id);
		}
		try {
			final User user = service.getUserById(id);
			// Apply patch to user object
			final User userPatched = applyPatchToUser(patch, user);
			// Actual update of user via service
			final User userUpdated = service.updateUser(userPatched);
			return ResponseEntity.ok(ControllerUtils.convertUserEntityToDTO(userUpdated));
		} catch (JsonPatchException | JsonProcessingException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	/**
	 * Compute the result of patching a {@link User} with given patch.<BR/>
	 * WARNING : the user id cannot be patched, this rule is enforced here
	 * 
	 * @param patch        the patch to be applied
	 * @param originalUser the user to be patched
	 * @return
	 * @throws JsonPatchException
	 * @throws JsonProcessingException
	 */
	private User applyPatchToUser(JsonPatch patch, User originalUser)
			throws JsonPatchException, JsonProcessingException {
		JsonNode patched = patch.apply(objectMapper.convertValue(originalUser, JsonNode.class));
		User patchedUser = objectMapper.treeToValue(patched, User.class);
		// User id cannot be patched : force id back to its initial value in case the
		// patch did attempt to modify its value
		patchedUser.setId(originalUser.getId());
		return patchedUser;
	}

	/**
	 * Delete an existing user. When user is not found by its id, a
	 * {@link UserNotFoundException} is thrown and a {@link HttpStatus#NOT_FOUND}
	 * status is sent to caller
	 * 
	 * @param id id of the user to be deleted
	 * @return {@link HttpStatus#NO_CONTENT HttpStatus.NO_CONTENT (204)}
	 * @throws UserNotFoundException         when user could not be found
	 * @throws CouldNotSendMessageBusMessage when message could not be sent to the
	 *                                       message bus
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUserById(@PathVariable("id") Long id)
			throws UserNotFoundException, CouldNotSendMessageBusMessage {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("deleteUserById() id=" + id);
		}
		service.deleteUserById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}