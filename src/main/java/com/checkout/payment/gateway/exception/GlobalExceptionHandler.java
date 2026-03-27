package com.checkout.payment.gateway.exception;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.checkout.payment.gateway.dto.response.error.ErrorResponse;
import com.checkout.payment.gateway.dto.response.error.PaymentProcessingError;
import com.checkout.payment.gateway.enums.PaymentStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(PaymentNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleException(PaymentNotFoundException ex) {
		LOG.warn("Payment not found: {}", ex.getMessage());
		LOG.debug("Exception stack trace", ex);
		return new ResponseEntity<>(new ErrorResponse("Payment not found"), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<PaymentProcessingError> handleException(MethodArgumentNotValidException ex) {
		String errors = ex.getBindingResult().getAllErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(", "));
		
		LOG.warn("Payment rejected due to validation errors: {}", errors);

		return new ResponseEntity<>(new PaymentProcessingError(PaymentStatus.REJECTED, "Invalid request body"),
				HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<PaymentProcessingError> handleException(IllegalArgumentException ex) {
	    LOG.warn("Payment rejected due to invalid argument: {}", ex.getMessage());
	    return new ResponseEntity<>(
	        new PaymentProcessingError(PaymentStatus.REJECTED, "Invalid request body"),
	        HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<PaymentProcessingError> handleException(HttpMessageNotReadableException ex) {
	    LOG.warn("Payment rejected due to unreadable message: {}", ex.getMessage());
	    return new ResponseEntity<>(
	        new PaymentProcessingError(PaymentStatus.REJECTED, "Invalid request body"),
	        HttpStatus.BAD_REQUEST);
	}
}
