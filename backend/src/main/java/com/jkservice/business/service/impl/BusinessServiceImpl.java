package com.jkservice.business.service.impl;

import com.jkservice.business.dto.request.CreateBusinessRequest;
import com.jkservice.business.dto.request.UpdateBusinessRequest;
import com.jkservice.business.dto.response.BusinessResponse;
import com.jkservice.business.entity.BusinessEntity;
import com.jkservice.business.mapper.BusinessMapper;
import com.jkservice.business.repository.BusinessRepository;
import com.jkservice.business.service.BusinessService;
import com.jkservice.common.exception.DuplicateResourceException;
import com.jkservice.common.exception.ResourceNotFoundException;
import com.jkservice.logging.audit.service.AuditLogService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BusinessServiceImpl implements BusinessService {

  private static final String ENTITY_TYPE = "BUSINESS";

  private final BusinessRepository businessRepository;
  private final BusinessMapper businessMapper;
  private final AuditLogService auditLogService;

  @Override
  public BusinessResponse createBusiness(Long organizationId, CreateBusinessRequest request) {

    String businessCode = normalizeBusinessCode(request.businessCode());

    if (businessRepository.existsByOrganizationIdAndBusinessCode(organizationId, businessCode)) {
      throw new DuplicateResourceException("Business code already exists: " + businessCode);
    }

    BusinessEntity business = businessMapper.toEntity(request, organizationId);
    business.setBusinessCode(businessCode);

    BusinessEntity savedBusiness = businessRepository.save(business);

    auditLogService.record(
        organizationId,
        savedBusiness.getId(),
        null,
        "BUSINESS_CREATED",
        ENTITY_TYPE,
        savedBusiness.getId().toString(),
        Map.of(
            "businessName", savedBusiness.getBusinessName(),
            "businessType", savedBusiness.getBusinessType(),
            "businessCode", savedBusiness.getBusinessCode(),
            "currencyCode", savedBusiness.getCurrencyCode()));

    return businessMapper.toResponse(savedBusiness);
  }

  @Override
  @Transactional(readOnly = true)
  public List<BusinessResponse> getBusinesses(Long organizationId) {

    return businessRepository.findAllByOrganizationId(organizationId).stream()
        .map(businessMapper::toResponse)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public BusinessResponse getBusiness(Long organizationId, Long businessId) {

    BusinessEntity business = findBusiness(organizationId, businessId);

    return businessMapper.toResponse(business);
  }

  @Override
  public BusinessResponse updateBusiness(
      Long organizationId, Long businessId, UpdateBusinessRequest request) {

    BusinessEntity business =
        businessRepository
            .findByIdAndOrganizationIdAndActiveTrue(businessId, organizationId)
            .orElseThrow(() ->
                new ResourceNotFoundException("Active business not found with ID: " + businessId));

    String businessCode = normalizeBusinessCode(request.businessCode());

    if (businessRepository.existsByOrganizationIdAndBusinessCodeAndIdNot(
        organizationId, businessCode, businessId)) {
      throw new DuplicateResourceException("Business code already exists: " + businessCode);
    }

    Map<String, Object> before = businessAuditSnapshot(business);

    businessMapper.updateEntity(request, business);
    business.setBusinessCode(businessCode);

    BusinessEntity updatedBusiness = businessRepository.save(business);

    Map<String, Object> after = businessAuditSnapshot(updatedBusiness);

    auditLogService.record(
        organizationId,
        updatedBusiness.getId(),
        null,
        "BUSINESS_UPDATED",
        ENTITY_TYPE,
        updatedBusiness.getId().toString(),
        Map.of("before", before, "after", after));

    return businessMapper.toResponse(updatedBusiness);
  }

  @Override
  public void deactivateBusiness(Long organizationId, Long businessId) {

    BusinessEntity business = findBusiness(organizationId, businessId);

    if (!business.isActive()) {
      return;
    }

    business.setActive(false);
    BusinessEntity deactivatedBusiness = businessRepository.save(business);

    auditLogService.record(
        organizationId,
        deactivatedBusiness.getId(),
        null,
        "BUSINESS_DEACTIVATED",
        ENTITY_TYPE,
        deactivatedBusiness.getId().toString(),
        Map.of(
            "businessName",
            deactivatedBusiness.getBusinessName(),
            "businessCode",
            deactivatedBusiness.getBusinessCode(),
            "previousActive",
            true,
            "active",
            false));
  }

  private Map<String, Object> businessAuditSnapshot(BusinessEntity business) {

    return Map.of(
        "businessName", business.getBusinessName(),
        "businessType", business.getBusinessType(),
        "businessCode", business.getBusinessCode(),
        "currencyCode", business.getCurrencyCode());
  }

  private BusinessEntity findBusiness(Long organizationId, Long businessId) {

    return businessRepository
        .findByIdAndOrganizationId(businessId, organizationId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Business not found with ID: " + businessId));
  }

  private String normalizeBusinessCode(String businessCode) {
    return businessCode.trim().toUpperCase();
  }
}
