package com.jkservice.business.repositroy;

import com.jkservice.business.entity.BusinessEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessRepository extends JpaRepository<BusinessEntity, Long> {

  Optional<BusinessEntity> findByIdAndActiveTrue(Long id);
}
