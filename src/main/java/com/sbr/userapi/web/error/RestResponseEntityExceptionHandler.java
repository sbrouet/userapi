package com.sbr.userapi.web.error;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.sbr.userapi.exception.CouldNotSendMessageBusMessage;
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

	@ExceptionHandler(value = { EntityNotFoundException.class, UserNotFoundException.class,
			DataIntegrityViolationException.class })
	protected ResponseEntity<ErrorDetails> handleNotFound(final Exception exc, final WebRequest request) {
		return new ResponseEntity<>(buildDefaultErrorDetails(exc, request), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = { ConstraintViolationException.class })
	protected ResponseEntity<ErrorDetails> handleConstraintViolationException(
			final ConstraintViolationException violationExc, final WebRequest request) {
		final ErrorDetails errorDetails = buildErrorDetails(violationExc, request);
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle a {@link TransactionSystemException} that might encapsulate a
	 * {@link ConstraintViolationException}
	 * 
	 * @param exc     the exception that arrived to the exception handler, not
	 *                necessarily the root exception
	 * @param request request that caused the issue(s)
	 * @return
	 */
	@ExceptionHandler(value = { org.springframework.transaction.TransactionSystemException.class })
	protected ResponseEntity<ErrorDetails> handleTransactionExceptions(
			final org.springframework.transaction.TransactionSystemException exc, final WebRequest request) {
		final Throwable rootCause = ExceptionUtils.getRootCause(exc);

		if (null != rootCause && rootCause instanceof ConstraintViolationException) {
			return handleConstraintViolationException((ConstraintViolationException) rootCause, request);
		} else {
			// Default error 500
			return handleOtherServerExceptions(exc, request);
		}
	}

	// 5xx

	@ExceptionHandler({ CannotComputeLocationException.class, CouldNotSendMessageBusMessage.class,
			NullPointerException.class, IllegalArgumentException.class, IllegalStateException.class })
	protected ResponseEntity<ErrorDetails> handleOtherServerExceptions(final Exception exc, final WebRequest request) {
		return new ResponseEntity<>(buildDefaultErrorDetails(exc, request), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ErrorDetails buildDefaultErrorDetails(final Exception exc, final WebRequest request) {
		return new ErrorDetails(timeService.getCurrentDateTimeTimestamp(), exc.getMessage(), null,
				request.getDescription(false));
	}

	/**
	 * Build error with a message containing the detail of a
	 * {@link ConstraintViolationException}
	 * 
	 * @param violationExc contains the violations
	 * @param request      request that caused the issue(s)
	 * @return
	 */
	private ErrorDetails buildErrorDetails(final ConstraintViolationException violationExc, final WebRequest request) {
		final List<String> details = violationExc.getConstraintViolations().stream()
				.map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
				.collect(Collectors.toList());

		return new ErrorDetails(timeService.getCurrentDateTimeTimestamp(),
				"Data has constraint violations (see the details field)", details, request.getDescription(false));
	}

}