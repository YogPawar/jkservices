package com.jkservice.business.service;

import com.jkservice.business.dto.request.CreateBusinessRequest;
import com.jkservice.business.dto.request.UpdateBusinessRequest;
import com.jkservice.business.dto.response.BusinessResponse;
import java.util.List;

public interface BusinessService {

  BusinessResponse createBusiness(Long organizationId, CreateBusinessRequest request);

  List<BusinessResponse> getBusinesses(Long organizationId);

  BusinessResponse getBusiness(Long organizationId, Long businessId);

  BusinessResponse updateBusiness(
      Long organizationId, Long businessId, UpdateBusinessRequest request);

  void deactivateBusiness(Long organizationId, Long businessId);
}
