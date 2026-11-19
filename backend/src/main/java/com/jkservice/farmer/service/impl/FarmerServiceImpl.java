package com.jkservice.farmer.service.impl;

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
import com.jkservice.farmer.service.FarmerCodeGenerator;
import com.jkservice.farmer.service.FarmerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FarmerServiceImpl implements FarmerService {

  private static final String FARMING_SERVICES = "FARMING_SERVICES";

  private final BusinessRepository businessRepository;
  private final CustomerRepository customerRepository;
  private final FarmerRepository farmerRepository;
  private final FarmerMapper farmerMapper;
  private final CustomerCodeGenerator customerCodeGenerator;
  private final FarmerCodeGenerator farmerCodeGenerator;

  @Override
  public FarmerResponse createFarmer(Long businessId, CreateFarmerRequest request) {
    getActiveFarmingBusiness(businessId);

    String customerCode = customerCodeGenerator.generate();
    String farmerCode = farmerCodeGenerator.generate();

    CustomerEntity customer = farmerMapper.toCustomerEntity(request, businessId, customerCode);

    CustomerEntity savedCustomer = customerRepository.save(customer);

    FarmerEntity farmer =
        farmerMapper.toFarmerEntity(request, businessId, savedCustomer.getId(), farmerCode);

    FarmerEntity savedFarmer = farmerRepository.save(farmer);

    return farmerMapper.toResponse(savedFarmer, savedCustomer);
  }

  @Override
  @Transactional(readOnly = true)
  public List<FarmerResponse> getFarmers(Long businessId) {
    getFarmingBusiness(businessId);

    return farmerRepository.findAllByBusinessId(businessId).stream()
        .map(farmer -> {
          CustomerEntity customer = getCustomer(businessId, farmer.getCustomerId());
          return farmerMapper.toResponse(farmer, customer);
        })
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public FarmerResponse getFarmer(Long businessId, Long farmerId) {
    FarmerEntity farmer = getFarmerEntity(businessId, farmerId);
    CustomerEntity customer = getCustomer(businessId, farmer.getCustomerId());

    return farmerMapper.toResponse(farmer, customer);
  }

  @Override
  public FarmerResponse updateFarmer(Long businessId, Long farmerId, UpdateFarmerRequest request) {

    getActiveFarmingBusiness(businessId);

    FarmerEntity farmer = getFarmerEntity(businessId, farmerId);
    CustomerEntity customer = getCustomer(businessId, farmer.getCustomerId());

    farmerMapper.updateCustomerEntity(request, customer);
    farmerMapper.updateFarmerEntity(request, farmer);

    return farmerMapper.toResponse(farmer, customer);
  }

  @Override
  public void deactivateFarmer(Long businessId, Long farmerId) {
    getActiveFarmingBusiness(businessId);

    FarmerEntity farmer = getFarmerEntity(businessId, farmerId);
    CustomerEntity customer = getCustomer(businessId, farmer.getCustomerId());

    // Idempotent soft deactivation of the customer record.
    customer.setActive(false);
  }

  private BusinessEntity getFarmingBusiness(Long businessId) {
    BusinessEntity business =
        businessRepository
            .findById(businessId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Business not found with ID: " + businessId));

    if (!FARMING_SERVICES.equals(business.getBusinessType())) {
      throw new BusinessException("Farmer operations are only supported for farming businesses");
    }

    return business;
  }

  private BusinessEntity getActiveFarmingBusiness(Long businessId) {
    BusinessEntity business =
        businessRepository
            .findByIdAndActiveTrue(businessId)
            .orElseThrow(() ->
                new ResourceNotFoundException("Active business not found with ID: " + businessId));

    if (!FARMING_SERVICES.equals(business.getBusinessType())) {
      throw new BusinessException("Farmer operations are only supported for farming businesses");
    }

    return business;
  }

  private FarmerEntity getFarmerEntity(Long businessId, Long farmerId) {
    return farmerRepository
        .findByIdAndBusinessId(farmerId, businessId)
        .orElseThrow(() ->
            new ResourceNotFoundException(
                "Farmer not found with ID: " + farmerId + " in business: " + businessId));
  }

  private CustomerEntity getCustomer(Long businessId, Long customerId) {
    return customerRepository
        .findByIdAndBusinessId(customerId, businessId)
        .orElseThrow(() ->
            new ResourceNotFoundException(
                "Customer not found with ID: " + customerId + " in business: " + businessId));
  }
}
