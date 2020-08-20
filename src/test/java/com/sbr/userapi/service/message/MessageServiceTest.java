package com.sbr.userapi.service.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sbr.userapi.exception.CouldNotSendMessageBusMessage;
import com.sbr.userapi.exception.location.CannotComputeLocationException;
import com.sbr.userapi.messaging.processor.MessageProcessor;
import com.sbr.userapi.model.messaging.Message;

/**
 * Unit test for {@link MessageService}
 * 
 * @author sbrouet
 *
 */
@ExtendWith(SpringExtension.class)
public class MessageServiceTest {

	@Autowired
	private MessageService messageService;

	/**
	 * Mock for messageProcessor channel, allows avoiding actual attempts to send
	 * messages through the cloud (that would fail) and verifying the messages sent
	 */
	@Mock
	private MessageChannel mockMessageChannel;

	@MockBean
	private MessageProcessor messageProcessor;

	@TestConfiguration
	static class MessageServiceTestContextConfiguration {
		/**
		 * Create a MessageService with mocked dependencies
		 * 
		 * @param messageProcessor mock is automatically injected by Spring
		 * @return an initialized {@link MessageService}
		 */
		@Bean
		public MessageService messageService(MessageProcessor messageProcessor) {
			return new MessageService(messageProcessor);
		}
	}

	@BeforeEach
	public void setUp() throws CannotComputeLocationException, CouldNotSendMessageBusMessage {
		// Mock the channel used to send messages
		Mockito.when(messageProcessor.mainChannel()).thenReturn(mockMessageChannel);

	}

	@Test
	public void sendMessage_whenToMainChannelIsWorking_NoExceptionShouldBeRaised()
			throws CouldNotSendMessageBusMessage {
		final Message.Type messageType = Message.Type.USER_CREATED;
		final Long userId = 99L;
		Mockito.when(mockMessageChannel.send(any(org.springframework.messaging.Message.class), anyLong()))
				.thenReturn(true);

		messageService.sendMessage(messageType, userId);
		assertMessageBusWasCalled(messageType, userId);
	}

	@Test
	public void sendMessage_whenToMainChannelRetursFalse_exceptionShoulBeRaised() throws CouldNotSendMessageBusMessage {
		final Message.Type messageType = Message.Type.USER_CREATED;
		final Long userId = 99L;
		Mockito.when(mockMessageChannel.send(any(org.springframework.messaging.Message.class), anyLong()))
				.thenReturn(false);

		assertThrows(CouldNotSendMessageBusMessage.class, () -> {
			messageService.sendMessage(messageType, userId);
		});
		assertMessageBusWasCalled(messageType, userId);
	}

	@Test
	public void sendMessage_whenToMainChannelThrowsException_exceptionShoulBeRaised()
			throws CouldNotSendMessageBusMessage {
		final Message.Type messageType = Message.Type.USER_CREATED;
		final Long userId = 99L;
		Mockito.when(mockMessageChannel.send(any(org.springframework.messaging.Message.class), anyLong()))
				.thenThrow(RuntimeException.class);

		assertThrows(CouldNotSendMessageBusMessage.class, () -> {
			messageService.sendMessage(messageType, userId);
		});
		assertMessageBusWasCalled(messageType, userId);
	}

	/**
	 * Assert that a {@link Message} has been sent to the message bus with expected
	 * contents
	 * 
	 * @param expectedMessageType expected {@link Message.Type}
	 * @param expectedUserId      expected user id
	 */
	private void assertMessageBusWasCalled(final Message.Type expectedMessageType, final Long expectedUserId) {
		final ArgumentCaptor<org.springframework.messaging.Message<Message>> argument = ArgumentCaptor
				.forClass(org.springframework.messaging.Message.class);
		Mockito.verify(mockMessageChannel, times(1)).send(argument.capture(), anyLong());
		final Message msgSent = argument.getValue().getPayload();
		assertThat(msgSent.getType()).isEqualTo(expectedMessageType);
		assertThat(msgSent.getUserId()).isEqualTo(expectedUserId);
		assertThat(msgSent.getTimeStamp() > 0);
		Mockito.verifyNoMoreInteractions(mockMessageChannel);
	}
}
