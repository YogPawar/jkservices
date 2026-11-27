package com.jkservice.organization.service;

import com.jkservice.organization.dto.request.CreateOrganizationMemberRequest;
import com.jkservice.organization.dto.request.UpdateOrganizationMemberRequest;
import com.jkservice.organization.dto.response.OrganizationMemberResponse;
import java.util.List;

public interface OrganizationMemberService {

  OrganizationMemberResponse addMember(
      Long organizationId, CreateOrganizationMemberRequest request);

  List<OrganizationMemberResponse> getMembers(Long organizationId);

  OrganizationMemberResponse getMember(Long organizationId, Long memberId);

  OrganizationMemberResponse updateMember(
      Long organizationId, Long memberId, UpdateOrganizationMemberRequest request);

  void removeMember(Long organizationId, Long memberId);
}
