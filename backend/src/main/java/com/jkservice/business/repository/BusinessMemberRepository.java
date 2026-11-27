package com.jkservice.business.repository;

import com.jkservice.business.entity.BusinessMemberEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessMemberRepository extends JpaRepository<BusinessMemberEntity, Long> {

  List<BusinessMemberEntity> findAllByOrganizationIdAndBusinessIdOrderByIdAsc(
      Long organizationId, Long businessId);

  Optional<BusinessMemberEntity> findByIdAndOrganizationIdAndBusinessId(
      Long id, Long organizationId, Long businessId);

  boolean existsByOrganizationIdAndBusinessIdAndOrganizationMemberId(
      Long organizationId, Long businessId, Long organizationMemberId);
}
