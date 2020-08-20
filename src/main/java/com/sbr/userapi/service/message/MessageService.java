package com.sbr.userapi.service.message;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.sbr.userapi.exception.CouldNotSendMessageBusMessage;
import com.sbr.userapi.messaging.processor.MessageProcessor;
import com.sbr.userapi.model.messaging.Message;

/**
 * Service for sending messages to the message bus
 * 
 * @author sbrouet
 *
 */
@Service
public class MessageService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

	private static final long SERVICE_BUS_SEND_MESSAGE_TIMEOUT_MILLIS = 2000;

	/** Message processor allows sending messages to a service bus */
	private MessageProcessor messageProcessor;

	@Autowired
	public MessageService(MessageProcessor messageProcessor) {
		this.messageProcessor = messageProcessor;
	}

	/**
	 * Send message to the service bus with given message information
	 * 
	 * @param messageType message type see enum {@link Message.Type} for possible
	 *                    values
	 * @param userId      the user id the message is about
	 * @throws CouldNotSendMessageBusMessage when message could not be sent to the
	 *                                       message bus
	 * 
	 */
	public void sendMessage(final Message.Type messageType, final Long userId) throws CouldNotSendMessageBusMessage {
		final Message message = new Message(new Date().getTime(), userId, messageType);
		try {
			if (!messageProcessor.mainChannel().send(message(message), SERVICE_BUS_SEND_MESSAGE_TIMEOUT_MILLIS)) {
				throwCouldNotSendMessageBusMessage(message, null);
			}
		} catch (RuntimeException e) {
			throwCouldNotSendMessageBusMessage(message, e);
		}
	}

	/**
	 * Actually send a message to the service bus
	 * 
	 * @param <T> the message class
	 * @param val message to be sent
	 * @return message sent
	 */
	private static final <T> org.springframework.messaging.Message<T> message(T val) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Sending message to messsage bus : " + val.toString());
		}
		return MessageBuilder.withPayload(val).build();
	}

	private static void throwCouldNotSendMessageBusMessage(final Message message, final Exception e)
			throws CouldNotSendMessageBusMessage {
		throw new CouldNotSendMessageBusMessage("Failed sendind message to message bus: message=" + message, e);
	}

}
