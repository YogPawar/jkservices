package com.jkservice.integration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

  private static final Logger log = LoggerFactory.getLogger(TestController.class);

  @PostMapping(value = "/test/users", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, Object> createUser(@Valid @RequestBody TestUserRequest request) {
    log.info("Test user creation request.");
    return Map.of("success", true);
  }

  public record TestUserRequest(@NotBlank(message = "Name must not be blank") String name) {}
}
