package com.jkservice.organization.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.jkservice.organization.dto.response.OrganizationMemberResponse;
import com.jkservice.organization.entity.OrganizationMemberEntity;
import com.jkservice.organization.enums.OrganizationMembershipStatus;
import com.jkservice.organization.enums.OrganizationRole;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrganizationMemberMapperTest {

  private OrganizationMemberMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new OrganizationMemberMapper();
  }

  @Test
  void toResponse_shouldMapAllFields() {
    Instant now = Instant.parse("2026-11-27T10:30:00Z");

    OrganizationMemberEntity entity = new OrganizationMemberEntity();
    entity.setId(101L);
    entity.setOrganizationId(1L);
    entity.setUserId(12L);
    entity.setRole(OrganizationRole.MEMBER);
    entity.setMembershipStatus(OrganizationMembershipStatus.ACTIVE);
    entity.setJoinedAt(now);
    entity.setCreatedAt(now);
    entity.setUpdatedAt(now);

    OrganizationMemberResponse response = mapper.toResponse(entity);

    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(101L);
    assertThat(response.organizationId()).isEqualTo(1L);
    assertThat(response.userId()).isEqualTo(12L);
    assertThat(response.role()).isEqualTo(OrganizationRole.MEMBER);
    assertThat(response.membershipStatus()).isEqualTo(OrganizationMembershipStatus.ACTIVE);
    assertThat(response.joinedAt()).isEqualTo(now);
    assertThat(response.createdAt()).isEqualTo(now);
    assertThat(response.updatedAt()).isEqualTo(now);
  }
}
