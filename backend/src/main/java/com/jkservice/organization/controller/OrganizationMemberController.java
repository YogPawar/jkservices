package com.jkservice.organization.controller;

import com.jkservice.common.response.ApiResponse;
import com.jkservice.organization.dto.request.CreateOrganizationMemberRequest;
import com.jkservice.organization.dto.request.UpdateOrganizationMemberRequest;
import com.jkservice.organization.dto.response.OrganizationMemberResponse;
import com.jkservice.organization.service.OrganizationMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/organizations/{organizationId}/members")
@Tag(
    name = "Organization Members",
    description =
        """
        APIs for managing organization memberships.
        Organization members represent users who belong to an organization.
        Business-specific access is managed separately through BusinessMember.
        """)
public class OrganizationMemberController {

  private final OrganizationMemberService memberService;

  @PostMapping
  @Operation(
      summary = "Add a user to an organization",
      description =
          """
          Adds an existing active application user to the specified organization.
          A user can belong to multiple organizations, but only one membership
          per user is allowed within a particular organization.
          """)
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "Organization member created successfully",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid request or inactive user",
        content =
            @Content(
                schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Organization or user not found",
        content =
            @Content(
                schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "409",
        description = "User is already a member of the organization",
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
  public ResponseEntity<ApiResponse<OrganizationMemberResponse>> addMember(
      @Parameter(description = "Unique organization ID", example = "1", required = true)
          @PathVariable
          Long organizationId,
      @Valid @RequestBody CreateOrganizationMemberRequest request) {

    OrganizationMemberResponse response = memberService.addMember(organizationId, request);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success("Organization member added successfully", response));
  }

  @GetMapping
  @Operation(
      summary = "List organization members",
      description =
          """
          Retrieves all memberships belonging to the specified organization.
          The response includes each member's user ID, organization role,
          membership status, and membership timestamps.
          """)
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Organization members retrieved successfully"),
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
  public ResponseEntity<ApiResponse<List<OrganizationMemberResponse>>> getMembers(
      @Parameter(description = "Unique organization ID", example = "1", required = true)
          @PathVariable
          Long organizationId) {

    List<OrganizationMemberResponse> members = memberService.getMembers(organizationId);

    return ResponseEntity.ok(
        ApiResponse.success("Organization members retrieved successfully", members));
  }

  @GetMapping("/{memberId}")
  @Operation(
      summary = "Get organization member by ID",
      description =
          """
          Retrieves a specific organization membership.
          The member ID must belong to the organization specified in the path.
          """)
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Organization member retrieved successfully"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Organization or organization member not found",
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
  public ResponseEntity<ApiResponse<OrganizationMemberResponse>> getMember(
      @Parameter(description = "Unique organization ID", example = "1", required = true)
          @PathVariable
          Long organizationId,
      @Parameter(description = "Unique organization membership ID", example = "10", required = true)
          @PathVariable
          Long memberId) {

    OrganizationMemberResponse response = memberService.getMember(organizationId, memberId);

    return ResponseEntity.ok(
        ApiResponse.success("Organization member retrieved successfully", response));
  }

  @PutMapping("/{memberId}")
  @Operation(
      summary = "Update organization member",
      description =
          """
          Updates an organization member's role and membership status.
          The last active organization owner cannot be demoted or suspended.
          Business-specific permissions are managed independently.
          """)
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Organization member updated successfully"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid request or attempt to remove the last active owner",
        content =
            @Content(
                schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Organization or organization member not found",
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
  public ResponseEntity<ApiResponse<OrganizationMemberResponse>> updateMember(
      @Parameter(description = "Unique organization ID", example = "1", required = true)
          @PathVariable
          Long organizationId,
      @Parameter(description = "Unique organization membership ID", example = "10", required = true)
          @PathVariable
          Long memberId,
      @Valid @RequestBody UpdateOrganizationMemberRequest request) {

    OrganizationMemberResponse response =
        memberService.updateMember(organizationId, memberId, request);

    return ResponseEntity.ok(
        ApiResponse.success("Organization member updated successfully", response));
  }

  @DeleteMapping("/{memberId}")
  @Operation(
      summary = "Remove organization member",
      description =
          """
          Permanently removes the organization membership and revokes all
          business assignments associated with that membership.
          The last active organization owner cannot be removed.
          """)
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Organization member removed successfully"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Attempt to remove the last active organization owner",
        content =
            @Content(
                schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Organization or organization member not found",
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
  public ResponseEntity<ApiResponse<Void>> removeMember(
      @Parameter(description = "Unique organization ID", example = "1", required = true)
          @PathVariable
          Long organizationId,
      @Parameter(description = "Unique organization membership ID", example = "10", required = true)
          @PathVariable
          Long memberId) {

    memberService.removeMember(organizationId, memberId);

    return ResponseEntity.ok(ApiResponse.success("Organization member removed successfully"));
  }
}
