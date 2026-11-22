package com.jkservice.business.repository;

import com.jkservice.business.entity.BusinessEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessRepository extends JpaRepository<BusinessEntity, Long> {

  List<BusinessEntity> findAllByOrganizationId(Long organizationId);

  Optional<BusinessEntity> findByIdAndOrganizationId(Long businessId, Long organizationId);

  Optional<BusinessEntity> findByIdAndOrganizationIdAndActiveTrue(
      Long businessId, Long organizationId);

  Optional<BusinessEntity> findByIdAndActiveTrue(Long businessId);

  boolean existsByOrganizationIdAndBusinessCode(Long organizationId, String businessCode);

  boolean existsByOrganizationIdAndBusinessCodeAndIdNot(
      Long organizationId, String businessCode, Long businessId);
}
