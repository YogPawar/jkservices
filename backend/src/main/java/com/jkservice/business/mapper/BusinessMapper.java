package com.jkservice.business.mapper;

import com.jkservice.business.dto.request.CreateBusinessRequest;
import com.jkservice.business.dto.request.UpdateBusinessRequest;
import com.jkservice.business.dto.response.BusinessResponse;
import com.jkservice.business.entity.BusinessEntity;
import org.springframework.stereotype.Component;

@Component
public class BusinessMapper {

  public BusinessEntity toEntity(CreateBusinessRequest request, Long organizationId) {

    BusinessEntity business = new BusinessEntity();

    business.setOrganizationId(organizationId);
    business.setBusinessName(request.businessName().trim());
    business.setBusinessType(request.businessType().name());
    business.setBusinessCode(request.businessCode().trim().toUpperCase());
    business.setContactNumber(normalizeOptional(request.contactNumber()));
    business.setEmail(normalizeOptional(request.email()));
    business.setAddress(normalizeOptional(request.address()));
    business.setCurrencyCode(
        request.currencyCode() == null || request.currencyCode().isBlank()
            ? "INR"
            : request.currencyCode().trim().toUpperCase());
    business.setActive(true);

    return business;
  }

  public void updateEntity(UpdateBusinessRequest request, BusinessEntity business) {

    business.setBusinessName(request.businessName().trim());
    business.setBusinessType(request.businessType().name());
    business.setBusinessCode(request.businessCode().trim().toUpperCase());
    business.setContactNumber(normalizeOptional(request.contactNumber()));
    business.setEmail(normalizeOptional(request.email()));
    business.setAddress(normalizeOptional(request.address()));
    business.setCurrencyCode(request.currencyCode().trim().toUpperCase());
  }

  public BusinessResponse toResponse(BusinessEntity business) {

    return new BusinessResponse(
        business.getId(),
        business.getBusinessName(),
        business.getBusinessType(),
        business.getBusinessCode(),
        business.getContactNumber(),
        business.getEmail(),
        business.getAddress(),
        business.getCurrencyCode(),
        business.isActive(),
        business.getCreatedAt(),
        business.getUpdatedAt());
  }

  private String normalizeOptional(String value) {
    return value == null || value.isBlank() ? null : value.trim();
  }
}
