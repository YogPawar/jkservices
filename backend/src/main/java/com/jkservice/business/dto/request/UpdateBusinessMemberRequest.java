package com.jkservice.business.dto.request;

import com.jkservice.business.enums.BusinessMemberRole;
import com.jkservice.business.enums.BusinessMembershipStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to update a business member's role or membership status")
public record UpdateBusinessMemberRequest(
    @NotNull(message = "Business member role is required")
        @Schema(
            description = "Updated role within the business",
            example = "OPERATOR",
            requiredMode = Schema.RequiredMode.REQUIRED)
        BusinessMemberRole role,
    @NotNull(message = "Membership status is required")
        @Schema(
            description = "Updated business membership status",
            example = "ACTIVE",
            requiredMode = Schema.RequiredMode.REQUIRED)
        BusinessMembershipStatus membershipStatus) {}
