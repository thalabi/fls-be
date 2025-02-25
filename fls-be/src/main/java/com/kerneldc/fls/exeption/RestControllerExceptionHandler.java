package com.kerneldc.fls.exeption;

import java.sql.SQLException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class RestControllerExceptionHandler /*extends ResponseEntityExceptionHandler*/ {

	private static final String LOG_MESSAGE_PREFIX = "Exception handled by RestControllerExceptionHandler.";
	protected static final String MESSAGE_SEPERATOR = "; ";
	protected record ErrorBody(String message, String stackTrace) {}

	@ExceptionHandler(IllegalArgumentException.class)
	protected ResponseEntity<ErrorBody> handleIllegalArgumentException(IllegalArgumentException illegalArgumentException, WebRequest request) {
		illegalArgumentException.printStackTrace();

		LOGGER.info(LOG_MESSAGE_PREFIX + "handleIllegalArgumentException()");
		return new ResponseEntity<>(new ErrorBody(illegalArgumentException.getMessage(),
				ExceptionUtils.getStackTrace(illegalArgumentException)), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ApplicationException.class)
	protected ResponseEntity<ErrorBody> handleApplicationException(ApplicationException applicationException) {
		applicationException.printStackTrace();
		
		LOGGER.info(LOG_MESSAGE_PREFIX + "handleApplicationException()");
		return new ResponseEntity<>(new ErrorBody(String.join(MESSAGE_SEPERATOR, applicationException.getMessageList()),
				ExceptionUtils.getStackTrace(applicationException)), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(SQLException.class)
	protected ResponseEntity<ErrorBody> handleSqlException(SQLException sqlException) {
		sqlException.printStackTrace();

		LOGGER.info(LOG_MESSAGE_PREFIX + "handleSqlException()");
		return new ResponseEntity<>(
				new ErrorBody(sqlException.getMessage(), ExceptionUtils.getStackTrace(sqlException)),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(NullPointerException.class)
	protected ResponseEntity<ErrorBody> handleNullPointerException(NullPointerException nullPointerException) {
		nullPointerException.printStackTrace();

		LOGGER.info(LOG_MESSAGE_PREFIX + "handleNullPointerException()");
		return new ResponseEntity<>(
				new ErrorBody(nullPointerException.getMessage(), ExceptionUtils.getStackTrace(nullPointerException)),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(RuntimeException.class)
	protected ResponseEntity<ErrorBody> handleRuntimeException(RuntimeException runtimeException) {
		runtimeException.printStackTrace();

		LOGGER.info(LOG_MESSAGE_PREFIX + "handleRuntimeException()");
		return new ResponseEntity<>(
				new ErrorBody(runtimeException.getMessage(), ExceptionUtils.getStackTrace(runtimeException)),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
