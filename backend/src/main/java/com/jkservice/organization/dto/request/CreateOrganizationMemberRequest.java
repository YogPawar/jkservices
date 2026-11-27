package com.jkservice.organization.dto.request;

import com.jkservice.organization.enums.OrganizationRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
    name = "CreateOrganizationMemberRequest",
    description = "Request to add an existing active user to an organization")
public record CreateOrganizationMemberRequest(
    @NotNull
        @Schema(
            description = "ID of the existing active user to add to the organization",
            example = "12",
            requiredMode = Schema.RequiredMode.REQUIRED)
        Long userId,
    @NotNull
        @Schema(
            description =
                "Role assigned to the user at the organization level. Business-level access is managed separately through BusinessMember.",
            example = "MEMBER",
            requiredMode = Schema.RequiredMode.REQUIRED)
        OrganizationRole role) {}
