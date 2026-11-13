package com.jkservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleResourceNotFoundException(
      ResourceNotFoundException ex, HttpServletRequest request) {
    return buildErrorResponse(
        HttpStatus.NOT_FOUND,
        ApiErrorCode.RESOURCE_NOT_FOUND,
        ex.getMessage(),
        request.getRequestURI());
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ProblemDetail> handleBusinessException(
      BusinessException ex, HttpServletRequest request) {

    return buildErrorResponse(
        HttpStatus.BAD_REQUEST,
        ApiErrorCode.BUSINESS_RULE_VIOLATION,
        ex.getMessage(),
        request.getRequestURI());
  }

  @ExceptionHandler(DuplicateResourceException.class)
  public ResponseEntity<ProblemDetail> handleDuplicateResource(
      DuplicateResourceException ex, HttpServletRequest request) {

    return buildErrorResponse(
        HttpStatus.CONFLICT,
        ApiErrorCode.DUPLICATE_RESOURCE,
        ex.getMessage(),
        request.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleUnexpectedException(
      Exception ex, HttpServletRequest request) {

    log.error(
        "Unexpected error occurred while processing request: {}", request.getRequestURI(), ex);

    return buildErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        ApiErrorCode.INTERNAL_SERVER_ERROR,
        "An unexpected error occurred",
        request.getRequestURI());
  }

  private ResponseEntity<ProblemDetail> buildErrorResponse(
      HttpStatus httpStatus, ApiErrorCode errorCode, String message, String requestURI) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, message);
    problemDetail.setTitle(httpStatus.getReasonPhrase());
    problemDetail.setInstance(URI.create(requestURI));
    problemDetail.setProperty("errorCode", errorCode.name());
    return ResponseEntity.status(httpStatus).body(problemDetail);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");

    problemDetail.setTitle(HttpStatus.BAD_REQUEST.getReasonPhrase());
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    problemDetail.setProperty("errorCode", ApiErrorCode.VALIDATION_FAILED.name());

    Map<String, String> fieldErrors = new HashMap<>();

    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

    problemDetail.setProperty("fieldErrors", fieldErrors);

    return ResponseEntity.badRequest().body(problemDetail);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ProblemDetail> handleConstraintViolation(
      ConstraintViolationException ex, HttpServletRequest request) {

    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");

    problemDetail.setTitle(HttpStatus.BAD_REQUEST.getReasonPhrase());
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    problemDetail.setProperty("errorCode", ApiErrorCode.VALIDATION_FAILED.name());

    Map<String, String> fieldErrors = new HashMap<>();

    ex.getConstraintViolations()
        .forEach(violation ->
            fieldErrors.put(violation.getPropertyPath().toString(), violation.getMessage()));

    problemDetail.setProperty("fieldErrors", fieldErrors);

    return ResponseEntity.badRequest().body(problemDetail);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ProblemDetail> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex, HttpServletRequest request) {

    return buildErrorResponse(
        HttpStatus.BAD_REQUEST,
        ApiErrorCode.MALFORMED_REQUEST,
        "Request body is missing or contains malformed data",
        request.getRequestURI());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

    return buildErrorResponse(
        HttpStatus.BAD_REQUEST,
        ApiErrorCode.VALIDATION_FAILED,
        "Invalid value for request parameter: " + ex.getName(),
        request.getRequestURI());
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ProblemDetail> handleMissingRequestParameter(
      MissingServletRequestParameterException ex, HttpServletRequest request) {

    return buildErrorResponse(
        HttpStatus.BAD_REQUEST,
        ApiErrorCode.VALIDATION_FAILED,
        "Required request parameter is missing: " + ex.getParameterName(),
        request.getRequestURI());
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ProblemDetail> handleMethodNotSupported(
      HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {

    return buildErrorResponse(
        HttpStatus.METHOD_NOT_ALLOWED,
        ApiErrorCode.METHOD_NOT_ALLOWED,
        "HTTP method is not supported for this endpoint",
        request.getRequestURI());
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ProblemDetail> handleUnsupportedMediaType(
      HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {

    return buildErrorResponse(
        HttpStatus.UNSUPPORTED_MEDIA_TYPE,
        ApiErrorCode.UNSUPPORTED_MEDIA_TYPE,
        "Content type is not supported for this endpoint",
        request.getRequestURI());
  }

  @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
  public ResponseEntity<ProblemDetail> handleMediaTypeNotAcceptable(
      HttpMediaTypeNotAcceptableException ex, HttpServletRequest request) {

    return buildErrorResponse(
        HttpStatus.NOT_ACCEPTABLE,
        ApiErrorCode.NOT_ACCEPTABLE,
        "Requested response media type is not supported",
        request.getRequestURI());
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ProblemDetail> handleNoResourceFound(
      NoResourceFoundException ex, HttpServletRequest request) {

    return buildErrorResponse(
        HttpStatus.NOT_FOUND,
        ApiErrorCode.RESOURCE_NOT_FOUND,
        "Requested resource was not found",
        request.getRequestURI());
  }
}
