package com.jkservice.farmer.mapper;

import com.jkservice.family.entity.FamilyEntity;
import com.jkservice.farmer.dto.CreateFarmerRequest;
import com.jkservice.farmer.dto.FarmerResponse;
import com.jkservice.farmer.dto.UpdateFarmerRequest;
import com.jkservice.farmer.entity.FarmerEntity;
import org.springframework.stereotype.Component;

@Component
public class FarmerMapper {

  public FarmerEntity toEntity(
      CreateFarmerRequest request, FamilyEntity family, String farmerCode) {

    FarmerEntity farmer = new FarmerEntity();

    farmer.setFamily(family);
    farmer.setFarmerCode(farmerCode);
    farmer.setName(request.name().trim());
    farmer.setPhone(normalizeOptional(request.phone()));
    farmer.setVillage(normalizeOptional(request.village()));
    farmer.setAddress(normalizeOptional(request.address()));
    farmer.setActive(request.active());

    return farmer;
  }

  public void updateEntity(UpdateFarmerRequest request, FarmerEntity farmer) {

    farmer.setName(request.name().trim());
    farmer.setPhone(normalizeOptional(request.phone()));
    farmer.setVillage(normalizeOptional(request.village()));
    farmer.setAddress(normalizeOptional(request.address()));
    farmer.setActive(request.active());
  }

  public FarmerResponse toResponse(FarmerEntity farmer) {

    return new FarmerResponse(
        farmer.getId(),
        farmer.getFarmerCode(),
        farmer.getName(),
        farmer.getPhone(),
        farmer.getVillage(),
        farmer.getAddress(),
        farmer.isActive(),
        farmer.getCreatedAt());
  }

  private String normalizeOptional(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }

    return value.trim();
  }
}
