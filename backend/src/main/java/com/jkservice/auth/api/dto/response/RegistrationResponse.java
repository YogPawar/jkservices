package com.jkservice.auth.api.dto.response;

public record RegistrationResponse(
    Long userId, Long familyId, String role, String membershipStatus, String message) {}
