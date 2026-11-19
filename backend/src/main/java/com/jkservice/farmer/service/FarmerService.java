package com.jkservice.farmer.service;

import com.jkservice.farmer.dto.CreateFarmerRequest;
import com.jkservice.farmer.dto.FarmerResponse;
import com.jkservice.farmer.dto.UpdateFarmerRequest;
import java.util.List;

public interface FarmerService {

  FarmerResponse createFarmer(Long businessId, CreateFarmerRequest request);

  List<FarmerResponse> getFarmers(Long businessId);

  FarmerResponse getFarmer(Long businessId, Long farmerId);

  FarmerResponse updateFarmer(
          Long businessId, Long farmerId, UpdateFarmerRequest request);

  void deactivateFarmer(Long businessId, Long farmerId);
}