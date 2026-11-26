package com.jkservice.business.controller;

import com.jkservice.business.dto.request.CreateBusinessRequest;
import com.jkservice.business.dto.request.UpdateBusinessRequest;
import com.jkservice.business.dto.response.BusinessResponse;
import com.jkservice.business.service.BusinessService;
import com.jkservice.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/organizations/{organizationId}/businesses")
@Tag(name = "Business Management", description = "APIs for managing organization businesses")
public class BusinessController {

  private final BusinessService businessService;

  @PostMapping
  @Operation(
      summary = "Create business",
      description = "Creates a new business under the specified organization")
  public ResponseEntity<ApiResponse<BusinessResponse>> createBusiness(
      @PathVariable Long organizationId, @Valid @RequestBody CreateBusinessRequest request) {

    log.info("Creating business for organizationId={}", organizationId);

    BusinessResponse response = businessService.createBusiness(organizationId, request);

    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{businessId}")
            .buildAndExpand(response.id())
            .toUri();

    return ResponseEntity.created(location)
        .body(ApiResponse.success("Business created successfully", response));
  }

  @GetMapping
  @Operation(
      summary = "Get organization businesses",
      description = "Retrieves all businesses belonging to the specified organization")
  public ResponseEntity<ApiResponse<List<BusinessResponse>>> getBusinesses(
      @PathVariable Long organizationId) {

    log.debug("Fetching businesses for organizationId={}", organizationId);

    List<BusinessResponse> businesses = businessService.getBusinesses(organizationId);

    return ResponseEntity.ok(ApiResponse.success("Businesses retrieved successfully", businesses));
  }

  @GetMapping("/{businessId}")
  @Operation(
      summary = "Get business details",
      description = "Retrieves a business belonging to the specified organization")
  public ResponseEntity<ApiResponse<BusinessResponse>> getBusiness(
      @PathVariable Long organizationId, @PathVariable Long businessId) {

    log.debug(
        "Fetching business with businessId={} for organizationId={}", businessId, organizationId);

    BusinessResponse response = businessService.getBusiness(organizationId, businessId);

    return ResponseEntity.ok(ApiResponse.success("Business retrieved successfully", response));
  }

  @PutMapping("/{businessId}")
  @Operation(
      summary = "Update business",
      description = "Updates an existing business belonging to the specified organization")
  public ResponseEntity<ApiResponse<BusinessResponse>> updateBusiness(
      @PathVariable Long organizationId,
      @PathVariable Long businessId,
      @Valid @RequestBody UpdateBusinessRequest request) {

    log.info(
        "Updating business with businessId={} for organizationId={}", businessId, organizationId);

    BusinessResponse response = businessService.updateBusiness(organizationId, businessId, request);

    return ResponseEntity.ok(ApiResponse.success("Business updated successfully", response));
  }

  @DeleteMapping("/{businessId}")
  @Operation(
      summary = "Deactivate business",
      description = "Deactivates a business belonging to the specified organization")
  public ResponseEntity<ApiResponse<Void>> deactivateBusiness(
      @PathVariable Long organizationId, @PathVariable Long businessId) {

    log.info(
        "Deactivating business with businessId={} for organizationId={}",
        businessId,
        organizationId);

    businessService.deactivateBusiness(organizationId, businessId);

    return ResponseEntity.ok(ApiResponse.success("Business deactivated successfully"));
  }
}
