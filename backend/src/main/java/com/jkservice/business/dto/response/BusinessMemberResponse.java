package com.jkservice.business.dto.response;

import com.jkservice.business.enums.BusinessMemberRole;
import com.jkservice.business.enums.BusinessMembershipStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Business membership details")
public record BusinessMemberResponse(
    @Schema(description = "Business membership ID", example = "101") Long id,
    @Schema(description = "Organization ID", example = "1") Long organizationId,
    @Schema(description = "Business ID", example = "5") Long businessId,
    @Schema(description = "Organization membership ID of the assigned member", example = "12")
        Long organizationMemberId,
    @Schema(description = "Role assigned within this business", example = "MANAGER")
        BusinessMemberRole role,
    @Schema(description = "Business membership status", example = "ACTIVE")
        BusinessMembershipStatus membershipStatus,
    @Schema(description = "Creation timestamp") LocalDateTime createdAt,
    @Schema(description = "Last update timestamp") LocalDateTime updatedAt) {}
