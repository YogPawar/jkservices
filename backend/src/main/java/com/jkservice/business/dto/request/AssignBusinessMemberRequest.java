package com.jkservice.business.dto.request;

import com.jkservice.business.enums.BusinessMemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to assign an organization member to a business")
public record AssignBusinessMemberRequest(
    @NotNull(message = "Organization member ID is required")
        @Schema(
            description = "ID of an active organization member to assign",
            example = "12",
            requiredMode = Schema.RequiredMode.REQUIRED)
        Long organizationMemberId,
    @NotNull(message = "Business member role is required")
        @Schema(
            description = "Role granted within this business",
            example = "MANAGER",
            requiredMode = Schema.RequiredMode.REQUIRED)
        BusinessMemberRole role) {}
