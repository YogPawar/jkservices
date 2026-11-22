package com.jkservice.business.dto.request;

import com.jkservice.business.enums.BusinessType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to update an existing business")
public record UpdateBusinessRequest(
    @Schema(
            description = "Updated name of the business",
            example = "Pawar Farming",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Business name is required")
        @Size(max = 150, message = "Business name cannot exceed 150 characters")
        String businessName,
    @Schema(
            description = "Type of business",
            example = "FARMING_SERVICES",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Business type is required")
        BusinessType businessType,
    @Schema(
            description = "Unique business code within the organization",
            example = "PAWAR-001",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Business code is required")
        @Size(max = 50, message = "Business code cannot exceed 50 characters")
        String businessCode,
    @Schema(description = "Business contact number", example = "9876543210")
        @Size(max = 20, message = "Contact number cannot exceed 20 characters")
        String contactNumber,
    @Schema(description = "Business email address", example = "contact@pawarfarming.com")
        @Email(message = "Invalid email format")
        @Size(max = 255, message = "Email cannot exceed 255 characters")
        String email,
    @Schema(description = "Business address", example = "Pune, Maharashtra") String address,
    @Schema(
            description = "ISO 4217 three-letter currency code",
            example = "INR",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Currency code is required")
        @Pattern(
            regexp = "^[A-Za-z]{3}$",
            message = "Currency code must contain exactly 3 alphabetic characters")
        String currencyCode) {}
