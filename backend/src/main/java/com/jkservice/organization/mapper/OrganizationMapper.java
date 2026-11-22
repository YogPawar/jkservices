package com.jkservice.organization.mapper;

import com.jkservice.organization.dto.response.OrganizationResponse;
import com.jkservice.organization.entity.OrganizationEntity;
import org.springframework.stereotype.Component;

@Component
public class OrganizationMapper {

  public OrganizationResponse toResponse(OrganizationEntity organization) {

    return new OrganizationResponse(
        organization.getId(),
        organization.getOrganizationName(),
        organization.getInviteCode(),
        organization.isActive(),
        organization.getCreatedAt(),
        organization.getUpdatedAt());
  }
}
