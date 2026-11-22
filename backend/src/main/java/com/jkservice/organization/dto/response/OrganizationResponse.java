package com.jkservice.organization.dto.response;

import java.time.Instant;

public record OrganizationResponse(
    Long id,
    String organizationName,
    String inviteCode,
    boolean active,
    Instant createdAt,
    Instant updatedAt) {}
