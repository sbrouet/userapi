package com.sbr.userapi.exception;

/**
 * Exception when a User is not found<BR/>
 * It is mapped to HTTP Status {@link HttpStatus#NOT_FOUND)} which is the
 * specific HTTP response status when a resource is not found (error 404)
 * 
 * @author sbrouet
 *
 */
public class UserNotFoundException extends Exception {

	private static final long serialVersionUID = -3021372496111817005L;

	public UserNotFoundException(String message) {
		super(message);
	}

	public UserNotFoundException(String message, Throwable t) {
		super(message, t);
	}
}
