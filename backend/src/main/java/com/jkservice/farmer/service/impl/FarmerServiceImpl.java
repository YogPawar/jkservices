package com.jkservice.farmer.service.impl;

import com.jkservice.common.exception.BusinessException;
import com.jkservice.common.exception.ResourceNotFoundException;
import com.jkservice.family.entity.FamilyEntity;
import com.jkservice.family.repository.FamilyRepository;
import com.jkservice.farmer.dto.CreateFarmerRequest;
import com.jkservice.farmer.dto.FarmerResponse;
import com.jkservice.farmer.entity.FarmerEntity;
import com.jkservice.farmer.mapper.FarmerMapper;
import com.jkservice.farmer.repository.FarmerRepository;
import com.jkservice.farmer.service.FarmerCodeGenerator;
import com.jkservice.farmer.service.FarmerService;
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

    FamilyEntity family =
        familyRepository
            .findById(familyId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Family not found with ID: " + familyId));

    if (!family.isActive()) {
      throw new BusinessException("Cannot create a farmer under an inactive family");
    }

    String farmerCode = farmerCodeGenerator.generate();

    FarmerEntity farmer = farmerMapper.toEntity(request, family, farmerCode);

    FarmerEntity savedFarmer = farmerRepository.save(farmer);

    return farmerMapper.toResponse(savedFarmer);
  }
}
