package com.jkservice.business.service.impl;

import com.jkservice.business.dto.request.AssignBusinessMemberRequest;
import com.jkservice.business.dto.request.UpdateBusinessMemberRequest;
import com.jkservice.business.dto.response.BusinessMemberResponse;
import com.jkservice.business.entity.BusinessMemberEntity;
import com.jkservice.business.mapper.BusinessMemberMapper;
import com.jkservice.business.repository.BusinessMemberRepository;
import com.jkservice.business.repository.BusinessRepository;
import com.jkservice.business.service.BusinessMemberService;
import com.jkservice.common.exception.DuplicateResourceException;
import com.jkservice.common.exception.ResourceNotFoundException;
import com.jkservice.logging.audit.service.AuditLogService;
import com.jkservice.organization.entity.OrganizationMemberEntity;
import com.jkservice.organization.enums.OrganizationMembershipStatus;
import com.jkservice.organization.repository.OrganizationMemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BusinessMemberServiceImpl implements BusinessMemberService {

  private static final String ENTITY_TYPE = "BUSINESS_MEMBER";

  private final BusinessMemberRepository businessMemberRepository;
  private final BusinessRepository businessRepository;
  private final OrganizationMemberRepository organizationMemberRepository;
  private final BusinessMemberMapper businessMemberMapper;
  private final AuditLogService auditLogService;

  @Override
  public BusinessMemberResponse assignMember(
      Long organizationId, Long businessId, AssignBusinessMemberRequest request) {
    validateBusiness(organizationId, businessId);

    OrganizationMemberEntity organizationMember =
        organizationMemberRepository
            .findByIdAndOrganizationIdAndMembershipStatus(
                request.organizationMemberId(), organizationId, OrganizationMembershipStatus.ACTIVE)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Active organization member not found with ID: "
                        + request.organizationMemberId()));

    if (businessMemberRepository.existsByOrganizationIdAndBusinessIdAndOrganizationMemberId(
        organizationId, businessId, organizationMember.getId())) {
      throw new DuplicateResourceException(
          "Organization member is already assigned to this business");
    }

    BusinessMemberEntity entity = new BusinessMemberEntity();
    entity.setOrganizationId(organizationId);
    entity.setBusinessId(businessId);
    entity.setOrganizationMemberId(organizationMember.getId());
    entity.setRole(request.role().name());
    entity.setMembershipStatus("ACTIVE");

    BusinessMemberEntity saved = businessMemberRepository.save(entity);

    auditLogService.record(
        organizationId,
        businessId,
        null,
        "BUSINESS_MEMBER_ASSIGNED",
        ENTITY_TYPE,
        saved.getId().toString(),
        "Assigned organization member "
            + organizationMember.getId()
            + " with role "
            + request.role());

    log.info(
        "Business member assigned: organizationId={}, businessId={}, "
            + "businessMemberId={}, organizationMemberId={}, role={}",
        organizationId,
        businessId,
        saved.getId(),
        organizationMember.getId(),
        request.role());

    return businessMemberMapper.toResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<BusinessMemberResponse> getMembers(Long organizationId, Long businessId) {
    validateBusiness(organizationId, businessId);

    return businessMemberRepository
        .findAllByOrganizationIdAndBusinessIdOrderByIdAsc(organizationId, businessId)
        .stream()
        .map(businessMemberMapper::toResponse)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public BusinessMemberResponse getMember(Long organizationId, Long businessId, Long memberId) {
    validateBusiness(organizationId, businessId);

    return businessMemberMapper.toResponse(
        findBusinessMember(organizationId, businessId, memberId));
  }

  @Override
  public BusinessMemberResponse updateMember(
      Long organizationId, Long businessId, Long memberId, UpdateBusinessMemberRequest request) {
    validateBusiness(organizationId, businessId);

    BusinessMemberEntity entity = findBusinessMember(organizationId, businessId, memberId);

    String previousRole = entity.getRole();
    String previousStatus = entity.getMembershipStatus();

    entity.setRole(request.role().name());
    entity.setMembershipStatus(request.membershipStatus().name());

    BusinessMemberEntity updated = businessMemberRepository.save(entity);

    auditLogService.record(
        organizationId,
        businessId,
        null,
        "BUSINESS_MEMBER_UPDATED",
        ENTITY_TYPE,
        updated.getId().toString(),
        "Updated business member. Previous role/status: "
            + previousRole
            + "/"
            + previousStatus
            + ", new role/status: "
            + request.role()
            + "/"
            + request.membershipStatus());

    log.info(
        "Business member updated: organizationId={}, businessId={}, "
            + "businessMemberId={}, role={}, status={}",
        organizationId,
        businessId,
        memberId,
        request.role(),
        request.membershipStatus());

    return businessMemberMapper.toResponse(updated);
  }

  @Override
  public void unassignMember(Long organizationId, Long businessId, Long memberId) {
    validateBusiness(organizationId, businessId);

    BusinessMemberEntity entity = findBusinessMember(organizationId, businessId, memberId);

    businessMemberRepository.delete(entity);

    auditLogService.record(
        organizationId,
        businessId,
        null,
        "BUSINESS_MEMBER_UNASSIGNED",
        ENTITY_TYPE,
        entity.getId().toString(),
        "Unassigned organization member " + entity.getOrganizationMemberId() + " from business");

    log.info(
        "Business member unassigned: organizationId={}, businessId={}, "
            + "businessMemberId={}, organizationMemberId={}",
        organizationId,
        businessId,
        memberId,
        entity.getOrganizationMemberId());
  }

  private void validateBusiness(Long organizationId, Long businessId) {
    boolean exists =
        businessRepository.findByIdAndOrganizationId(businessId, organizationId).isPresent();

    if (!exists) {
      throw new ResourceNotFoundException(
          "Business not found with ID: " + businessId + " in organization: " + organizationId);
    }
  }

  private BusinessMemberEntity findBusinessMember(
      Long organizationId, Long businessId, Long memberId) {
    return businessMemberRepository
        .findByIdAndOrganizationIdAndBusinessId(memberId, organizationId, businessId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Business member not found with ID: " + memberId));
  }
}
