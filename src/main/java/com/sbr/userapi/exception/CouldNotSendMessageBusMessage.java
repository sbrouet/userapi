package com.sbr.userapi.exception;

/**
 * Exception when an attempt to send a message to the message bus has
 * failed<BR/>
 * 
 * @author sbrouet
 *
 */
public class CouldNotSendMessageBusMessage extends Exception {

	private static final long serialVersionUID = -6671767598054332739L;

	public CouldNotSendMessageBusMessage(String message) {
		super(message);
	}

	public CouldNotSendMessageBusMessage(String message, Throwable cause) {
		super(message, cause);
	}
}
