package com.jkservice.farmer.controller;

import com.jkservice.common.response.ApiResponse;
import com.jkservice.farmer.dto.CreateFarmerRequest;
import com.jkservice.farmer.dto.FarmerResponse;
import com.jkservice.farmer.service.FarmerService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/families/{familyId}/farmers")
@RequiredArgsConstructor
public class FarmerController {

  private final FarmerService farmerService;

  /** Creates a farmer under the specified family. */
  @PostMapping
  public ResponseEntity<ApiResponse<FarmerResponse>> createFarmer(
      @PathVariable Long familyId, @Valid @RequestBody CreateFarmerRequest request) {

    FarmerResponse farmerResponse = farmerService.createFarmer(familyId, request);

    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{farmerId}")
            .buildAndExpand(farmerResponse.id())
            .toUri();

    ApiResponse<FarmerResponse> response =
        ApiResponse.success("Farmer created successfully", farmerResponse);

    return ResponseEntity.created(location).body(response);
  }
}
