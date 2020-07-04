package com.sbr.userapi.exception.location;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception when the location of a client could not be computed. This can occur
 * when the external service which is used is not responding.
 * 
 * @author sbrouet
 *
 */
// TODO find better HttpStatus
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class CannotComputeLocationException extends Exception {

	private static final long serialVersionUID = 2802233886682609393L;

	public CannotComputeLocationException(String message) {
		super(message);
	}

	public CannotComputeLocationException(String message, Throwable t) {
		super(message, t);
	}
}
