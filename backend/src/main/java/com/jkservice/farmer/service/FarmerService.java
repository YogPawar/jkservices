package com.jkservice.farmer.service;

import com.jkservice.farmer.dto.CreateFarmerRequest;
import com.jkservice.farmer.dto.FarmerResponse;
import com.jkservice.farmer.dto.UpdateFarmerRequest;
import java.util.List;

public interface FarmerService {

  FarmerResponse createFarmer(Long familyId, CreateFarmerRequest request);

  List<FarmerResponse> getFarmers(Long familyId);

  FarmerResponse getFarmer(Long familyId, Long farmerId);

  FarmerResponse updateFarmer(Long familyId, Long farmerId, UpdateFarmerRequest request);

  void deactivateFarmer(Long familyId, Long farmerId);
}
