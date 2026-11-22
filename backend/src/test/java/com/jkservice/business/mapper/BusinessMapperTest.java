package com.jkservice.business.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.jkservice.business.dto.request.CreateBusinessRequest;
import com.jkservice.business.dto.request.UpdateBusinessRequest;
import com.jkservice.business.dto.response.BusinessResponse;
import com.jkservice.business.entity.BusinessEntity;
import com.jkservice.business.enums.BusinessType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BusinessMapperTest {

  private BusinessMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new BusinessMapper();
  }

  @Test
  void shouldMapCreateRequestToEntityAndApplyDefaults() {
    CreateBusinessRequest request =
        new CreateBusinessRequest(
            " Pawar Farming ",
            BusinessType.FARMING_SERVICES,
            " pawar-001 ",
            " 9876543210 ",
            " test@example.com ",
            " Pune ",
            null);

    BusinessEntity result = mapper.toEntity(request, 10L);

    assertEquals(10L, result.getOrganizationId());
    assertEquals("Pawar Farming", result.getBusinessName());
    assertEquals("FARMING_SERVICES", result.getBusinessType());
    assertEquals("PAWAR-001", result.getBusinessCode());
    assertEquals("9876543210", result.getContactNumber());
    assertEquals("test@example.com", result.getEmail());
    assertEquals("Pune", result.getAddress());
    assertEquals("INR", result.getCurrencyCode());
    assertTrue(result.isActive());
  }

  @Test
  void shouldNormalizeProvidedCurrencyCode() {
    CreateBusinessRequest request =
        new CreateBusinessRequest(
            "Pawar Farming", BusinessType.FARMING_SERVICES, "PAWAR-001", null, null, null, " usd ");

    BusinessEntity result = mapper.toEntity(request, 10L);

    assertEquals("USD", result.getCurrencyCode());
  }

  @Test
  void shouldConvertBlankOptionalFieldsToNull() {
    CreateBusinessRequest request =
        new CreateBusinessRequest(
            "Pawar Farming", BusinessType.FARMING_SERVICES, "PAWAR-001", " ", null, " ", null);

    BusinessEntity result = mapper.toEntity(request, 10L);

    assertNull(result.getContactNumber());
    assertNull(result.getEmail());
    assertNull(result.getAddress());
  }

  @Test
  void shouldUpdateEntityWithoutChangingOwnershipOrActiveStatus() {
    BusinessEntity business = new BusinessEntity();
    business.setId(20L);
    business.setOrganizationId(10L);
    business.setActive(true);

    UpdateBusinessRequest request =
        new UpdateBusinessRequest(
            "Updated Farming",
            BusinessType.FARMING_SERVICES,
            "updated-001",
            "9999999999",
            "updated@example.com",
            "Nashik",
            "inr");

    mapper.updateEntity(request, business);

    assertEquals(20L, business.getId());
    assertEquals(10L, business.getOrganizationId());
    assertTrue(business.isActive());
    assertEquals("Updated Farming", business.getBusinessName());
    assertEquals("UPDATED-001", business.getBusinessCode());
    assertEquals("9999999999", business.getContactNumber());
    assertEquals("updated@example.com", business.getEmail());
    assertEquals("Nashik", business.getAddress());
    assertEquals("INR", business.getCurrencyCode());
  }

  @Test
  void shouldMapEntityToResponse() {
    LocalDateTime now = LocalDateTime.now();

    BusinessEntity business = new BusinessEntity();
    business.setId(20L);
    business.setOrganizationId(10L);
    business.setBusinessName("Pawar Farming");
    business.setBusinessType("FARMING_SERVICES");
    business.setBusinessCode("PAWAR-001");
    business.setCurrencyCode("INR");
    business.setActive(true);
    business.setCreatedAt(now);
    business.setUpdatedAt(now);

    BusinessResponse result = mapper.toResponse(business);

    assertEquals(20L, result.id());
    assertEquals("Pawar Farming", result.businessName());
    assertEquals("FARMING_SERVICES", result.businessType());
    assertEquals("PAWAR-001", result.businessCode());
    assertEquals("INR", result.currencyCode());
    assertTrue(result.active());
    assertEquals(now, result.createdAt());
    assertEquals(now, result.updatedAt());
  }
}
