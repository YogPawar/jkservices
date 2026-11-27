package com.jkservice.organization.dto.request;

import com.jkservice.organization.enums.OrganizationMembershipStatus;
import com.jkservice.organization.enums.OrganizationRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
    name = "UpdateOrganizationMemberRequest",
    description = "Request to update an organization member's role and membership status")
public record UpdateOrganizationMemberRequest(
    @NotNull
        @Schema(
            description = "New organization-level role assigned to the member",
            example = "ADMIN",
            requiredMode = Schema.RequiredMode.REQUIRED)
        OrganizationRole role,
    @NotNull
        @Schema(
            description = "New membership status",
            example = "ACTIVE",
            requiredMode = Schema.RequiredMode.REQUIRED)
        OrganizationMembershipStatus membershipStatus) {}
