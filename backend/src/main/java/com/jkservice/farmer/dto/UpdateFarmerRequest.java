package com.jkservice.farmer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateFarmerRequest(
    @NotBlank(message = "Farmer name is required")
        @Size(min = 2, max = 150, message = "Farmer name must be between 2 and 150 characters")
        String name,
    @Size(max = 20, message = "Phone number cannot exceed 20 characters") String phone,
    @Size(max = 100, message = "Village cannot exceed 100 characters") String village,
    String address,
    @NotNull(message = "Farmer status is required") Boolean active) {}
