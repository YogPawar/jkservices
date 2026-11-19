package com.jkservice.organization.repository;

import com.jkservice.organization.entity.OrganizationMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationMemberRepository
        extends JpaRepository<OrganizationMemberEntity, Long> {
}