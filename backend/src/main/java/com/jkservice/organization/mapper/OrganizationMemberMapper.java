
package com.jkservice.organization.mapper;

import com.jkservice.organization.dto.response.OrganizationMemberResponse;
import com.jkservice.organization.entity.OrganizationMemberEntity;
import org.springframework.stereotype.Component;

@Component
public class OrganizationMemberMapper {

    public OrganizationMemberResponse toResponse(
            OrganizationMemberEntity entity) {

        return new OrganizationMemberResponse(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getUserId(),
                entity.getRole(),
                entity.getMembershipStatus(),
                entity.getJoinedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
