package com.sbr.userapi.web.error;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.sbr.userapi.exception.CouldNotSendMessageBusMessage;
import com.sbr.userapi.exception.InvalidValueException;
import com.sbr.userapi.exception.UserNotFoundException;
import com.sbr.userapi.exception.location.CannotComputeLocationException;
import com.sbr.userapi.service.time.TimeService;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	private TimeService timeService;

	@Autowired
	public RestResponseEntityExceptionHandler(TimeService timeService) {
		super();
		this.timeService = timeService;
	}

	// 4xx

	@ExceptionHandler(value = { EntityNotFoundException.class, UserNotFoundException.class })
	protected ResponseEntity<ErrorDetails> handleNotFound(final Exception exc, final WebRequest request) {
		return new ResponseEntity<>(buildDefaultErrorDetails(exc, request), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = { InvalidValueException.class, DataIntegrityViolationException.class })
	protected ResponseEntity<ErrorDetails> handleInvalidValueException(final Exception exc, final WebRequest request) {
		return new ResponseEntity<>(buildDefaultErrorDetails(exc, request), HttpStatus.BAD_REQUEST);
	}

	// 5xx

	@ExceptionHandler({ CannotComputeLocationException.class, CouldNotSendMessageBusMessage.class,
			NullPointerException.class, IllegalArgumentException.class, IllegalStateException.class })
	protected ResponseEntity<ErrorDetails> handleOtherServerExceptions(final Exception exc, final WebRequest request) {
		return new ResponseEntity<>(buildDefaultErrorDetails(exc, request), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ErrorDetails buildDefaultErrorDetails(final Exception exc, final WebRequest request) {
		return new ErrorDetails(timeService.getCurrentDateTimeTimestamp(), exc.getMessage(), request.getDescription(false));
	}

}