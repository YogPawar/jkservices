package com.jkservice.business.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.jkservice.business.dto.request.CreateBusinessRequest;
import com.jkservice.business.dto.request.UpdateBusinessRequest;
import com.jkservice.business.dto.response.BusinessResponse;
import com.jkservice.business.entity.BusinessEntity;
import com.jkservice.business.enums.BusinessType;
import com.jkservice.business.mapper.BusinessMapper;
import com.jkservice.business.repository.BusinessRepository;
import com.jkservice.common.exception.DuplicateResourceException;
import com.jkservice.common.exception.ResourceNotFoundException;
import com.jkservice.logging.audit.service.AuditLogService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BusinessServiceImplTest {

  private static final Long ORGANIZATION_ID = 1L;
  private static final Long BUSINESS_ID = 10L;

  @Mock private BusinessRepository businessRepository;

  @Mock private BusinessMapper businessMapper;

  @Mock private AuditLogService auditLogService;

  @InjectMocks private BusinessServiceImpl businessService;

  private BusinessEntity business;
  private BusinessResponse businessResponse;
  private CreateBusinessRequest createRequest;
  private UpdateBusinessRequest updateRequest;

  @BeforeEach
  void setUp() {
    business = new BusinessEntity();
    business.setId(BUSINESS_ID);
    business.setOrganizationId(ORGANIZATION_ID);
    business.setBusinessName("Pawar Farming");
    business.setBusinessCode("PAWAR-001");
    business.setBusinessType(String.valueOf(BusinessType.FARMING_SERVICES));
    business.setCurrencyCode("INR");
    business.setActive(true);

    businessResponse = org.mockito.Mockito.mock(BusinessResponse.class);

    createRequest =
        new CreateBusinessRequest("Pawar Farming", null, "pawar-001", null, null, null, null);

    updateRequest =
        new UpdateBusinessRequest("Updated Farming", null, "pawar-002", null, null, null, null);
  }

  @Test
  void shouldCreateBusinessSuccessfully() {
    BusinessEntity newBusiness = new BusinessEntity();
    newBusiness.setId(BUSINESS_ID);
    newBusiness.setOrganizationId(ORGANIZATION_ID);
    newBusiness.setBusinessName("Pawar Farming");
    newBusiness.setBusinessCode("PAWAR-001");
    newBusiness.setBusinessType(String.valueOf(BusinessType.FARMING_SERVICES));
    newBusiness.setCurrencyCode("INR"); // Missing field
    newBusiness.setActive(true);

    when(businessRepository.existsByOrganizationIdAndBusinessCode(ORGANIZATION_ID, "PAWAR-001"))
        .thenReturn(false);

    when(businessMapper.toEntity(createRequest, ORGANIZATION_ID)).thenReturn(newBusiness);

    when(businessRepository.save(newBusiness)).thenReturn(newBusiness);

    when(businessMapper.toResponse(newBusiness)).thenReturn(businessResponse);

    BusinessResponse result = businessService.createBusiness(ORGANIZATION_ID, createRequest);

    assertEquals(businessResponse, result);

    verify(businessRepository).existsByOrganizationIdAndBusinessCode(ORGANIZATION_ID, "PAWAR-001");

    verify(businessRepository).save(newBusiness);
    verify(businessMapper).toResponse(newBusiness);

    verify(auditLogService)
        .record(
            eq(ORGANIZATION_ID),
            eq(BUSINESS_ID),
            isNull(),
            eq("BUSINESS_CREATED"),
            eq("BUSINESS"),
            eq(BUSINESS_ID.toString()),
            any());
  }

  @Test
  void shouldThrowExceptionWhenCreatingBusinessWithDuplicateCode() {
    when(businessRepository.existsByOrganizationIdAndBusinessCode(ORGANIZATION_ID, "PAWAR-001"))
        .thenReturn(true);

    assertThrows(
        DuplicateResourceException.class,
        () -> businessService.createBusiness(ORGANIZATION_ID, createRequest));

    verifyNoInteractions(auditLogService);
  }

  @Test
  void shouldGetAllBusinessesForOrganization() {
    when(businessRepository.findAllByOrganizationId(ORGANIZATION_ID)).thenReturn(List.of(business));

    when(businessMapper.toResponse(business)).thenReturn(businessResponse);

    List<BusinessResponse> result = businessService.getBusinesses(ORGANIZATION_ID);

    assertEquals(1, result.size());
    assertEquals(businessResponse, result.get(0));
  }

  @Test
  void shouldGetBusinessSuccessfully() {
    when(businessRepository.findByIdAndOrganizationId(BUSINESS_ID, ORGANIZATION_ID))
        .thenReturn(Optional.of(business));

    when(businessMapper.toResponse(business)).thenReturn(businessResponse);

    BusinessResponse result = businessService.getBusiness(ORGANIZATION_ID, BUSINESS_ID);

    assertEquals(businessResponse, result);
  }

  @Test
  void shouldThrowExceptionWhenBusinessDoesNotExist() {
    when(businessRepository.findByIdAndOrganizationId(BUSINESS_ID, ORGANIZATION_ID))
        .thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> businessService.getBusiness(ORGANIZATION_ID, BUSINESS_ID));
  }

  @Test
  void shouldUpdateBusinessSuccessfully() {
    when(businessRepository.findByIdAndOrganizationIdAndActiveTrue(BUSINESS_ID, ORGANIZATION_ID))
        .thenReturn(Optional.of(business));

    when(businessRepository.existsByOrganizationIdAndBusinessCodeAndIdNot(
            ORGANIZATION_ID, "PAWAR-002", BUSINESS_ID))
        .thenReturn(false);

    when(businessRepository.save(business)).thenReturn(business);
    when(businessMapper.toResponse(business)).thenReturn(businessResponse);

    BusinessResponse result =
        businessService.updateBusiness(ORGANIZATION_ID, BUSINESS_ID, updateRequest);

    assertEquals(businessResponse, result);

    verify(businessRepository).save(business);

    verify(auditLogService)
        .record(
            eq(ORGANIZATION_ID),
            eq(BUSINESS_ID),
            isNull(),
            eq("BUSINESS_UPDATED"),
            eq("BUSINESS"),
            eq(BUSINESS_ID.toString()),
            any());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistingBusiness() {
    when(businessRepository.findByIdAndOrganizationIdAndActiveTrue(BUSINESS_ID, ORGANIZATION_ID))
        .thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> businessService.updateBusiness(ORGANIZATION_ID, BUSINESS_ID, updateRequest));

    verifyNoInteractions(auditLogService);
  }

  @Test
  void shouldThrowExceptionWhenUpdatingWithDuplicateCode() {
    when(businessRepository.findByIdAndOrganizationIdAndActiveTrue(BUSINESS_ID, ORGANIZATION_ID))
        .thenReturn(Optional.of(business));

    when(businessRepository.existsByOrganizationIdAndBusinessCodeAndIdNot(
            ORGANIZATION_ID, "PAWAR-002", BUSINESS_ID))
        .thenReturn(true);

    assertThrows(
        DuplicateResourceException.class,
        () -> businessService.updateBusiness(ORGANIZATION_ID, BUSINESS_ID, updateRequest));

    verifyNoInteractions(auditLogService);
  }

  @Test
  void shouldDeactivateBusinessSuccessfully() {
    when(businessRepository.findByIdAndOrganizationId(BUSINESS_ID, ORGANIZATION_ID))
        .thenReturn(Optional.of(business));

    when(businessRepository.save(business)).thenReturn(business);

    businessService.deactivateBusiness(ORGANIZATION_ID, BUSINESS_ID);

    assertEquals(false, business.isActive());

    verify(auditLogService)
        .record(
            eq(ORGANIZATION_ID),
            eq(BUSINESS_ID),
            isNull(),
            eq("BUSINESS_DEACTIVATED"),
            eq("BUSINESS"),
            eq(BUSINESS_ID.toString()),
            any());
  }

  @Test
  void shouldNotCreateAuditLogWhenBusinessIsAlreadyInactive() {
    business.setActive(false);

    when(businessRepository.findByIdAndOrganizationId(BUSINESS_ID, ORGANIZATION_ID))
        .thenReturn(Optional.of(business));

    businessService.deactivateBusiness(ORGANIZATION_ID, BUSINESS_ID);

    verifyNoInteractions(auditLogService);
  }

  @Test
  void shouldThrowExceptionWhenDeactivatingNonExistingBusiness() {
    when(businessRepository.findByIdAndOrganizationId(BUSINESS_ID, ORGANIZATION_ID))
        .thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> businessService.deactivateBusiness(ORGANIZATION_ID, BUSINESS_ID));

    verifyNoInteractions(auditLogService);
  }
}
