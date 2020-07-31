package com.sbr.userapi.model.messaging;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A message sent to the message bus
 * 
 * @author sbrouet
 *
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Message {

	/**
	 * Enumeration of the valid message types that describe a user operation
	 */
	public static enum Type {
		USER_CREATED, USER_DELETED, USER_UPDATED
	}

	private long timeStamp;

	private Long userId;

	private Type type;

	/** Default constructor */
	public Message() {
		super();
	}

	/**
	 * Constructor with all fields set
	 * 
	 * @param userId user id
	 * @param the    type of operation
	 */
	public Message(long timeStamp, Long userId, Type type) {
		super();
		this.timeStamp = timeStamp;
		this.userId = userId;
		this.type = type;
	}

}
