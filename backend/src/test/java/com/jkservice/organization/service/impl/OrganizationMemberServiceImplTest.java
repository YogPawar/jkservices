
package com.jkservice.organization.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

import com.jkservice.common.exception.DuplicateResourceException;
import com.jkservice.common.exception.ResourceNotFoundException;
import com.jkservice.logging.audit.service.AuditLogService;
import com.jkservice.organization.dto.request.CreateOrganizationMemberRequest;
import com.jkservice.organization.dto.request.UpdateOrganizationMemberRequest;
import com.jkservice.organization.dto.response.OrganizationMemberResponse;
import com.jkservice.organization.entity.OrganizationMemberEntity;
import com.jkservice.organization.enums.OrganizationMembershipStatus;
import com.jkservice.organization.enums.OrganizationRole;
import com.jkservice.organization.mapper.OrganizationMemberMapper;
import com.jkservice.organization.repository.OrganizationMemberRepository;
import com.jkservice.organization.repository.OrganizationRepository;
import com.jkservice.user.entity.UserEntity;
import com.jkservice.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganizationMemberServiceImplTest {

  private static final Long ORGANIZATION_ID = 1L;
  private static final Long MEMBER_ID = 101L;
  private static final Long USER_ID = 12L;

  @Mock
  private OrganizationMemberRepository organizationMemberRepository;

  @Mock
  private OrganizationRepository organizationRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private OrganizationMemberMapper organizationMemberMapper;

  @Mock
  private AuditLogService auditLogService;

  @Mock
  private EntityManager entityManager;

  @Mock
  private Query query;

  @InjectMocks
  private OrganizationMemberServiceImpl organizationMemberService;

  private OrganizationMemberEntity member;
  private OrganizationMemberResponse response;

  @BeforeEach
  void setUp() {
    member = new OrganizationMemberEntity();
    member.setId(MEMBER_ID);
    member.setOrganizationId(ORGANIZATION_ID);
    member.setUserId(USER_ID);
    member.setRole(OrganizationRole.MEMBER);
    member.setMembershipStatus(OrganizationMembershipStatus.ACTIVE);

    response = new OrganizationMemberResponse(
            MEMBER_ID,
            ORGANIZATION_ID,
            USER_ID,
            OrganizationRole.MEMBER,
            OrganizationMembershipStatus.ACTIVE,
            null,
            null,
            null
    );
  }

  private UserEntity activeUser() {
    UserEntity user = mock(UserEntity.class);
    when(user.isActive()).thenReturn(true);
    return user;
  }

  @Test
  void addMember_shouldCreateMembership() {
    CreateOrganizationMemberRequest request =
            new CreateOrganizationMemberRequest(USER_ID, OrganizationRole.MEMBER);

    UserEntity user = activeUser();

    when(organizationRepository.existsById(ORGANIZATION_ID)).thenReturn(true);
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(organizationMemberRepository.existsByOrganizationIdAndUserId(
            ORGANIZATION_ID, USER_ID)).thenReturn(false);

    when(organizationMemberRepository.save(any(OrganizationMemberEntity.class)))
            .thenAnswer(invocation -> {
              OrganizationMemberEntity saved = invocation.getArgument(0);
              saved.setId(MEMBER_ID);
              return saved;
            });

    when(organizationMemberMapper.toResponse(any(OrganizationMemberEntity.class)))
            .thenReturn(response);

    OrganizationMemberResponse result =
            organizationMemberService.addMember(ORGANIZATION_ID, request);

    assertThat(result).isEqualTo(response);

    ArgumentCaptor<OrganizationMemberEntity> captor =
            ArgumentCaptor.forClass(OrganizationMemberEntity.class);

    verify(organizationMemberRepository).save(captor.capture());

    assertThat(captor.getValue().getOrganizationId()).isEqualTo(ORGANIZATION_ID);
    assertThat(captor.getValue().getUserId()).isEqualTo(USER_ID);
    assertThat(captor.getValue().getRole()).isEqualTo(OrganizationRole.MEMBER);
    assertThat(captor.getValue().getMembershipStatus())
            .isEqualTo(OrganizationMembershipStatus.ACTIVE);
    assertThat(captor.getValue().getJoinedAt()).isNotNull();

    verify(auditLogService).record(
            eq(ORGANIZATION_ID),
            isNull(),
            isNull(),
            eq("ORGANIZATION_MEMBER_ADDED"),
            eq("ORGANIZATION_MEMBER"),
            anyString(),
            anyMap()
    );
  }

  @Test
  void addMember_whenMembershipAlreadyExists_shouldThrowException() {
    CreateOrganizationMemberRequest request =
            new CreateOrganizationMemberRequest(USER_ID, OrganizationRole.MEMBER);

    UserEntity user = activeUser();

    when(organizationRepository.existsById(ORGANIZATION_ID)).thenReturn(true);
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(organizationMemberRepository.existsByOrganizationIdAndUserId(
            ORGANIZATION_ID, USER_ID)).thenReturn(true);

    assertThatThrownBy(() ->
            organizationMemberService.addMember(ORGANIZATION_ID, request))
            .isInstanceOf(DuplicateResourceException.class);

    verify(organizationMemberRepository, never()).save(any());

    verify(auditLogService, never()).record(
            any(), any(), any(), any(), any(), any(), any()
    );
  }

  @Test
  void addMember_whenUserIsInactive_shouldThrowException() {
    CreateOrganizationMemberRequest request =
            new CreateOrganizationMemberRequest(USER_ID, OrganizationRole.MEMBER);

    UserEntity user = mock(UserEntity.class);
    when(user.isActive()).thenReturn(false);

    when(organizationRepository.existsById(ORGANIZATION_ID)).thenReturn(true);
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

    assertThatThrownBy(() ->
            organizationMemberService.addMember(ORGANIZATION_ID, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("inactive user");

    verify(organizationMemberRepository, never()).save(any());
  }

  @Test
  void getMembers_shouldReturnOrganizationMembers() {
    when(organizationRepository.existsById(ORGANIZATION_ID)).thenReturn(true);

    when(organizationMemberRepository.findAllByOrganizationIdOrderByIdAsc(
            ORGANIZATION_ID)).thenReturn(List.of(member));

    when(organizationMemberMapper.toResponse(member)).thenReturn(response);

    List<OrganizationMemberResponse> result =
            organizationMemberService.getMembers(ORGANIZATION_ID);

    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(response);
  }

  @Test
  void getMember_shouldReturnMember() {
    when(organizationRepository.existsById(ORGANIZATION_ID)).thenReturn(true);

    when(organizationMemberRepository.findByIdAndOrganizationId(
            MEMBER_ID, ORGANIZATION_ID)).thenReturn(Optional.of(member));

    when(organizationMemberMapper.toResponse(member)).thenReturn(response);

    OrganizationMemberResponse result =
            organizationMemberService.getMember(ORGANIZATION_ID, MEMBER_ID);

    assertThat(result).isEqualTo(response);
  }

  @Test
  void getMember_whenMemberBelongsToAnotherOrganization_shouldThrowException() {
    when(organizationRepository.existsById(ORGANIZATION_ID)).thenReturn(true);

    when(organizationMemberRepository.findByIdAndOrganizationId(
            MEMBER_ID, ORGANIZATION_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            organizationMemberService.getMember(ORGANIZATION_ID, MEMBER_ID))
            .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void updateMember_shouldUpdateRoleAndStatus() {
    UpdateOrganizationMemberRequest request =
            new UpdateOrganizationMemberRequest(
                    OrganizationRole.ADMIN,
                    OrganizationMembershipStatus.ACTIVE
            );

    when(organizationRepository.existsById(ORGANIZATION_ID)).thenReturn(true);

    when(organizationMemberRepository.findByIdAndOrganizationId(
            MEMBER_ID, ORGANIZATION_ID)).thenReturn(Optional.of(member));

    when(organizationMemberRepository.save(member)).thenReturn(member);
    when(organizationMemberMapper.toResponse(member)).thenReturn(response);

    OrganizationMemberResponse result =
            organizationMemberService.updateMember(
                    ORGANIZATION_ID, MEMBER_ID, request);

    assertThat(result).isEqualTo(response);
    assertThat(member.getRole()).isEqualTo(OrganizationRole.ADMIN);
    assertThat(member.getMembershipStatus())
            .isEqualTo(OrganizationMembershipStatus.ACTIVE);

    verify(organizationMemberRepository).save(member);

    verify(auditLogService).record(
            eq(ORGANIZATION_ID),
            isNull(),
            isNull(),
            anyString(),
            eq("ORGANIZATION_MEMBER"),
            eq(String.valueOf(MEMBER_ID)),
            anyMap()
    );
  }

  @Test
  void updateMember_whenDemotingLastActiveOwner_shouldThrowException() {
    member.setRole(OrganizationRole.OWNER);
    member.setMembershipStatus(OrganizationMembershipStatus.ACTIVE);

    UpdateOrganizationMemberRequest request =
            new UpdateOrganizationMemberRequest(
                    OrganizationRole.MEMBER,
                    OrganizationMembershipStatus.ACTIVE
            );

    when(organizationRepository.existsById(ORGANIZATION_ID)).thenReturn(true);

    when(organizationMemberRepository.findByIdAndOrganizationId(
            MEMBER_ID, ORGANIZATION_ID)).thenReturn(Optional.of(member));

    when(organizationMemberRepository.countByOrganizationIdAndRoleAndMembershipStatus(
            ORGANIZATION_ID,
            OrganizationRole.OWNER,
            OrganizationMembershipStatus.ACTIVE)).thenReturn(1L);

    assertThatThrownBy(() ->
            organizationMemberService.updateMember(
                    ORGANIZATION_ID, MEMBER_ID, request))
            .isInstanceOf(IllegalArgumentException.class);

    verify(organizationMemberRepository, never()).save(any());
  }

  @Test
  void removeMember_shouldDeleteBusinessAssignmentsAndMembership() {
    when(organizationRepository.existsById(ORGANIZATION_ID)).thenReturn(true);

    when(organizationMemberRepository.findByIdAndOrganizationId(
            MEMBER_ID, ORGANIZATION_ID)).thenReturn(Optional.of(member));

    when(entityManager.createNativeQuery(anyString())).thenReturn(query);

    when(query.setParameter(eq("memberId"), eq(MEMBER_ID))).thenReturn(query);

    when(query.setParameter(eq("organizationId"), eq(ORGANIZATION_ID)))
            .thenReturn(query);

    when(query.executeUpdate()).thenReturn(1);

    organizationMemberService.removeMember(ORGANIZATION_ID, MEMBER_ID);

    verify(entityManager).createNativeQuery(contains("DELETE FROM business_members"));
    verify(query).executeUpdate();
    verify(organizationMemberRepository).delete(member);

    verify(auditLogService).record(
            eq(ORGANIZATION_ID),
            isNull(),
            isNull(),
            anyString(),
            eq("ORGANIZATION_MEMBER"),
            eq(String.valueOf(MEMBER_ID)),
            anyMap()
    );
  }

  @Test
  void removeMember_whenRemovingLastActiveOwner_shouldThrowException() {
    member.setRole(OrganizationRole.OWNER);
    member.setMembershipStatus(OrganizationMembershipStatus.ACTIVE);

    when(organizationRepository.existsById(ORGANIZATION_ID)).thenReturn(true);

    when(organizationMemberRepository.findByIdAndOrganizationId(
            MEMBER_ID, ORGANIZATION_ID)).thenReturn(Optional.of(member));

    when(organizationMemberRepository.countByOrganizationIdAndRoleAndMembershipStatus(
            ORGANIZATION_ID,
            OrganizationRole.OWNER,
            OrganizationMembershipStatus.ACTIVE)).thenReturn(1L);

    assertThatThrownBy(() ->
            organizationMemberService.removeMember(ORGANIZATION_ID, MEMBER_ID))
            .isInstanceOf(IllegalArgumentException.class);

    verify(organizationMemberRepository, never()).delete(any());
    verify(entityManager, never()).createNativeQuery(anyString());
  }
}
