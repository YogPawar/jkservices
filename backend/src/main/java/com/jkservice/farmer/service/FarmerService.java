package com.jkservice.farmer.service;

import com.jkservice.farmer.dto.CreateFarmerRequest;
import com.jkservice.farmer.dto.FarmerResponse;

public interface FarmerService {
  FarmerResponse createFarmer(Long familyId, CreateFarmerRequest request);
}
