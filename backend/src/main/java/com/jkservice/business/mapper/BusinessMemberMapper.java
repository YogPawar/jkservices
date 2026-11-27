package com.jkservice.business.mapper;

import com.jkservice.business.dto.response.BusinessMemberResponse;
import com.jkservice.business.entity.BusinessMemberEntity;
import com.jkservice.business.enums.BusinessMemberRole;
import com.jkservice.business.enums.BusinessMembershipStatus;
import org.springframework.stereotype.Component;

@Component
public class BusinessMemberMapper {

  public BusinessMemberResponse toResponse(BusinessMemberEntity entity) {
    return new BusinessMemberResponse(
        entity.getId(),
        entity.getOrganizationId(),
        entity.getBusinessId(),
        entity.getOrganizationMemberId(),
        BusinessMemberRole.valueOf(entity.getRole()),
        BusinessMembershipStatus.valueOf(entity.getMembershipStatus()),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
