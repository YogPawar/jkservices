package com.jkservice.common.response;

import java.time.Instant;
import java.util.Objects;

public record ApiResponse<T>(boolean success, String message, T data, Instant timestamp) {

  public ApiResponse {
    Objects.requireNonNull(message, "message must not be null");
    Objects.requireNonNull(timestamp, "timestamp must not be null");
  }

  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>(true, message, data, Instant.now());
  }

  public static ApiResponse<Void> success(String message) {
    return new ApiResponse<>(true, message, null, Instant.now());
  }
}
