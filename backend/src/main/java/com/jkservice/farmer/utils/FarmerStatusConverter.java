package com.jkservice.farmer.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class FarmerStatusConverter implements AttributeConverter<FarmerStatus, Boolean> {

  @Override
  public Boolean convertToDatabaseColumn(FarmerStatus status) {
    return status == null || status == FarmerStatus.ACTIVE;
  }

  @Override
  public FarmerStatus convertToEntityAttribute(Boolean active) {
    return Boolean.TRUE.equals(active) ? FarmerStatus.ACTIVE : FarmerStatus.INACTIVE;
  }
}
