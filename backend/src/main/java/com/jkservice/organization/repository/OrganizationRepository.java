package com.jkservice.organization.repository;

import com.jkservice.organization.entity.OrganizationEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Long> {

  Optional<OrganizationEntity> findByInviteCodeAndActiveTrue(String inviteCode);

  boolean existsByInviteCode(String inviteCode);
}
