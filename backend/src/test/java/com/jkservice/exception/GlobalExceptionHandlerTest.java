package com.jkservice.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

public class GlobalExceptionHandlerTest {
  private GlobalExceptionHandler exceptionHandler;
  private HttpServletRequest request;

  @BeforeEach
  void setup() {
    exceptionHandler = new GlobalExceptionHandler();
    request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn("/api/users/100");

    assertEquals("/api/users/100", request.getRequestURI());
  }

  @Test
  void shouldReturn404WhenResourceNotFound() {
    ResourceNotFoundException exception = new ResourceNotFoundException("User Not Found");
    ProblemDetail response =
        exceptionHandler.handleResourceNotFoundException(exception, request).getBody();

    assertNotNull(response);
    assertEquals(404, response.getStatus());
    assertEquals("Not Found", response.getTitle());
    assertEquals("User Not Found", response.getDetail());
    assertNotNull(response.getInstance());
    assertEquals("/api/users/100", response.getInstance().toString());

    assertNotNull(response.getProperties());
    assertEquals("RESOURCE_NOT_FOUND", response.getProperties().get("errorCode"));
  }

  @Test
  void shouldReturn400WhenBusinessRuleIsViolated() {

    // Arrange
    BusinessException exception = new BusinessException("Payment amount must be positive");

    // Act
    ResponseEntity<ProblemDetail> response =
        exceptionHandler.handleBusinessException(exception, request);

    ProblemDetail problemDetail = response.getBody();

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    assertNotNull(problemDetail);
    assertEquals(400, problemDetail.getStatus());
    assertEquals("Bad Request", problemDetail.getTitle());
    assertEquals("Payment amount must be positive", problemDetail.getDetail());

    assertNotNull(problemDetail.getProperties());
    assertEquals("BUSINESS_RULE_VIOLATION", problemDetail.getProperties().get("errorCode"));
  }

  @Test
  void shouldReturn409WhenDuplicateResourceIsDetected() {

    // Arrange
    DuplicateResourceException exception = new DuplicateResourceException("User already exists");

    // Act
    ResponseEntity<ProblemDetail> response =
        exceptionHandler.handleDuplicateResource(exception, request);

    ProblemDetail problemDetail = response.getBody();

    // Assert
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

    assertNotNull(problemDetail);
    assertEquals(409, problemDetail.getStatus());
    assertEquals("Conflict", problemDetail.getTitle());
    assertEquals("User already exists", problemDetail.getDetail());

    assertNotNull(problemDetail.getProperties());
    assertEquals("DUPLICATE_RESOURCE", problemDetail.getProperties().get("errorCode"));
  }

  @Test
  void shouldReturn500WhenUnexpectedExceptionOccurs() {
    // Arrange
    Exception exception = new RuntimeException("Sensitive database details");

    // Act
    ResponseEntity<ProblemDetail> response =
        exceptionHandler.handleUnexpectedException(exception, request);

    ProblemDetail problemDetail = response.getBody();

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

    assertNotNull(problemDetail);
    assertEquals(500, problemDetail.getStatus());
    assertEquals("Internal Server Error", problemDetail.getTitle());
    assertEquals("An unexpected error occurred", problemDetail.getDetail());

    assertNotNull(problemDetail.getInstance());
    assertEquals("/api/users/100", problemDetail.getInstance().toString());

    assertNotNull(problemDetail.getProperties());
    assertEquals("INTERNAL_SERVER_ERROR", problemDetail.getProperties().get("errorCode"));

    // Verify that internal exception details are not exposed.
    assertNotEquals(exception.getMessage(), problemDetail.getDetail());
  }

  @Test
  void shouldReturn400WithFieldErrorsWhenRequestValidationFails() {

    // Arrange
    BindingResult bindingResult =
        new BeanPropertyBindingResult(new Object(), "userRegistrationRequest");

    bindingResult.addError(
        new FieldError(
            "userRegistrationRequest", "name", "", false, null, null, "Name is required"));

    bindingResult.addError(
        new FieldError(
            "userRegistrationRequest",
            "email",
            "invalid-email",
            false,
            null,
            null,
            "Invalid email format"));

    MethodArgumentNotValidException exception =
        new MethodArgumentNotValidException(null, bindingResult);

    // Act
    ResponseEntity<ProblemDetail> response =
        exceptionHandler.handleMethodArgumentNotValid(exception, request);

    ProblemDetail problemDetail = response.getBody();

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    assertNotNull(problemDetail);
    assertEquals(400, problemDetail.getStatus());
    assertEquals("Bad Request", problemDetail.getTitle());
    assertEquals("Request validation failed", problemDetail.getDetail());
    assertEquals("/api/users/100", problemDetail.getInstance().toString());

    assertEquals("VALIDATION_FAILED", problemDetail.getProperties().get("errorCode"));

    @SuppressWarnings("unchecked")
    Map<String, String> fieldErrors =
        (Map<String, String>) problemDetail.getProperties().get("fieldErrors");

    assertNotNull(fieldErrors);
    assertEquals(2, fieldErrors.size());
    assertEquals(List.of("Name is required"), fieldErrors.get("name"));
    assertEquals(List.of("Invalid email format"), fieldErrors.get("email"));
  }

