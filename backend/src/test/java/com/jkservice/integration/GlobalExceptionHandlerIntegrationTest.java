package com.jkservice.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jkservice.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = TestController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  // 1. Malformed JSON
  @Test
  void shouldReturn400WhenActualHttpRequestContainsMalformedJson() throws Exception {

    String malformedJson =
        """
        {
            "name": "Yogesh"
        """;

    mockMvc
        .perform(post("/test/users").contentType(MediaType.APPLICATION_JSON).content(malformedJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.title").value("Bad Request"))
        .andExpect(jsonPath("$.detail").value("Request body is missing or contains malformed data"))
        .andExpect(jsonPath("$.errorCode").value("MALFORMED_REQUEST"))
        .andExpect(jsonPath("$.instance").value("/test/users"));
  }

  // 2. Bean Validation failure
  @Test
  void shouldReturn400WhenRequestValidationFails() throws Exception {

    String requestBody =
        """
        {
            "name": ""
        }
        """;

    mockMvc
        .perform(post("/test/users").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
        .andExpect(jsonPath("$.detail").value("Request validation failed"))
        .andExpect(jsonPath("$.fieldErrors.name").value("Name must not be blank"));
  }

  // 3. Unsupported HTTP method
  @Test
  void shouldReturn405WhenHttpMethodIsNotSupported() throws Exception {

    mockMvc
        .perform(get("/test/users"))
        .andExpect(status().isMethodNotAllowed())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(405))
        .andExpect(jsonPath("$.errorCode").value("METHOD_NOT_ALLOWED"))
        .andExpect(jsonPath("$.detail").value("HTTP method is not supported for this endpoint"));
  }

  // 4. Unsupported media type
  @Test
  void shouldReturn415WhenUnsupportedMediaTypeIsProvided() throws Exception {

    mockMvc
        .perform(
            post("/test/users")
                .contentType(MediaType.APPLICATION_XML)
                .content("<user><name>Yogesh</name></user>"))
        .andExpect(status().isUnsupportedMediaType())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(415))
        .andExpect(jsonPath("$.errorCode").value("UNSUPPORTED_MEDIA_TYPE"))
        .andExpect(jsonPath("$.detail").value("Content type is not supported for this endpoint"));
  }

  // 5. Missing endpoint
  @Test
  void shouldReturn404WhenRequestedEndpointDoesNotExist() throws Exception {

    mockMvc
        .perform(get("/test/unknown"))
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"))
        .andExpect(jsonPath("$.detail").value("Requested resource was not found"));
  }
}
