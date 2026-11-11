package com.jkservice.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final RegistrationService service;

  public AuthController(RegistrationService service) {
    this.service = service;
  }

  @PostMapping("/register-family")
  @ResponseStatus(HttpStatus.CREATED)
  public RegistrationResponse registerFamily(@Valid @RequestBody CreateFamilyRequest r) {
    return service.registerFamily(r);
  }

  @PostMapping("/join-family")
  @ResponseStatus(HttpStatus.CREATED)
  public RegistrationResponse joinFamily(@Valid @RequestBody JoinFamilyRequest r) {
    return service.joinFamily(r);
  }

  public record CreateFamilyRequest(
      @NotBlank @Size(max = 120) String familyName,
      @NotBlank @Size(max = 120) String fullName,
      @NotBlank @Email String email,
      @NotBlank @Size(min = 8, max = 100) String password) {}

  public record JoinFamilyRequest(
      @NotBlank String inviteCode,
      @NotBlank @Size(max = 120) String fullName,
      @NotBlank @Email String email,
      @NotBlank @Size(min = 8, max = 100) String password) {}

  public record RegistrationResponse(
      Long userId, Long familyId, String role, String membershipStatus, String message) {}
}
