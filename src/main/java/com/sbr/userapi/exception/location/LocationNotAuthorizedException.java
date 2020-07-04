package com.sbr.userapi.exception.location;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception when the location of a client is not authorized to call the API
 * 
 * @author sbrouet
 *
 */
// TODO find better HttpStatus
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class LocationNotAuthorizedException extends Exception {

	private static final long serialVersionUID = 3646747393963612745L;

	public LocationNotAuthorizedException(String message) {
		super(message);
	}

	public LocationNotAuthorizedException(String message, Throwable t) {
		super(message, t);
	}
}
