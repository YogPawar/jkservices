package com.jkservice.auth.api;

import com.jkservice.auth.api.dto.response.RegistrationResponse;
import com.jkservice.auth.application.RegistrationService;
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

  @PostMapping("/register-family")
  @ResponseStatus(HttpStatus.CREATED)
  public RegistrationResponse registerFamily(@Valid @RequestBody JoinOrganizationRequest r) {
    return service.joinOrganization(r);
  }

  @PostMapping("/join-family")
  @ResponseStatus(HttpStatus.CREATED)
  public RegistrationResponse joinFamily(@Valid @RequestBody JoinOrganizationRequest r) {
    return service.joinOrganization(r);
  }
}
