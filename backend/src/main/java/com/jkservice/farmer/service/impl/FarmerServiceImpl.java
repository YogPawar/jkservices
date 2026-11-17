package com.jkservice.farmer.service.impl;

import com.jkservice.common.exception.BusinessException;
import com.jkservice.common.exception.ResourceNotFoundException;
import com.jkservice.family.entity.FamilyEntity;
import com.jkservice.family.repository.FamilyRepository;
import com.jkservice.farmer.dto.CreateFarmerRequest;
import com.jkservice.farmer.dto.FarmerResponse;
import com.jkservice.farmer.dto.UpdateFarmerRequest;
import com.jkservice.farmer.entity.FarmerEntity;
import com.jkservice.farmer.mapper.FarmerMapper;
import com.jkservice.farmer.repository.FarmerRepository;
import com.jkservice.farmer.service.FarmerCodeGenerator;
import com.jkservice.farmer.service.FarmerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FarmerServiceImpl implements FarmerService {

  private final FarmerRepository farmerRepository;
  private final FamilyRepository familyRepository;
  private final FarmerMapper farmerMapper;
  private final FarmerCodeGenerator farmerCodeGenerator;

  @Override
  public FarmerResponse createFarmer(Long familyId, CreateFarmerRequest request) {

    FamilyEntity family = getActiveFamily(familyId);

    String farmerCode = farmerCodeGenerator.generate();

    FarmerEntity farmer = farmerMapper.toEntity(request, family, farmerCode);

    FarmerEntity savedFarmer = farmerRepository.save(farmer);

    return farmerMapper.toResponse(savedFarmer);
  }

  @Override
  @Transactional(readOnly = true)
  public List<FarmerResponse> getFarmers(Long familyId) {

    getFamily(familyId);

    return farmerRepository.findAllByFamily_Id(familyId).stream()
        .map(farmerMapper::toResponse)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public FarmerResponse getFarmer(Long familyId, Long farmerId) {

    FarmerEntity farmer = getFarmerEntity(familyId, farmerId);

    return farmerMapper.toResponse(farmer);
  }

  @Override
  public FarmerResponse updateFarmer(Long familyId, Long farmerId, UpdateFarmerRequest request) {

    getActiveFamily(familyId);

    FarmerEntity farmer = getFarmerEntity(familyId, farmerId);

    farmerMapper.updateEntity(request, farmer);

    return farmerMapper.toResponse(farmer);
  }

  @Override
  public void deactivateFarmer(Long familyId, Long farmerId) {

    getActiveFamily(familyId);

    FarmerEntity farmer = getFarmerEntity(familyId, farmerId);

    // Idempotent soft deletion.
    farmer.setActive(false);
  }

  private FamilyEntity getFamily(Long familyId) {

    return familyRepository
        .findById(familyId)
        .orElseThrow(() -> new ResourceNotFoundException("Family not found with ID: " + familyId));
  }

  private FamilyEntity getActiveFamily(Long familyId) {

    FamilyEntity family = getFamily(familyId);

    if (!family.isActive()) {
      throw new BusinessException("Cannot modify farmers under an inactive family");
    }

    return family;
  }

  private FarmerEntity getFarmerEntity(Long familyId, Long farmerId) {

    return farmerRepository
        .findByIdAndFamily_Id(farmerId, familyId)
        .orElseThrow(() ->
            new ResourceNotFoundException(
                "Farmer not found with ID: " + farmerId + " in family: " + familyId));
  }
}
