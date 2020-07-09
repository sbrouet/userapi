package com.sbr.userapi.messaging.processor;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * Provides access to a binded output channel. Binded automatically at
 * application startup thanks to the
 * {@link org.springframework.cloud.stream.annotation.EnableBinding
 * EnableBinding} annotation, see application main class
 * {@link com.sbr.userapi.UserApiApplication}
 * 
 * @author sbrouet
 *
 */
public interface MessageProcessor {
	static final String OUTPUT_MESSAGE_CHANNEL_NAME = "com.sbr.userapi.output";

	@Output(OUTPUT_MESSAGE_CHANNEL_NAME)
	MessageChannel mainChannel();
}