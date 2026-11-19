package com.jkservice.auth.api;

import com.jkservice.auth.api.dto.response.RegistrationResponse;
import com.jkservice.auth.application.RegistrationService;
import com.jkservice.organization.dto.CreateOrganizationRequest;
import com.jkservice.organization.dto.JoinOrganizationRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final RegistrationService service;

  public AuthController(RegistrationService service) {
    this.service = service;
  }

  @PostMapping("/register-organization")
  @ResponseStatus(HttpStatus.CREATED)
  public RegistrationResponse registerOrganization(
      @Valid @RequestBody CreateOrganizationRequest request) {
    return service.registerOrganization(request);
  }

  @PostMapping("/join-organization")
  @ResponseStatus(HttpStatus.CREATED)
  public RegistrationResponse joinOrganization(
      @Valid @RequestBody JoinOrganizationRequest request) {
    return service.joinOrganization(request);
  }
}
