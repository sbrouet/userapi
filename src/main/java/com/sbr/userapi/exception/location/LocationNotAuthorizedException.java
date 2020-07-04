package com.sbr.userapi.exception.location;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception when the location of a client is not authorized to call the
 * API.<BR/>
 * 
 * It is mapped to Http Status 403 Forbidden : "The server understood the
 * request, but is refusing to fulfill it. Authorization will not help and the
 * request SHOULD NOT be repeated. If the request method was not HEAD and the
 * server wishes to make public why the request has not been fulfilled, it
 * SHOULD describe the reason for the refusal in the entity. If the server does
 * not wish to make this information available to the client, the status code
 * 404 (Not Found) can be used instead."
 * 
 * @author sbrouet
 *
 */
// TODO either publish the error in response or use code 404 instead
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class LocationNotAuthorizedException extends Exception {

	private static final long serialVersionUID = 3646747393963612745L;

	public LocationNotAuthorizedException(String message) {
		super(message);
	}

	public LocationNotAuthorizedException(String message, Throwable t) {
		super(message, t);
	}
}
