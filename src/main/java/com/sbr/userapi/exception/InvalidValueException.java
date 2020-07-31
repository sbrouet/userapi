package com.sbr.userapi.exception;

/**
 * Exception when a value is invalid according to the application.<BR/>
 * It is mapped to HTTP Status {@link HttpStatus#UNPROCESSABLE_ENTITY)} for
 * stating that the server did understand the content type of the request entity
 * and that the syntax of the request entity is correct, but that the data is
 * not acceptable for the application
 * 
 * @author sbrouet
 *
 */
public class InvalidValueException extends Exception {

	private static final long serialVersionUID = 5637874728467833472L;

	public InvalidValueException(String message) {
		super(message);
	}

	public InvalidValueException(String message, Throwable t) {
		super(message, t);
	}
}
