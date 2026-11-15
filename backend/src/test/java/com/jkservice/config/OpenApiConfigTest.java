package com.jkservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OpenApiConfigTest {
  private final OpenAPIConfig openAPIConfig = new OpenAPIConfig();

  @Test
  void shouldConfigureApiDocumentationMetadata() {
    OpenAPI openAPI = openAPIConfig.jkservicesOpenAPI();
    assertNotNull(openAPI);
    assertNotNull(openAPI.getInfo());

    assertEquals("JKServices API", openAPI.getInfo().getTitle());
    assertEquals("v1", openAPI.getInfo().getVersion());

    assertTrue(openAPI.getInfo().getDescription().contains("work management"));
  }
}
