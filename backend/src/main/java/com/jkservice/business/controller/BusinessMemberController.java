package com.jkservice.business.controller;

import com.jkservice.business.dto.request.AssignBusinessMemberRequest;
import com.jkservice.business.dto.request.UpdateBusinessMemberRequest;
import com.jkservice.business.dto.response.BusinessMemberResponse;
import com.jkservice.business.service.BusinessMemberService;
import com.jkservice.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/businesses/{businessId}/members")
@RequiredArgsConstructor
@Validated
@Tag(
    name = "Business Members",
    description = "Manage organization members assigned to individual businesses")
public class BusinessMemberController {

  private final BusinessMemberService businessMemberService;

  @PostMapping
  @Operation(
      summary = "Assign an organization member to a business",
      description =
          """
          Assigns an active organization member to the specified business.
          The member must already belong to the organization.
          Business membership is independent of organization-level membership.
          """)
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "Business member assigned successfully"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid request or organization member is not active"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Organization, business, or organization member not found"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "409",
        description = "Member is already assigned to this business")
  })
  public ResponseEntity<ApiResponse<BusinessMemberResponse>> assignMember(
      @Parameter(description = "Organization ID", example = "1") @PathVariable @Positive
          Long organizationId,
      @Parameter(description = "Business ID", example = "5") @PathVariable @Positive
          Long businessId,
      @Valid @RequestBody AssignBusinessMemberRequest request) {
    BusinessMemberResponse response =
        businessMemberService.assignMember(organizationId, businessId, request);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success("Business member assigned successfully", response));
  }

  @GetMapping
  @Operation(
      summary = "Get business members",
      description = "Returns all members assigned to the specified business.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Business members retrieved successfully"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Organization or business not found")
  })
  public ResponseEntity<ApiResponse<List<BusinessMemberResponse>>> getMembers(
      @Parameter(description = "Organization ID", example = "1") @PathVariable @Positive
          Long organizationId,
      @Parameter(description = "Business ID", example = "5") @PathVariable @Positive
          Long businessId) {
    List<BusinessMemberResponse> response =
        businessMemberService.getMembers(organizationId, businessId);

    return ResponseEntity.ok(
        ApiResponse.success("Business members retrieved successfully", response));
  }

  @GetMapping("/{memberId}")
  @Operation(
      summary = "Get business member by ID",
      description =
          "Retrieves a membership only if it belongs to the specified organization and business.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Business member retrieved successfully"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Business member not found")
  })
  public ResponseEntity<ApiResponse<BusinessMemberResponse>> getMember(
      @Parameter(description = "Organization ID", example = "1") @PathVariable @Positive
          Long organizationId,
      @Parameter(description = "Business ID", example = "5") @PathVariable @Positive
          Long businessId,
      @Parameter(description = "Business member ID", example = "101") @PathVariable @Positive
          Long memberId) {
    BusinessMemberResponse response =
        businessMemberService.getMember(organizationId, businessId, memberId);

    return ResponseEntity.ok(
        ApiResponse.success("Business member retrieved successfully", response));
  }

  @PutMapping("/{memberId}")
  @Operation(
      summary = "Update business member",
      description = "Updates the role and membership status for a business member.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Business member updated successfully"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Business member not found"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid request")
  })
  public ResponseEntity<ApiResponse<BusinessMemberResponse>> updateMember(
      @Parameter(description = "Organization ID", example = "1") @PathVariable @Positive
          Long organizationId,
      @Parameter(description = "Business ID", example = "5") @PathVariable @Positive
          Long businessId,
      @Parameter(description = "Business member ID", example = "101") @PathVariable @Positive
          Long memberId,
      @Valid @RequestBody UpdateBusinessMemberRequest request) {
    BusinessMemberResponse response =
        businessMemberService.updateMember(organizationId, businessId, memberId, request);

    return ResponseEntity.ok(ApiResponse.success("Business member updated successfully", response));
  }

  @DeleteMapping("/{memberId}")
  @Operation(
      summary = "Unassign member from business",
      description =
          """
          Removes the business membership assignment.
          This does not delete or deactivate the person's organization membership.
          """)
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Business member unassigned successfully"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Business member not found")
  })
  public ResponseEntity<ApiResponse<Void>> unassignMember(
      @Parameter(description = "Organization ID", example = "1") @PathVariable @Positive
          Long organizationId,
      @Parameter(description = "Business ID", example = "5") @PathVariable @Positive
          Long businessId,
      @Parameter(description = "Business member ID", example = "101") @PathVariable @Positive
          Long memberId) {
    businessMemberService.unassignMember(organizationId, businessId, memberId);

    return ResponseEntity.ok(ApiResponse.success("Business member unassigned successfully"));
  }
}
