package com.jkservice.organization.controller;

import com.jkservice.common.response.ApiResponse;
import com.jkservice.organization.dto.request.CreateOrganizationRequest;
import com.jkservice.organization.dto.request.UpdateOrganizationRequest;
import com.jkservice.organization.dto.response.OrganizationResponse;
import com.jkservice.organization.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Tag(name = "Organization Management", description = "APIs for creating and managing organizations")
public class OrganizationController {

  private final OrganizationService organizationService;

  @PostMapping
  @Operation(
      summary = "Create organization",
      description =
          "Creates a new organization, registers its initial owner account, "
              + "and assigns the owner membership.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "Organization created successfully",
        content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid request",
        content =
            @Content(
                schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "409",
        description = "Email already registered",
        content =
            @Content(
                schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "500",
        description = "Unexpected server error",
        content =
            @Content(
                schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
  })
  public ResponseEntity<ApiResponse<OrganizationResponse>> createOrganization(
      @Valid @RequestBody CreateOrganizationRequest request) {

    log.info("Creating organization: organizationName={}", request.organizationName());

    OrganizationResponse response = organizationService.createOrganization(request);

    log.info("Organization created successfully: organizationId={}", response.id());

    var location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{organizationId}")
            .buildAndExpand(response.id())
            .toUri();

    return ResponseEntity.created(location)
        .body(ApiResponse.success("Organization created successfully", response));
  }

  @GetMapping
  @Operation(summary = "Get all organizations", description = "Returns all organizations.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Organizations retrieved successfully"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "500",
        description = "Unexpected server error",
        content =
            @Content(
                schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
  })
  public ResponseEntity<ApiResponse<List<OrganizationResponse>>> getOrganizations() {

    List<OrganizationResponse> organizations = organizationService.getOrganizations();

    log.info("Retrieved organizations: count={}", organizations.size());

    return ResponseEntity.ok(
        ApiResponse.success("Organizations retrieved successfully", organizations));
  }

  @GetMapping("/{organizationId}")
  @Operation(
      summary = "Get organization by ID",
      description = "Retrieves organization details using its unique ID.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Organization retrieved successfully"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Organization not found",
        content =
            @Content(
                schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
  })
  public ResponseEntity<ApiResponse<OrganizationResponse>> getOrganization(
      @Parameter(description = "Organization ID", required = true, example = "1") @PathVariable
          Long organizationId) {

    log.info("Retrieving organization: organizationId={}", organizationId);

    OrganizationResponse response = organizationService.getOrganization(organizationId);

    return ResponseEntity.ok(ApiResponse.success("Organization retrieved successfully", response));
  }

  @PutMapping("/{organizationId}")
  @Operation(
      summary = "Update organization",
      description =
          "Updates the organization's name and active status. "
              + "An inactive organization can be reactivated by setting active to true.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Organization updated successfully"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid update request",
        content =
            @Content(
                schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Organization not found",
        content =
            @Content(
                schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "500",
        description = "Unexpected server error",
        content =
            @Content(
                schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
  })
  public ResponseEntity<ApiResponse<OrganizationResponse>> updateOrganization(
      @Parameter(description = "Organization ID", required = true, example = "1") @PathVariable
          Long organizationId,
      @Valid @RequestBody UpdateOrganizationRequest request) {

    log.info("Updating organization: organizationId={}", organizationId);

    OrganizationResponse response = organizationService.updateOrganization(organizationId, request);

    log.info(
        "Organization updated successfully: organizationId={}, active={}",
        organizationId,
        response.active());

    return ResponseEntity.ok(ApiResponse.success("Organization updated successfully", response));
  }

  @DeleteMapping("/{organizationId}")
  @Operation(
      summary = "Deactivate organization",
      description = "Soft-deactivates an organization. The organization record is retained.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Organization deactivated successfully"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Organization not found",
        content =
            @Content(
                schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
  })
  public ResponseEntity<ApiResponse<Void>> deactivateOrganization(
      @Parameter(description = "Organization ID", required = true, example = "1") @PathVariable
          Long organizationId) {

    log.info("Deactivating organization: organizationId={}", organizationId);

    organizationService.deactivateOrganization(organizationId);

    log.info("Organization deactivated successfully: organizationId={}", organizationId);

    return ResponseEntity.ok(ApiResponse.success("Organization deactivated successfully"));
  }
}
