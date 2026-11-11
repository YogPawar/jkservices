package com.jkservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
      ResourceNotFoundException ex, HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(
      BusinessException ex, HttpServletRequest request) {

    return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(DuplicateResourceException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateResource(
      DuplicateResourceException ex, HttpServletRequest request) {

    return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnexpectedException(
      Exception ex, HttpServletRequest request) {

    log.error(
        "Unexpected error occurred while processing request: {}", request.getRequestURI(), ex);

    return buildErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request.getRequestURI());
  }

  private ResponseEntity<ErrorResponse> buildErrorResponse(
      HttpStatus httpStatus, String message, String requestURI) {
    ErrorResponse response =
        new ErrorResponse(
            Instant.now(), httpStatus.value(), httpStatus.name(), message, requestURI);
    return ResponseEntity.status(httpStatus).body(response);
  }
}
