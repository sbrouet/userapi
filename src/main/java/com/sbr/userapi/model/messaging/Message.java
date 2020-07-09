package com.sbr.userapi.model.messaging;

import java.util.Objects;

/**
 * A message sent to the message bus
 * 
 * @author sbrouet
 *
 */
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

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(timeStamp, type, userId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Message)) {
			return false;
		}
		Message other = (Message) obj;
		return timeStamp == other.timeStamp && type == other.type && Objects.equals(userId, other.userId);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Message [timeStamp=").append(timeStamp).append(", userId=").append(userId).append(", type=")
				.append(type).append("]");
		return builder.toString();
	}

}
