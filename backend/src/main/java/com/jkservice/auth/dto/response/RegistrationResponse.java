package com.jkservice.auth.dto.response;

public record RegistrationResponse(
    Long userId, Long organizationId, String role, String membershipStatus, String message) {}
