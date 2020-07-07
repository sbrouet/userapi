package com.sbr.userapi.model.messaging;

import java.util.Objects;

public class Message {
	// TODO ? add timestamp

	/**
	 * Enumeration of the valid message types that describe a user operation
	 */
	public static enum Type {
		USER_CREATED, USER_DELETED, USER_UPDATED
	}

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
	public Message(Long userId, Type type) {
		super();
		this.userId = userId;
		this.type = type;
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
		return Objects.hash(type, userId);
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
		return type == other.type && Objects.equals(userId, other.userId);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Message [userId=").append(userId).append(", type=").append(type).append("]");
		return builder.toString();
	}

}
