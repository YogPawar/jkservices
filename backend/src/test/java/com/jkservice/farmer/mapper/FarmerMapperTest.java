package com.jkservice.farmer.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.jkservice.customer.entity.CustomerEntity;
import com.jkservice.farmer.dto.CreateFarmerRequest;
import com.jkservice.farmer.dto.FarmerResponse;
import com.jkservice.farmer.dto.UpdateFarmerRequest;
import com.jkservice.farmer.entity.FarmerEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FarmerMapperTest {

  private FarmerMapper farmerMapper;

  private static final Long BUSINESS_ID = 10L;
  private static final Long CUSTOMER_ID = 20L;

  @BeforeEach
  void setUp() {
    farmerMapper = new FarmerMapper();
  }

  @Test
  void shouldMapCreateRequestToCustomerAndFarmerEntities() {

    CreateFarmerRequest request =
        new CreateFarmerRequest(
            "  Yogesh Pawar  ", " 9876543210 ", "  Pune  ", "  Maharashtra  ", true);

    CustomerEntity customer = farmerMapper.toCustomerEntity(request, BUSINESS_ID, "C-001");

    FarmerEntity farmer = farmerMapper.toFarmerEntity(request, BUSINESS_ID, CUSTOMER_ID, "F-001");

    assertAll(
        () -> assertNull(customer.getId()),
        () -> assertEquals(BUSINESS_ID, customer.getBusinessId()),
        () -> assertEquals("C-001", customer.getCustomerCode()),
        () -> assertEquals("Yogesh Pawar", customer.getName()),
        () -> assertEquals("9876543210", customer.getPhone()),
        () -> assertEquals("Maharashtra", customer.getAddress()),
        () -> assertTrue(customer.isActive()),
        () -> assertNull(farmer.getId()),
        () -> assertEquals(BUSINESS_ID, farmer.getBusinessId()),
        () -> assertEquals(CUSTOMER_ID, farmer.getCustomerId()),
        () -> assertEquals("F-001", farmer.getFarmerCode()),
        () -> assertEquals("Pune", farmer.getVillage()));
  }

  @Test
  void shouldNormalizeNullAndBlankOptionalFieldsDuringCreate() {

    CreateFarmerRequest request = new CreateFarmerRequest("Yogesh Pawar", null, "   ", null, true);

    CustomerEntity customer = farmerMapper.toCustomerEntity(request, BUSINESS_ID, "C-002");

    FarmerEntity farmer = farmerMapper.toFarmerEntity(request, BUSINESS_ID, CUSTOMER_ID, "F-002");

    assertAll(
        () -> assertNull(customer.getPhone()),
        () -> assertNull(customer.getAddress()),
        () -> assertNull(farmer.getVillage()));
  }

  @Test
  void shouldUpdateCustomerAndFarmerEntities() {

    CustomerEntity customer = new CustomerEntity();
    customer.setBusinessId(BUSINESS_ID);
    customer.setCustomerCode("C-003");
    customer.setName("Old Name");
    customer.setPhone("9999999999");
    customer.setAddress("Old Address");
    customer.setActive(false);

    FarmerEntity farmer = new FarmerEntity();
    farmer.setBusinessId(BUSINESS_ID);
    farmer.setCustomerId(CUSTOMER_ID);
    farmer.setFarmerCode("F-003");
    farmer.setVillage("Old Village");

    UpdateFarmerRequest request =
        new UpdateFarmerRequest(
            "  Updated Name  ", " 8888888888 ", "  Updated Village ", " Updated Address ", true);

    farmerMapper.updateCustomerEntity(request, customer);
    farmerMapper.updateFarmerEntity(request, farmer);

    assertAll(
        () -> assertEquals(BUSINESS_ID, customer.getBusinessId()),
        () -> assertEquals("C-003", customer.getCustomerCode()),
        () -> assertEquals("Updated Name", customer.getName()),
        () -> assertEquals("8888888888", customer.getPhone()),
        () -> assertEquals("Updated Address", customer.getAddress()),
        () -> assertTrue(customer.isActive()),
        () -> assertEquals(BUSINESS_ID, farmer.getBusinessId()),
        () -> assertEquals(CUSTOMER_ID, farmer.getCustomerId()),
        () -> assertEquals("F-003", farmer.getFarmerCode()),
        () -> assertEquals("Updated Village", farmer.getVillage()));
  }

  @Test
  void shouldNormalizeNullAndBlankOptionalFieldsDuringUpdate() {

    CustomerEntity customer = new CustomerEntity();
    customer.setName("Existing Name");
    customer.setPhone("9999999999");
    customer.setAddress("Existing Address");

    FarmerEntity farmer = new FarmerEntity();
    farmer.setVillage("Existing Village");

    UpdateFarmerRequest request = new UpdateFarmerRequest("Updated Name", null, "   ", null, true);

    farmerMapper.updateCustomerEntity(request, customer);
    farmerMapper.updateFarmerEntity(request, farmer);

    assertAll(
        () -> assertNull(customer.getPhone()),
        () -> assertNull(customer.getAddress()),
        () -> assertNull(farmer.getVillage()));
  }

  @Test
  void shouldMapCustomerAndFarmerEntitiesToResponse() {

    CustomerEntity customer = new CustomerEntity();
    customer.setName("Yogesh Pawar");
    customer.setPhone("9876543210");
    customer.setAddress("Maharashtra");
    customer.setActive(true);

    FarmerEntity farmer = new FarmerEntity();
    farmer.setFarmerCode("F-005");
    farmer.setVillage("Pune");

    FarmerResponse response = farmerMapper.toResponse(farmer, customer);

    assertAll(
        () -> assertNull(response.id()),
        () -> assertEquals("F-005", response.farmerCode()),
        () -> assertEquals("Yogesh Pawar", response.name()),
        () -> assertEquals("9876543210", response.phone()),
        () -> assertEquals("Pune", response.village()),
        () -> assertEquals("Maharashtra", response.address()),
        () -> assertTrue(response.active()),
        () -> assertNull(response.createdAt()));
  }
}