  @Test
  void shouldReturn400WithFieldErrorsWhenConstraintViolationOccurs() {

    // Arrange
    ConstraintViolation<?> violation = mock(ConstraintViolation.class);
    Path propertyPath = mock(Path.class);

    when(propertyPath.toString()).thenReturn("getUser.id");
    when(violation.getPropertyPath()).thenReturn(propertyPath);
    when(violation.getMessage()).thenReturn("must be greater than 0");

    ConstraintViolationException exception =
        new ConstraintViolationException("Validation failed", Set.of(violation));

    // Act
    ResponseEntity<ProblemDetail> response =
        exceptionHandler.handleConstraintViolation(exception, request);

    ProblemDetail problemDetail = response.getBody();

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    assertNotNull(problemDetail);
    assertEquals(400, problemDetail.getStatus());
    assertEquals("Bad Request", problemDetail.getTitle());
    assertEquals("Request validation failed", problemDetail.getDetail());
    assertEquals("/api/users/100", problemDetail.getInstance().toString());

    assertEquals("VALIDATION_FAILED", problemDetail.getProperties().get("errorCode"));

    @SuppressWarnings("unchecked")
    Map<String, String> fieldErrors =
        (Map<String, String>) problemDetail.getProperties().get("fieldErrors");

    assertNotNull(fieldErrors);
    assertEquals(1, fieldErrors.size());
    assertEquals(List.of("must be greater than 0"), fieldErrors.get("getUser.id"));
  }

  @Test
  void shouldReturn400WhenRequestBodyIsMalformed() {

    // Arrange
    HttpMessageNotReadableException exception =
        new HttpMessageNotReadableException("JSON parse error: Unexpected end of input");

    // Act
    ResponseEntity<ProblemDetail> response =
        exceptionHandler.handleHttpMessageNotReadable(exception, request);

    ProblemDetail problemDetail = response.getBody();

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    assertNotNull(problemDetail);
    assertEquals(400, problemDetail.getStatus());
    assertEquals("Bad Request", problemDetail.getTitle());

    assertEquals("Request body is missing or contains malformed data", problemDetail.getDetail());

    assertEquals("/api/users/100", problemDetail.getInstance().toString());

    assertEquals("MALFORMED_REQUEST", problemDetail.getProperties().get("errorCode"));
  }

  @Test
  void shouldReturn400WhenRequestParameterHasInvalidType() throws NoSuchMethodException {

    // Arrange
    Method method = getClass().getDeclaredMethod("dummyMethod", Long.class);

    MethodParameter methodParameter = new MethodParameter(method, 0);

    MethodArgumentTypeMismatchException exception =
        new MethodArgumentTypeMismatchException(
            "abc",
            Long.class,
            "id",
            methodParameter,
            new IllegalArgumentException("Invalid number format"));

    // Act
    ResponseEntity<ProblemDetail> response =
        exceptionHandler.handleMethodArgumentTypeMismatch(exception, request);

    ProblemDetail problemDetail = response.getBody();

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    assertNotNull(problemDetail);
    assertEquals(400, problemDetail.getStatus());
    assertEquals("Bad Request", problemDetail.getTitle());

    assertEquals("Invalid value for request parameter: id", problemDetail.getDetail());

    assertEquals("/api/users/100", problemDetail.getInstance().toString());

    assertEquals("VALIDATION_FAILED", problemDetail.getProperties().get("errorCode"));
  }

  @Test
  void shouldReturn400WhenRequiredRequestParameterIsMissing() {

    // Arrange
    MissingServletRequestParameterException exception =
        new MissingServletRequestParameterException("page", "Integer");

    // Act
    ResponseEntity<ProblemDetail> response =
        exceptionHandler.handleMissingRequestParameter(exception, request);

    ProblemDetail problemDetail = response.getBody();

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    assertNotNull(problemDetail);
    assertEquals(400, problemDetail.getStatus());
    assertEquals("Bad Request", problemDetail.getTitle());

    assertEquals("Required request parameter is missing: page", problemDetail.getDetail());

    assertEquals("/api/users/100", problemDetail.getInstance().toString());

    assertEquals("VALIDATION_FAILED", problemDetail.getProperties().get("errorCode"));
  }

  @Test
  void shouldReturn405WhenHttpMethodIsNotSupported() {

    // Arrange
    HttpRequestMethodNotSupportedException exception =
        new HttpRequestMethodNotSupportedException("POST");

    // Act
    ResponseEntity<ProblemDetail> response =
        exceptionHandler.handleMethodNotSupported(exception, request);

    ProblemDetail problemDetail = response.getBody();

    // Assert
    assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());

    assertNotNull(problemDetail);
    assertEquals(405, problemDetail.getStatus());
    assertEquals("Method Not Allowed", problemDetail.getTitle());

