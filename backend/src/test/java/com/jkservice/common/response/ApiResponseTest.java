package com.jkservice.common.response;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApiResponseTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper =
        new ObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  @Test
  void shouldCreateSuccessfulResponseWithData() {
    // Arrange
    String message = "Farmer created successfully";
    String data = "FARMER-101";

    // Act
    ApiResponse<String> response = ApiResponse.success(message, data);

    // Assert
    assertTrue(response.success());
    assertEquals(message, response.message());
    assertEquals(data, response.data());
    assertNotNull(response.timestamp());
  }

  @Test
  void shouldCreateSuccessfulResponseWithoutData() {
    // Arrange
    String message = "Operation completed successfully";

    // Act
    ApiResponse<Void> response = ApiResponse.success(message);

    // Assert
    assertTrue(response.success());
    assertEquals(message, response.message());
    assertNull(response.data());
    assertNotNull(response.timestamp());
  }

  @Test
  void shouldPopulateTimestampWhenCreatingSuccessfulResponse() {
    // Arrange
    Instant beforeCreation = Instant.now();

    // Act
    ApiResponse<String> response = ApiResponse.success("Success", "Test data");

    Instant afterCreation = Instant.now();

    // Assert
    assertNotNull(response.timestamp());
    assertFalse(
        response.timestamp().isBefore(beforeCreation),
        "Timestamp should not be before response creation");

    assertFalse(
        response.timestamp().isAfter(afterCreation),
        "Timestamp should not be after response creation");
  }

  @Test
  void shouldRejectNullMessage() {
    // Act & Assert
    assertThrows(NullPointerException.class, () -> ApiResponse.success(null, "Test data"));
  }

  @Test
  void shouldRejectNullTimestampWhenConstructingResponse() {
    // Act & Assert
    assertThrows(
        NullPointerException.class, () -> new ApiResponse<>(true, "Success", "Test data", null));
  }

  @Test
  void shouldSerializeSuccessfulResponseWithData() throws Exception {
    ApiResponse<String> response = ApiResponse.success("Farmer created successfully", "FARMER-101");

    String json = objectMapper.writeValueAsString(response);
    JsonNode root = objectMapper.readTree(json);

    assertTrue(root.get("success").asBoolean());
    assertEquals("Farmer created successfully", root.get("message").asText());
    assertEquals("FARMER-101", root.get("data").asText());
    assertNotNull(root.get("timestamp"));
  }

  @Test
  void shouldSerializeAllExpectedResponseProperties() throws Exception {
    ApiResponse<String> response = ApiResponse.success("Success", "Test data");

    JsonNode root = objectMapper.readTree(objectMapper.writeValueAsString(response));

    assertEquals(4, root.size());
    assertTrue(root.has("success"));
    assertTrue(root.has("message"));
    assertTrue(root.has("data"));
    assertTrue(root.has("timestamp"));
  }

  @Test
  void shouldSerializeNullDataForVoidResponse() throws Exception {
    ApiResponse<Void> response = ApiResponse.success("Operation completed");

    JsonNode root = objectMapper.readTree(objectMapper.writeValueAsString(response));

    assertTrue(root.get("success").asBoolean());
    assertEquals("Operation completed", root.get("message").asText());
    assertTrue(root.get("data").isNull());
  }

  @Test
  void shouldSerializeTimestampInIso8601Format() throws Exception {
    ApiResponse<String> response = ApiResponse.success("Success", "Test data");

    JsonNode root = objectMapper.readTree(objectMapper.writeValueAsString(response));

    String timestamp = root.get("timestamp").asText();

    assertDoesNotThrow(
        () -> Instant.parse(timestamp), "Timestamp should be a valid ISO-8601 UTC instant");
  }

  @Test
  void shouldNotSerializeUnexpectedProperties() throws Exception {
    ApiResponse<String> response = ApiResponse.success("Success", "Test data");

    JsonNode root = objectMapper.readTree(objectMapper.writeValueAsString(response));

    assertFalse(root.has("error"));
    assertFalse(root.has("status"));
    assertFalse(root.has("errors"));
  }
}
