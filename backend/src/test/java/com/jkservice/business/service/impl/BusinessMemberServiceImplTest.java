package com.jkservice.business.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.jkservice.business.dto.request.AssignBusinessMemberRequest;
import com.jkservice.business.dto.request.UpdateBusinessMemberRequest;
import com.jkservice.business.dto.response.BusinessMemberResponse;
import com.jkservice.business.entity.BusinessMemberEntity;
import com.jkservice.business.enums.BusinessMemberRole;
import com.jkservice.business.enums.BusinessMembershipStatus;
import com.jkservice.business.mapper.BusinessMemberMapper;
import com.jkservice.business.repository.BusinessMemberRepository;
import com.jkservice.business.repository.BusinessRepository;
import com.jkservice.common.exception.DuplicateResourceException;
import com.jkservice.common.exception.ResourceNotFoundException;
import com.jkservice.logging.audit.service.AuditLogService;
import com.jkservice.organization.entity.OrganizationMemberEntity;
import com.jkservice.organization.enums.OrganizationMembershipStatus;
import com.jkservice.organization.repository.OrganizationMemberRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BusinessMemberServiceImplTest {

  private static final Long ORGANIZATION_ID = 1L;
  private static final Long BUSINESS_ID = 10L;
  private static final Long ORGANIZATION_MEMBER_ID = 20L;
  private static final Long BUSINESS_MEMBER_ID = 30L;

  @Mock private BusinessMemberRepository businessMemberRepository;

  @Mock private BusinessRepository businessRepository;

  @Mock private OrganizationMemberRepository organizationMemberRepository;

  @Mock private BusinessMemberMapper businessMemberMapper;

  @Mock private AuditLogService auditLogService;

  @InjectMocks private BusinessMemberServiceImpl businessMemberService;

  private OrganizationMemberEntity organizationMember;
  private BusinessMemberEntity businessMember;
  private BusinessMemberResponse response;

  @BeforeEach
  void setUp() {
    organizationMember = new OrganizationMemberEntity();
    organizationMember.setId(ORGANIZATION_MEMBER_ID);
    organizationMember.setOrganizationId(ORGANIZATION_ID);

    businessMember = createBusinessMember();

    response =
        new BusinessMemberResponse(
            BUSINESS_MEMBER_ID,
            ORGANIZATION_ID,
            BUSINESS_ID,
            ORGANIZATION_MEMBER_ID,
            BusinessMemberRole.MANAGER,
            BusinessMembershipStatus.ACTIVE,
            null,
            null);
  }

  @Test
  void assignMember_shouldAssignActiveOrganizationMember() {
    AssignBusinessMemberRequest request =
        new AssignBusinessMemberRequest(ORGANIZATION_MEMBER_ID, BusinessMemberRole.MANAGER);

    when(businessRepository.findByIdAndOrganizationId(BUSINESS_ID, ORGANIZATION_ID))
        .thenReturn(Optional.of(mock(com.jkservice.business.entity.BusinessEntity.class)));

    when(organizationMemberRepository.findByIdAndOrganizationIdAndMembershipStatus(
            ORGANIZATION_MEMBER_ID, ORGANIZATION_ID, OrganizationMembershipStatus.ACTIVE))
        .thenReturn(Optional.of(organizationMember));

    when(businessMemberRepository.existsByOrganizationIdAndBusinessIdAndOrganizationMemberId(
            ORGANIZATION_ID, BUSINESS_ID, ORGANIZATION_MEMBER_ID))
        .thenReturn(false);

    when(businessMemberRepository.save(any(BusinessMemberEntity.class)))
        .thenAnswer(invocation -> {
          BusinessMemberEntity entity = invocation.getArgument(0);
          entity.setId(BUSINESS_MEMBER_ID);
          return entity;
        });

    when(businessMemberMapper.toResponse(any(BusinessMemberEntity.class))).thenReturn(response);

    BusinessMemberResponse result =
        businessMemberService.assignMember(ORGANIZATION_ID, BUSINESS_ID, request);

    assertNotNull(result);
    assertEquals(BUSINESS_MEMBER_ID, result.id());
    assertEquals(BusinessMemberRole.MANAGER, result.role());

    verify(businessMemberRepository)
        .save(
            argThat(entity ->
                entity.getOrganizationId().equals(ORGANIZATION_ID)
                    && entity.getBusinessId().equals(BUSINESS_ID)
                    && entity.getOrganizationMemberId().equals(ORGANIZATION_MEMBER_ID)
                    && entity.getRole().equals("MANAGER")
                    && entity.getMembershipStatus().equals("ACTIVE")));

    verify(auditLogService)
        .record(
            eq(ORGANIZATION_ID),
            eq(BUSINESS_ID),
            isNull(),
            eq("BUSINESS_MEMBER_ASSIGNED"),
            eq("BUSINESS_MEMBER"),
            eq(BUSINESS_MEMBER_ID.toString()),
            contains("MANAGER"));
  }

  @Test
  void assignMember_shouldRejectUnknownBusiness() {
    AssignBusinessMemberRequest request =
        new AssignBusinessMemberRequest(ORGANIZATION_MEMBER_ID, BusinessMemberRole.MANAGER);

    when(businessRepository.findByIdAndOrganizationId(BUSINESS_ID, ORGANIZATION_ID))
        .thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> businessMemberService.assignMember(ORGANIZATION_ID, BUSINESS_ID, request));

    verifyNoInteractions(organizationMemberRepository);
    verify(businessMemberRepository, never()).save(any());
  }

  @Test
  void assignMember_shouldRejectInactiveOrMissingOrganizationMember() {
    AssignBusinessMemberRequest request =
        new AssignBusinessMemberRequest(ORGANIZATION_MEMBER_ID, BusinessMemberRole.MANAGER);

    when(businessRepository.findByIdAndOrganizationId(BUSINESS_ID, ORGANIZATION_ID))
        .thenReturn(Optional.of(mock(com.jkservice.business.entity.BusinessEntity.class)));

    when(organizationMemberRepository.findByIdAndOrganizationIdAndMembershipStatus(
            ORGANIZATION_MEMBER_ID, ORGANIZATION_ID, OrganizationMembershipStatus.ACTIVE))
        .thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> businessMemberService.assignMember(ORGANIZATION_ID, BUSINESS_ID, request));

    verify(businessMemberRepository, never()).save(any());
  }

  @Test
  void assignMember_shouldRejectDuplicateAssignment() {
    AssignBusinessMemberRequest request =
        new AssignBusinessMemberRequest(ORGANIZATION_MEMBER_ID, BusinessMemberRole.MANAGER);

    when(businessRepository.findByIdAndOrganizationId(BUSINESS_ID, ORGANIZATION_ID))
        .thenReturn(Optional.of(mock(com.jkservice.business.entity.BusinessEntity.class)));

    when(organizationMemberRepository.findByIdAndOrganizationIdAndMembershipStatus(
            ORGANIZATION_MEMBER_ID, ORGANIZATION_ID, OrganizationMembershipStatus.ACTIVE))
        .thenReturn(Optional.of(organizationMember));

    when(businessMemberRepository.existsByOrganizationIdAndBusinessIdAndOrganizationMemberId(
            ORGANIZATION_ID, BUSINESS_ID, ORGANIZATION_MEMBER_ID))
        .thenReturn(true);

    assertThrows(
        DuplicateResourceException.class,
        () -> businessMemberService.assignMember(ORGANIZATION_ID, BUSINESS_ID, request));

    verify(businessMemberRepository, never()).save(any());
  }

  @Test
  void getMembers_shouldReturnBusinessMembers() {
    when(businessRepository.findByIdAndOrganizationId(BUSINESS_ID, ORGANIZATION_ID))
        .thenReturn(Optional.of(mock(com.jkservice.business.entity.BusinessEntity.class)));

    when(businessMemberRepository.findAllByOrganizationIdAndBusinessIdOrderByIdAsc(
            ORGANIZATION_ID, BUSINESS_ID))
        .thenReturn(List.of(businessMember));

    when(businessMemberMapper.toResponse(businessMember)).thenReturn(response);

    List<BusinessMemberResponse> result =
        businessMemberService.getMembers(ORGANIZATION_ID, BUSINESS_ID);

    assertEquals(1, result.size());
    assertEquals(BUSINESS_MEMBER_ID, result.get(0).id());
  }

  @Test
  void getMembers_shouldReturnEmptyListWhenNoMembersExist() {
    when(businessRepository.findByIdAndOrganizationId(BUSINESS_ID, ORGANIZATION_ID))
        .thenReturn(Optional.of(mock(com.jkservice.business.entity.BusinessEntity.class)));

    when(businessMemberRepository.findAllByOrganizationIdAndBusinessIdOrderByIdAsc(
            ORGANIZATION_ID, BUSINESS_ID))
        .thenReturn(List.of());

    List<BusinessMemberResponse> result =
        businessMemberService.getMembers(ORGANIZATION_ID, BUSINESS_ID);

    assertTrue(result.isEmpty());
  }

  @Test
  void getMember_shouldReturnMember() {
    when(businessRepository.findByIdAndOrganizationId(BUSINESS_ID, ORGANIZATION_ID))
        .thenReturn(Optional.of(mock(com.jkservice.business.entity.BusinessEntity.class)));

    when(businessMemberRepository.findByIdAndOrganizationIdAndBusinessId(
            BUSINESS_MEMBER_ID, ORGANIZATION_ID, BUSINESS_ID))
        .thenReturn(Optional.of(businessMember));

    when(businessMemberMapper.toResponse(businessMember)).thenReturn(response);

    BusinessMemberResponse result =
        businessMemberService.getMember(ORGANIZATION_ID, BUSINESS_ID, BUSINESS_MEMBER_ID);

    assertEquals(BUSINESS_MEMBER_ID, result.id());
  }

  @Test
  void getMember_shouldRejectMemberFromAnotherBusinessOrOrganization() {
    when(businessRepository.findByIdAndOrganizationId(BUSINESS_ID, ORGANIZATION_ID))
        .thenReturn(Optional.of(mock(com.jkservice.business.entity.BusinessEntity.class)));

    when(businessMemberRepository.findByIdAndOrganizationIdAndBusinessId(
            BUSINESS_MEMBER_ID, ORGANIZATION_ID, BUSINESS_ID))
        .thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> businessMemberService.getMember(ORGANIZATION_ID, BUSINESS_ID, BUSINESS_MEMBER_ID));
  }

  @Test
  void updateMember_shouldUpdateRoleAndStatus() {
    UpdateBusinessMemberRequest request =
        new UpdateBusinessMemberRequest(
            BusinessMemberRole.OPERATOR, BusinessMembershipStatus.SUSPENDED);

    when(businessRepository.findByIdAndOrganizationId(BUSINESS_ID, ORGANIZATION_ID))
        .thenReturn(Optional.of(mock(com.jkservice.business.entity.BusinessEntity.class)));

    when(businessMemberRepository.findByIdAndOrganizationIdAndBusinessId(
            BUSINESS_MEMBER_ID, ORGANIZATION_ID, BUSINESS_ID))
        .thenReturn(Optional.of(businessMember));

    when(businessMemberRepository.save(businessMember)).thenReturn(businessMember);

    BusinessMemberResponse updatedResponse =
        new BusinessMemberResponse(
            BUSINESS_MEMBER_ID,
            ORGANIZATION_ID,
            BUSINESS_ID,
            ORGANIZATION_MEMBER_ID,
            BusinessMemberRole.OPERATOR,
            BusinessMembershipStatus.SUSPENDED,
            null,
            null);

    when(businessMemberMapper.toResponse(businessMember)).thenReturn(updatedResponse);

    BusinessMemberResponse result =
        businessMemberService.updateMember(
            ORGANIZATION_ID, BUSINESS_ID, BUSINESS_MEMBER_ID, request);

    assertEquals(BusinessMemberRole.OPERATOR, result.role());
    assertEquals(BusinessMembershipStatus.SUSPENDED, result.membershipStatus());

    verify(auditLogService)
        .record(
            eq(ORGANIZATION_ID),
            eq(BUSINESS_ID),
            isNull(),
            eq("BUSINESS_MEMBER_UPDATED"),
            eq("BUSINESS_MEMBER"),
            eq(BUSINESS_MEMBER_ID.toString()),
            contains("OPERATOR"));
  }

  @Test
  void updateMember_shouldRejectUnknownMember() {
    UpdateBusinessMemberRequest request =
        new UpdateBusinessMemberRequest(
            BusinessMemberRole.OPERATOR, BusinessMembershipStatus.ACTIVE);

    when(businessRepository.findByIdAndOrganizationId(BUSINESS_ID, ORGANIZATION_ID))
        .thenReturn(Optional.of(mock(com.jkservice.business.entity.BusinessEntity.class)));

    when(businessMemberRepository.findByIdAndOrganizationIdAndBusinessId(
            BUSINESS_MEMBER_ID, ORGANIZATION_ID, BUSINESS_ID))
        .thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () ->
            businessMemberService.updateMember(
                ORGANIZATION_ID, BUSINESS_ID, BUSINESS_MEMBER_ID, request));

    verify(businessMemberRepository, never()).save(any());
  }

  @Test
  void unassignMember_shouldDeleteOnlyBusinessMembership() {
    when(businessRepository.findByIdAndOrganizationId(BUSINESS_ID, ORGANIZATION_ID))
        .thenReturn(Optional.of(mock(com.jkservice.business.entity.BusinessEntity.class)));

    when(businessMemberRepository.findByIdAndOrganizationIdAndBusinessId(
            BUSINESS_MEMBER_ID, ORGANIZATION_ID, BUSINESS_ID))
        .thenReturn(Optional.of(businessMember));

    businessMemberService.unassignMember(ORGANIZATION_ID, BUSINESS_ID, BUSINESS_MEMBER_ID);

    verify(businessMemberRepository).delete(businessMember);

    verify(auditLogService)
        .record(
            eq(ORGANIZATION_ID),
            eq(BUSINESS_ID),
            isNull(),
            eq("BUSINESS_MEMBER_UNASSIGNED"),
            eq("BUSINESS_MEMBER"),
            eq(BUSINESS_MEMBER_ID.toString()),
            contains(ORGANIZATION_MEMBER_ID.toString()));

    verifyNoInteractions(organizationMemberRepository);
  }

  @Test
  void unassignMember_shouldRejectUnknownMember() {
    when(businessRepository.findByIdAndOrganizationId(BUSINESS_ID, ORGANIZATION_ID))
        .thenReturn(Optional.of(mock(com.jkservice.business.entity.BusinessEntity.class)));

    when(businessMemberRepository.findByIdAndOrganizationIdAndBusinessId(
            BUSINESS_MEMBER_ID, ORGANIZATION_ID, BUSINESS_ID))
        .thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () ->
            businessMemberService.unassignMember(ORGANIZATION_ID, BUSINESS_ID, BUSINESS_MEMBER_ID));

    verify(businessMemberRepository, never()).delete(any());
  }

  private BusinessMemberEntity createBusinessMember() {
    BusinessMemberEntity entity = new BusinessMemberEntity();
    entity.setId(BUSINESS_MEMBER_ID);
    entity.setOrganizationId(ORGANIZATION_ID);
    entity.setBusinessId(BUSINESS_ID);
    entity.setOrganizationMemberId(ORGANIZATION_MEMBER_ID);
    entity.setRole("MANAGER");
    entity.setMembershipStatus("ACTIVE");
    return entity;
  }
}
