package com.jkservice.auth.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFamilyRequest(
    @NotBlank @Size(max = 120) String familyName,
    @NotBlank @Size(max = 120) String fullName,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8, max = 100) String password) {}
