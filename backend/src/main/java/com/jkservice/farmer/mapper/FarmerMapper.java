
package com.jkservice.farmer.mapper;

import com.jkservice.customer.entity.CustomerEntity;
import com.jkservice.farmer.dto.CreateFarmerRequest;
import com.jkservice.farmer.dto.FarmerResponse;
import com.jkservice.farmer.dto.UpdateFarmerRequest;
import com.jkservice.farmer.entity.FarmerEntity;
import org.springframework.stereotype.Component;

@Component
public class FarmerMapper {

  public CustomerEntity toCustomerEntity(
          CreateFarmerRequest request, Long businessId, String customerCode) {

    CustomerEntity customer = new CustomerEntity();

    customer.setBusinessId(businessId);
    customer.setCustomerCode(customerCode);
    customer.setName(request.name().trim());
    customer.setPhone(normalizeOptional(request.phone()));
    customer.setAddress(normalizeOptional(request.address()));
    customer.setActive(request.active());

    return customer;
  }

  public FarmerEntity toFarmerEntity(
          CreateFarmerRequest request,
          Long businessId,
          Long customerId,
          String farmerCode) {

    FarmerEntity farmer = new FarmerEntity();

    farmer.setBusinessId(businessId);
    farmer.setCustomerId(customerId);
    farmer.setFarmerCode(farmerCode);
    farmer.setVillage(normalizeOptional(request.village()));

    return farmer;
  }

  public void updateCustomerEntity(UpdateFarmerRequest request, CustomerEntity customer) {

    customer.setName(request.name().trim());
    customer.setPhone(normalizeOptional(request.phone()));
    customer.setAddress(normalizeOptional(request.address()));
    customer.setActive(request.active());
  }

  public void updateFarmerEntity(UpdateFarmerRequest request, FarmerEntity farmer) {

    farmer.setVillage(normalizeOptional(request.village()));
  }

  public FarmerResponse toResponse(FarmerEntity farmer, CustomerEntity customer) {

    return new FarmerResponse(
            farmer.getId(),
            farmer.getFarmerCode(),
            customer.getName(),
            customer.getPhone(),
            farmer.getVillage(),
            customer.getAddress(),
            customer.isActive(),
            farmer.getCreatedAt());
  }

  private String normalizeOptional(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }

    return value.trim();
  }
}
