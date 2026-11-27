package com.jkservice.organization.repository;

import com.jkservice.organization.entity.OrganizationMemberEntity;
import com.jkservice.organization.enums.OrganizationMembershipStatus;
import com.jkservice.organization.enums.OrganizationRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationMemberRepository
    extends JpaRepository<OrganizationMemberEntity, Long> {

  // List all members belonging to an organization.
  List<OrganizationMemberEntity> findAllByOrganizationIdOrderByIdAsc(Long organizationId);

  // Find a specific member within an organization.
  Optional<OrganizationMemberEntity> findByIdAndOrganizationId(Long id, Long organizationId);

  // Find organization membership for a particular user.
  Optional<OrganizationMemberEntity> findByOrganizationIdAndUserId(
      Long organizationId, Long userId);

  // Prevent duplicate memberships.
  boolean existsByOrganizationIdAndUserId(Long organizationId, Long userId);

  // Count active owners for last-owner protection.
  long countByOrganizationIdAndRoleAndMembershipStatus(
      Long organizationId, OrganizationRole role, OrganizationMembershipStatus membershipStatus);

  // Find all active organization memberships for a user.
  List<OrganizationMemberEntity> findAllByUserIdAndMembershipStatus(
      Long userId, OrganizationMembershipStatus membershipStatus);

  // Check whether a user has active membership in an organization.
  boolean existsByOrganizationIdAndUserIdAndMembershipStatus(
      Long organizationId, Long userId, OrganizationMembershipStatus membershipStatus);

  Optional<OrganizationMemberEntity> findByIdAndOrganizationIdAndMembershipStatus(
      Long id, Long organizationId, OrganizationMembershipStatus membershipStatus);
}
