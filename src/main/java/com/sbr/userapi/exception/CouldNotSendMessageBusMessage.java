package com.sbr.userapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception when an attempt to send a message to the message bus has
 * failed<BR/>
 * 
 * @author sbrouet
 *
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class CouldNotSendMessageBusMessage extends Exception {

	private static final long serialVersionUID = -6671767598054332739L;

	public CouldNotSendMessageBusMessage(String message) {
		super(message);
	}
}
