package com.jkservice.organization.dto;

public record RegistrationResponse(
    Long userId, Long organizationId, String role, String membershipStatus, String message) {}
