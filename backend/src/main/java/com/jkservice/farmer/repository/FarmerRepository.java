package com.jkservice.farmer.repository;

import com.jkservice.farmer.entity.FarmerEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmerRepository extends JpaRepository<FarmerEntity, Long> {

  Optional<FarmerEntity> findByIdAndBusinessId(Long farmerId, Long businessId);

  List<FarmerEntity> findAllByBusinessId(Long businessId);

  boolean existsByBusinessIdAndFarmerCode(Long businessId, String farmerCode);

  boolean existsByBusinessIdAndCustomerId(Long businessId, Long customerId);
}
