package com.jkservice.auth.api;

import com.jkservice.auth.dto.response.RegistrationResponse;
import com.jkservice.auth.application.RegistrationService;
import com.jkservice.organization.dto.request.CreateOrganizationRequest;
import com.jkservice.organization.dto.request.JoinOrganizationRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class RegistrationController {

  private final RegistrationService service;

  public RegistrationController(RegistrationService service) {
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
