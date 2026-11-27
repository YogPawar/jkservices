package com.jkservice.user.dto.response;

import java.time.Instant;

public record UserResponse(
    Long id, String fullName, String email, boolean active, Instant createdAt, Instant updatedAt) {}
