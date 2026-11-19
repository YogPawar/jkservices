package com.jkservice.farmer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.jkservice.business.entity.BusinessEntity;
import com.jkservice.business.repositroy.BusinessRepository;
import com.jkservice.common.exception.BusinessException;
import com.jkservice.common.exception.ResourceNotFoundException;
import com.jkservice.customer.entity.CustomerEntity;
import com.jkservice.customer.repository.CustomerRepository;
import com.jkservice.customer.service.CustomerCodeGenerator;
import com.jkservice.farmer.dto.CreateFarmerRequest;
import com.jkservice.farmer.dto.FarmerResponse;
import com.jkservice.farmer.dto.UpdateFarmerRequest;
import com.jkservice.farmer.entity.FarmerEntity;
import com.jkservice.farmer.mapper.FarmerMapper;
import com.jkservice.farmer.repository.FarmerRepository;
import com.jkservice.farmer.service.impl.FarmerServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FarmerServiceImplTest {

  private static final Long BUSINESS_ID = 10L;
  private static final Long FARMER_ID = 100L;
  private static final Long CUSTOMER_ID = 200L;

  @Mock private BusinessRepository businessRepository;
  @Mock private CustomerRepository customerRepository;
  @Mock private FarmerRepository farmerRepository;
  @Mock private FarmerMapper farmerMapper;
  @Mock private CustomerCodeGenerator customerCodeGenerator;
  @Mock private FarmerCodeGenerator farmerCodeGenerator;

  @InjectMocks private FarmerServiceImpl farmerService;

  private BusinessEntity business;
  private CustomerEntity customer;
  private FarmerEntity farmer;
  private CreateFarmerRequest createRequest;

  @BeforeEach
  void setUp() {
    business = new BusinessEntity();
    business.setId(BUSINESS_ID);
    business.setBusinessName("Pawar Farming");
    business.setBusinessType("FARMING_SERVICES");
    business.setActive(true);

    customer = new CustomerEntity();
    customer.setId(CUSTOMER_ID);
    customer.setBusinessId(BUSINESS_ID);
    customer.setCustomerCode("C-ABC123");
    customer.setName("Ramesh");
    customer.setPhone("9876543210");
    customer.setAddress("Farm address");
    customer.setActive(true);

    farmer = new FarmerEntity();
    farmer.setId(FARMER_ID);
    farmer.setBusinessId(BUSINESS_ID);
    farmer.setCustomerId(CUSTOMER_ID);
    farmer.setFarmerCode("F-ABC123");
    farmer.setVillage("Pune");

    createRequest = new CreateFarmerRequest("Ramesh", "9876543210", "Pune", "Farm address", true);
  }

  @Test
  void shouldCreateFarmerAndCustomerSuccessfully() {
    when(businessRepository.findByIdAndActiveTrue(BUSINESS_ID)).thenReturn(Optional.of(business));
    when(customerCodeGenerator.generate()).thenReturn("C-ABC123");
    when(farmerCodeGenerator.generate()).thenReturn("F-ABC123");
    when(farmerMapper.toCustomerEntity(createRequest, BUSINESS_ID, "C-ABC123"))
        .thenReturn(customer);
    when(customerRepository.save(customer)).thenReturn(customer);
    when(farmerMapper.toFarmerEntity(createRequest, BUSINESS_ID, CUSTOMER_ID, "F-ABC123"))
        .thenReturn(farmer);
    when(farmerRepository.save(farmer)).thenReturn(farmer);

    FarmerResponse expected =
        new FarmerResponse(
            FARMER_ID,
            "F-ABC123",
            "Ramesh",
            "9876543210",
            "Pune",
            "Farm address",
            true,
            LocalDateTime.now());

    when(farmerMapper.toResponse(farmer, customer)).thenReturn(expected);

    FarmerResponse result = farmerService.createFarmer(BUSINESS_ID, createRequest);

    assertSame(expected, result);

    verify(customerRepository).save(customer);
    verify(farmerRepository).save(farmer);
    verify(farmerMapper).toFarmerEntity(createRequest, BUSINESS_ID, CUSTOMER_ID, "F-ABC123");
  }

  @Test
  void shouldRejectCreationWhenBusinessDoesNotExistOrIsInactive() {
    when(businessRepository.findByIdAndActiveTrue(BUSINESS_ID)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> farmerService.createFarmer(BUSINESS_ID, createRequest));

    verifyNoInteractions(
        customerRepository,
        farmerRepository,
        customerCodeGenerator,
        farmerCodeGenerator,
        farmerMapper);
  }

  @Test
  void shouldRejectCreationForNonFarmingBusiness() {
    business.setBusinessType("EVENT_MANAGEMENT");

    when(businessRepository.findByIdAndActiveTrue(BUSINESS_ID)).thenReturn(Optional.of(business));

    assertThrows(
        BusinessException.class, () -> farmerService.createFarmer(BUSINESS_ID, createRequest));

    verifyNoInteractions(
        customerRepository,
        farmerRepository,
        customerCodeGenerator,
        farmerCodeGenerator,
        farmerMapper);
  }

  @Test
  void shouldReturnFarmersBelongingToBusiness() {
    when(businessRepository.findById(BUSINESS_ID)).thenReturn(Optional.of(business));
    when(farmerRepository.findAllByBusinessId(BUSINESS_ID)).thenReturn(List.of(farmer));
    when(customerRepository.findByIdAndBusinessId(CUSTOMER_ID, BUSINESS_ID))
        .thenReturn(Optional.of(customer));

    FarmerResponse expected =
        new FarmerResponse(
            FARMER_ID,
            "F-ABC123",
            "Ramesh",
            "9876543210",
            "Pune",
            "Farm address",
            true,
            LocalDateTime.now());

    when(farmerMapper.toResponse(farmer, customer)).thenReturn(expected);

    List<FarmerResponse> result = farmerService.getFarmers(BUSINESS_ID);

    assertEquals(1, result.size());
    assertSame(expected, result.get(0));

    verify(farmerRepository).findAllByBusinessId(BUSINESS_ID);
    verify(customerRepository).findByIdAndBusinessId(CUSTOMER_ID, BUSINESS_ID);
  }

  @Test
  void shouldReturnEmptyListWhenBusinessHasNoFarmers() {
    when(businessRepository.findById(BUSINESS_ID)).thenReturn(Optional.of(business));
    when(farmerRepository.findAllByBusinessId(BUSINESS_ID)).thenReturn(List.of());

    List<FarmerResponse> result = farmerService.getFarmers(BUSINESS_ID);

    assertTrue(result.isEmpty());
    verifyNoInteractions(customerRepository);
  }

  @Test
  void shouldRetrieveFarmerWithinOwningBusiness() {
    when(farmerRepository.findByIdAndBusinessId(FARMER_ID, BUSINESS_ID))
        .thenReturn(Optional.of(farmer));
    when(customerRepository.findByIdAndBusinessId(CUSTOMER_ID, BUSINESS_ID))
        .thenReturn(Optional.of(customer));

    FarmerResponse expected =
        new FarmerResponse(
            FARMER_ID,
            "F-ABC123",
            "Ramesh",
            "9876543210",
            "Pune",
            "Farm address",
            true,
            LocalDateTime.now());

    when(farmerMapper.toResponse(farmer, customer)).thenReturn(expected);

    FarmerResponse result = farmerService.getFarmer(BUSINESS_ID, FARMER_ID);

    assertSame(expected, result);
    verify(farmerRepository).findByIdAndBusinessId(FARMER_ID, BUSINESS_ID);
    verify(customerRepository).findByIdAndBusinessId(CUSTOMER_ID, BUSINESS_ID);
  }

  @Test
  void shouldNotRetrieveFarmerFromAnotherBusiness() {
    when(farmerRepository.findByIdAndBusinessId(FARMER_ID, BUSINESS_ID))
        .thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> farmerService.getFarmer(BUSINESS_ID, FARMER_ID));

    verifyNoInteractions(customerRepository, farmerMapper);
  }

  @Test
  void shouldUpdateCustomerAndFarmerSuccessfully() {
    UpdateFarmerRequest request =
        new UpdateFarmerRequest("Updated Ramesh", "9999999999", "Nashik", "Updated address", true);

    when(businessRepository.findByIdAndActiveTrue(BUSINESS_ID)).thenReturn(Optional.of(business));
    when(farmerRepository.findByIdAndBusinessId(FARMER_ID, BUSINESS_ID))
        .thenReturn(Optional.of(farmer));
    when(customerRepository.findByIdAndBusinessId(CUSTOMER_ID, BUSINESS_ID))
        .thenReturn(Optional.of(customer));

    FarmerResponse expected =
        new FarmerResponse(
            FARMER_ID,
            "F-ABC123",
            "Updated Ramesh",
            "9999999999",
            "Nashik",
            "Updated address",
            true,
            LocalDateTime.now());

    when(farmerMapper.toResponse(farmer, customer)).thenReturn(expected);

    FarmerResponse result = farmerService.updateFarmer(BUSINESS_ID, FARMER_ID, request);

    assertSame(expected, result);
    verify(farmerMapper).updateCustomerEntity(request, customer);
    verify(farmerMapper).updateFarmerEntity(request, farmer);
  }

  @Test
  void shouldSoftDeactivateCustomerSuccessfully() {
    when(businessRepository.findByIdAndActiveTrue(BUSINESS_ID)).thenReturn(Optional.of(business));
    when(farmerRepository.findByIdAndBusinessId(FARMER_ID, BUSINESS_ID))
        .thenReturn(Optional.of(farmer));
    when(customerRepository.findByIdAndBusinessId(CUSTOMER_ID, BUSINESS_ID))
        .thenReturn(Optional.of(customer));

    farmerService.deactivateFarmer(BUSINESS_ID, FARMER_ID);

    assertFalse(customer.isActive());
  }

  @Test
  void shouldRejectUpdateWhenBusinessIsInactive() {
    when(businessRepository.findByIdAndActiveTrue(BUSINESS_ID)).thenReturn(Optional.empty());

    UpdateFarmerRequest request =
        new UpdateFarmerRequest("Ramesh", "9876543210", "Pune", "Address", true);

    assertThrows(
        ResourceNotFoundException.class,
        () -> farmerService.updateFarmer(BUSINESS_ID, FARMER_ID, request));

    verifyNoInteractions(farmerRepository, customerRepository, farmerMapper);
  }
}
