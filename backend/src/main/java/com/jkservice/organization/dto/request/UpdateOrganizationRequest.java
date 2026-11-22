package com.jkservice.organization.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(
    name = "UpdateOrganizationRequest",
    description = "Request payload to update an organization's name and active status")
public record UpdateOrganizationRequest(
    @Schema(
            description = "Updated name of the organization",
            example = "JK Services",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Organization name is required")
        @Size(max = 150, message = "Organization name cannot exceed 150 characters")
        String organizationName,
    @Schema(
            description =
                "Indicates whether the organization is active. Set to true to reactivate or false to deactivate the organization",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Organization status is required")
        Boolean active) {}
