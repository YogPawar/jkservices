package com.jkservice.auth.api;

import com.jkservice.auth.application.RegistrationService;
import com.jkservice.auth.api.dto.request.CreateFamilyRequest;
import com.jkservice.auth.api.dto.request.JoinFamilyRequest;
import com.jkservice.auth.api.dto.response.RegistrationResponse;
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
  public RegistrationResponse registerFamily(@Valid @RequestBody CreateFamilyRequest r) {
    return service.registerFamily(r);
  }

  @PostMapping("/join-family")
  @ResponseStatus(HttpStatus.CREATED)
  public RegistrationResponse joinFamily(@Valid @RequestBody JoinFamilyRequest r) {
    return service.joinFamily(r);
  }
}
