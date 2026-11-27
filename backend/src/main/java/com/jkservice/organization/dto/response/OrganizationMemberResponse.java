package com.jkservice.organization.dto.response;

import com.jkservice.organization.enums.OrganizationMembershipStatus;
import com.jkservice.organization.enums.OrganizationRole;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(
    name = "OrganizationMemberResponse",
    description =
        "Organization membership details. Business-specific assignments and permissions are maintained separately.")
public record OrganizationMemberResponse(
    @Schema(description = "Unique organization membership ID", example = "101") Long id,
    @Schema(description = "ID of the organization", example = "1") Long organizationId,
    @Schema(description = "ID of the user who belongs to the organization", example = "12")
        Long userId,
    @Schema(description = "Organization-level role", example = "MEMBER") OrganizationRole role,
    @Schema(description = "Current membership status", example = "ACTIVE")
        OrganizationMembershipStatus membershipStatus,
    @Schema(
            description = "Timestamp when the membership became active",
            example = "2026-11-27T10:30:00Z")
        Instant joinedAt,
    @Schema(
            description = "Timestamp when the membership was created",
            example = "2026-11-27T10:30:00Z")
        Instant createdAt,
    @Schema(
            description = "Timestamp when the membership was last updated",
            example = "2026-11-27T11:00:00Z")
        Instant updatedAt) {}
