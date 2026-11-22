package com.jkservice.organization.service;

import com.jkservice.organization.dto.request.CreateOrganizationRequest;
import com.jkservice.organization.dto.request.JoinOrganizationRequest;
import com.jkservice.organization.dto.request.UpdateOrganizationRequest;
import com.jkservice.organization.dto.response.OrganizationResponse;
import com.jkservice.organization.dto.response.RegistrationResponse;
import java.util.List;

public interface OrganizationService {

  OrganizationResponse createOrganization(CreateOrganizationRequest request);

  RegistrationResponse joinOrganization(JoinOrganizationRequest request);

  List<OrganizationResponse> getOrganizations();

  OrganizationResponse getOrganization(Long organizationId);

  OrganizationResponse updateOrganization(
      Long organizationId, UpdateOrganizationRequest organizationRequest);

  void deactivateOrganization(Long organizationId);
}
