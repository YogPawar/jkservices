package com.jkservice.business.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.jkservice.business.dto.response.BusinessMemberResponse;
import com.jkservice.business.entity.BusinessMemberEntity;
import com.jkservice.business.enums.BusinessMemberRole;
import com.jkservice.business.enums.BusinessMembershipStatus;
import org.junit.jupiter.api.Test;

class BusinessMemberMapperTest {

  private final BusinessMemberMapper mapper = new BusinessMemberMapper();

  @Test
  void toResponse_shouldMapAllFields() {
    BusinessMemberEntity entity = new BusinessMemberEntity();
    entity.setId(30L);
    entity.setOrganizationId(1L);
    entity.setBusinessId(10L);
    entity.setOrganizationMemberId(20L);
    entity.setRole("MANAGER");
    entity.setMembershipStatus("ACTIVE");

    BusinessMemberResponse response = mapper.toResponse(entity);

    assertEquals(30L, response.id());
    assertEquals(1L, response.organizationId());
    assertEquals(10L, response.businessId());
    assertEquals(20L, response.organizationMemberId());
    assertEquals(BusinessMemberRole.MANAGER, response.role());
    assertEquals(BusinessMembershipStatus.ACTIVE, response.membershipStatus());
  }

  @Test
  void toResponse_shouldMapSuspendedMembership() {
    BusinessMemberEntity entity = new BusinessMemberEntity();
    entity.setId(30L);
    entity.setOrganizationId(1L);
    entity.setBusinessId(10L);
    entity.setOrganizationMemberId(20L);
    entity.setRole("VIEWER");
    entity.setMembershipStatus("SUSPENDED");

    BusinessMemberResponse response = mapper.toResponse(entity);

    assertEquals(BusinessMemberRole.VIEWER, response.role());
    assertEquals(BusinessMembershipStatus.SUSPENDED, response.membershipStatus());
  }
}
