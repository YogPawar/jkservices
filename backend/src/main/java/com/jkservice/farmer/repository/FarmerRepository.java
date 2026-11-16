package com.jkservice.farmer.repository;

import com.jkservice.farmer.entity.FarmerEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmerRepository extends JpaRepository<FarmerEntity, Long> {
  Optional<FarmerEntity> findByIdAndFamily_Id(Long farmerId, Long familyId);

  List<FarmerEntity> findAllByFamily_Id(Long familyId);

  boolean existsByFamily_IdAndFarmerCode(Long familyId, String farmerCode);
}
