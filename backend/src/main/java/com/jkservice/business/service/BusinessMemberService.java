package com.jkservice.business.service;

import com.jkservice.business.dto.request.AssignBusinessMemberRequest;
import com.jkservice.business.dto.request.UpdateBusinessMemberRequest;
import com.jkservice.business.dto.response.BusinessMemberResponse;
import java.util.List;

public interface BusinessMemberService {

  BusinessMemberResponse assignMember(
      Long organizationId, Long businessId, AssignBusinessMemberRequest request);

  List<BusinessMemberResponse> getMembers(Long organizationId, Long businessId);

  BusinessMemberResponse getMember(Long organizationId, Long businessId, Long memberId);

  BusinessMemberResponse updateMember(
      Long organizationId, Long businessId, Long memberId, UpdateBusinessMemberRequest request);

  void unassignMember(Long organizationId, Long businessId, Long memberId);
}