    assertEquals("HTTP method is not supported for this endpoint", problemDetail.getDetail());

    assertEquals("/api/users/100", problemDetail.getInstance().toString());

    assertEquals("METHOD_NOT_ALLOWED", problemDetail.getProperties().get("errorCode"));
  }

  @Test
  void shouldReturn415WhenUnsupportedMediaTypeIsProvided() {

    // Arrange
    HttpMediaTypeNotSupportedException exception =
        new HttpMediaTypeNotSupportedException(
            MediaType.APPLICATION_XML, List.of(MediaType.APPLICATION_JSON));

    // Act
    ResponseEntity<ProblemDetail> response =
        exceptionHandler.handleUnsupportedMediaType(exception, request);

    ProblemDetail problemDetail = response.getBody();

    // Assert
    assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());

    assertNotNull(problemDetail);
    assertEquals(415, problemDetail.getStatus());
    assertEquals("Unsupported Media Type", problemDetail.getTitle());

    assertEquals("Content type is not supported for this endpoint", problemDetail.getDetail());

    assertNotNull(problemDetail.getInstance());
    assertEquals("/api/users/100", problemDetail.getInstance().toString());

    assertNotNull(problemDetail.getProperties());
    assertEquals("UNSUPPORTED_MEDIA_TYPE", problemDetail.getProperties().get("errorCode"));
  }

  @Test
  void shouldReturn406WhenRequestedResponseMediaTypeIsNotAcceptable() {

    // Arrange
    HttpMediaTypeNotAcceptableException exception =
        new HttpMediaTypeNotAcceptableException(List.of(MediaType.APPLICATION_JSON));

    // Act
    ResponseEntity<ProblemDetail> response =
        exceptionHandler.handleMediaTypeNotAcceptable(exception, request);

    ProblemDetail problemDetail = response.getBody();

    // Assert
    assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());

    assertNotNull(problemDetail);
    assertEquals(406, problemDetail.getStatus());
    assertEquals("Not Acceptable", problemDetail.getTitle());

    assertEquals("Requested response media type is not supported", problemDetail.getDetail());

    assertEquals("/api/users/100", problemDetail.getInstance().toString());

    assertEquals("NOT_ACCEPTABLE", problemDetail.getProperties().get("errorCode"));
  }

  @Test
  void shouldReturn404WhenRequestedResourceDoesNotExist() {

    // Arrange
    NoResourceFoundException exception =
        new NoResourceFoundException(HttpMethod.GET, "/api/unknown-resource");

    // Act
    ResponseEntity<ProblemDetail> response =
        exceptionHandler.handleNoResourceFound(exception, request);

    ProblemDetail problemDetail = response.getBody();

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    assertNotNull(problemDetail);
    assertEquals(404, problemDetail.getStatus());
    assertEquals("Not Found", problemDetail.getTitle());

    assertEquals("Requested resource was not found", problemDetail.getDetail());

    assertEquals("/api/users/100", problemDetail.getInstance().toString());

    assertEquals("RESOURCE_NOT_FOUND", problemDetail.getProperties().get("errorCode"));
  }

  @Test
  void shouldPreserveMultipleValidationErrorsForSameField() {

    // Arrange
    BindingResult bindingResult =
        new BeanPropertyBindingResult(new Object(), "userRegistrationRequest");

    bindingResult.addError(
        new FieldError(
            "userRegistrationRequest", "name", "", false, null, null, "Name is required"));

    bindingResult.addError(
        new FieldError(
            "userRegistrationRequest",
            "name",
            "",
            false,
            null,
            null,
            "Name must contain at least 3 characters"));

    MethodArgumentNotValidException exception =
        new MethodArgumentNotValidException(null, bindingResult);

    // Act
    ResponseEntity<ProblemDetail> response =
        exceptionHandler.handleMethodArgumentNotValid(exception, request);

    ProblemDetail problemDetail = response.getBody();

    // Assert
    assertNotNull(problemDetail);

    @SuppressWarnings("unchecked")
    Map<String, List<String>> fieldErrors =
        (Map<String, List<String>>) problemDetail.getProperties().get("fieldErrors");

    assertNotNull(fieldErrors);
    assertEquals(2, fieldErrors.get("name").size());
    assertTrue(fieldErrors.get("name").contains("Name is required"));
    assertTrue(fieldErrors.get("name").contains("Name must contain at least 3 characters"));
  }

  @Test
  void shouldNotExposeInternalExceptionDetails() {
    // Arrange
    Exception exception = new RuntimeException("Sensitive internal database details");

    when(request.getMethod()).thenReturn("POST");

    // Act
    ResponseEntity<ProblemDetail> response =
        exceptionHandler.handleUnexpectedException(exception, request);

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());

    assertEquals("An unexpected error occurred", response.getBody().getDetail());

    assertFalse(response.getBody().getDetail().contains("Sensitive internal database details"));
  }

  private void dummyMethod(Long id) {
    // Used only to create MethodParameter for testing
  }
}
