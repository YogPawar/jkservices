
package com.jkservice.organization.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.jkservice.user.entity.UserEntity;
import com.jkservice.user.repository.UserRepository;
import com.jkservice.common.exception.DuplicateResourceException;
import com.jkservice.common.exception.ResourceNotFoundException;
import com.jkservice.logging.audit.service.AuditLogService;
import com.jkservice.organization.dto.request.CreateOrganizationRequest;
import com.jkservice.organization.dto.request.UpdateOrganizationRequest;
import com.jkservice.organization.dto.response.OrganizationResponse;
import com.jkservice.organization.entity.OrganizationEntity;
import com.jkservice.organization.entity.OrganizationMemberEntity;
import com.jkservice.organization.enums.OrganizationMembershipStatus;
import com.jkservice.organization.enums.OrganizationRole;
import com.jkservice.organization.mapper.OrganizationMapper;
import com.jkservice.organization.repository.OrganizationMemberRepository;
import com.jkservice.organization.repository.OrganizationRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceImplTest {

  @Mock private OrganizationRepository organizationRepository;
  @Mock private OrganizationMemberRepository organizationMemberRepository;
  @Mock private UserRepository userRepository;
  @Mock private OrganizationMapper organizationMapper;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private AuditLogService auditLogService;

  @InjectMocks private OrganizationServiceImpl organizationService;

  private OrganizationEntity organization;
  private OrganizationResponse organizationResponse;

  @BeforeEach
  void setUp() {
    organization = new OrganizationEntity();
    organization.setId(1L);
    organization.setOrganizationName("JK Services");
    organization.setInviteCode("ABC123");
    organization.setActive(true);

    organizationResponse =
            new OrganizationResponse(
                    1L, "JK Services", "ABC123", true, Instant.now(), Instant.now());
  }

  @Test
  void createOrganization_shouldCreateUserOrganizationAndOwnerMembership() {
    CreateOrganizationRequest request =
            new CreateOrganizationRequest(
                    " JK Services ", " Yogesh Pawar ", " YOGESH@EXAMPLE.COM ", "password123");

    when(userRepository.existsByEmail("yogesh@example.com")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

    when(userRepository.save(any(UserEntity.class)))
            .thenAnswer(
                    invocation -> {
                      UserEntity user = invocation.getArgument(0);
                      user.setId(10L);
                      return user;
                    });

    when(organizationRepository.existsByInviteCode(anyString())).thenReturn(false);
    when(organizationRepository.save(any(OrganizationEntity.class)))
            .thenAnswer(
                    invocation -> {
                      OrganizationEntity saved = invocation.getArgument(0);
                      saved.setId(1L);
                      return saved;
                    });

    when(organizationMapper.toResponse(any(OrganizationEntity.class)))
            .thenReturn(organizationResponse);

    OrganizationResponse result = organizationService.createOrganization(request);

    assertEquals(organizationResponse, result);

    ArgumentCaptor<UserEntity> userCaptor =
            ArgumentCaptor.forClass(UserEntity.class);
    verify(userRepository).save(userCaptor.capture());

    UserEntity savedUser = userCaptor.getValue();
    assertEquals("Yogesh Pawar", savedUser.getFullName());
    assertEquals("yogesh@example.com", savedUser.getEmail());
    assertEquals("encoded-password", savedUser.getPasswordHash());
    assertTrue(savedUser.isActive());

    verify(passwordEncoder).encode("password123");

    ArgumentCaptor<OrganizationEntity> organizationCaptor =
            ArgumentCaptor.forClass(OrganizationEntity.class);
    verify(organizationRepository).save(organizationCaptor.capture());

    OrganizationEntity savedOrganization = organizationCaptor.getValue();
    assertEquals("JK Services", savedOrganization.getOrganizationName());
    assertTrue(savedOrganization.isActive());
    assertNotNull(savedOrganization.getInviteCode());
    assertFalse(savedOrganization.getInviteCode().isBlank());

    ArgumentCaptor<OrganizationMemberEntity> memberCaptor =
            ArgumentCaptor.forClass(OrganizationMemberEntity.class);
    verify(organizationMemberRepository).save(memberCaptor.capture());

    OrganizationMemberEntity savedMember = memberCaptor.getValue();
    assertEquals(1L, savedMember.getOrganizationId());
    assertEquals(10L, savedMember.getUserId());
    assertEquals(OrganizationRole.OWNER, savedMember.getRole());
    assertEquals(OrganizationMembershipStatus.ACTIVE, savedMember.getMembershipStatus());
    assertNotNull(savedMember.getJoinedAt());

    @SuppressWarnings("unchecked")
    ArgumentCaptor<Map<String, Object>> detailsCaptor =
            ArgumentCaptor.forClass((Class<Map<String, Object>>) (Class<?>) Map.class);

    verify(auditLogService)
            .record(
                    eq(1L),
                    isNull(),
                    isNull(),
                    eq("ORGANIZATION_CREATED"),
                    eq("ORGANIZATION"),
                    eq("1"),
                    detailsCaptor.capture());

    Map<String, Object> details = detailsCaptor.getValue();

    assertEquals("JK Services", details.get("organizationName"));
    assertEquals(10L, ((Number) details.get("ownerUserId")).longValue());
  }

  @Test
  void createOrganization_shouldGenerateAnotherInviteCodeWhenCodeAlreadyExists() {
    CreateOrganizationRequest request =
            new CreateOrganizationRequest(
                    "JK Services", "Yogesh Pawar", "yogesh@example.com", "password123");

    when(userRepository.existsByEmail("yogesh@example.com")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

    when(userRepository.save(any(UserEntity.class)))
            .thenAnswer(
                    invocation -> {
                      UserEntity user = invocation.getArgument(0);
                      user.setId(10L);
                      return user;
                    });

    when(organizationRepository.existsByInviteCode(anyString())).thenReturn(true, false);

    when(organizationRepository.save(any(OrganizationEntity.class)))
            .thenAnswer(
                    invocation -> {
                      OrganizationEntity saved = invocation.getArgument(0);
                      saved.setId(1L);
                      return saved;
                    });

    when(organizationMapper.toResponse(any(OrganizationEntity.class)))
            .thenReturn(organizationResponse);

    organizationService.createOrganization(request);

    verify(organizationRepository, times(2)).existsByInviteCode(anyString());
    verify(organizationRepository).save(any(OrganizationEntity.class));
  }

  @Test
  void createOrganization_shouldThrowWhenEmailAlreadyExists() {
    CreateOrganizationRequest request =
            new CreateOrganizationRequest(
                    "JK Services", "Yogesh Pawar", "YOGESH@EXAMPLE.COM", "password123");

    when(userRepository.existsByEmail("yogesh@example.com")).thenReturn(true);

    assertThrows(
            DuplicateResourceException.class,
            () -> organizationService.createOrganization(request));

    verify(userRepository, never()).save(any());
    verify(organizationRepository, never()).save(any());
    verify(organizationMemberRepository, never()).save(any());
    verifyNoInteractions(passwordEncoder);
    verifyNoInteractions(auditLogService);
  }

  @Test
  void getOrganizations_shouldReturnAllOrganizations() {
    OrganizationEntity secondOrganization = new OrganizationEntity();
    secondOrganization.setId(2L);
    secondOrganization.setOrganizationName("Second Organization");

    OrganizationResponse secondResponse =
            new OrganizationResponse(2L, "Second Organization", "XYZ456", true, null, null);

    when(organizationRepository.findAll())
            .thenReturn(List.of(organization, secondOrganization));
    when(organizationMapper.toResponse(organization)).thenReturn(organizationResponse);
    when(organizationMapper.toResponse(secondOrganization)).thenReturn(secondResponse);

    List<OrganizationResponse> result = organizationService.getOrganizations();

    assertEquals(2, result.size());
    assertEquals(organizationResponse, result.get(0));
    assertEquals(secondResponse, result.get(1));

    verify(organizationRepository).findAll();
  }

  @Test
  void getOrganization_shouldReturnOrganizationWhenFound() {
    when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));
    when(organizationMapper.toResponse(organization)).thenReturn(organizationResponse);

    OrganizationResponse result = organizationService.getOrganization(1L);

    assertEquals(organizationResponse, result);
    verify(organizationRepository).findById(1L);
  }

  @Test
  void getOrganization_shouldThrowWhenOrganizationDoesNotExist() {
    when(organizationRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(
            ResourceNotFoundException.class,
            () -> organizationService.getOrganization(99L));

    verify(organizationMapper, never()).toResponse(any());
  }

  @Test
  void updateOrganization_shouldUpdateNameAndStatusAndRecordAudit() {
    when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));

    when(organizationRepository.save(any(OrganizationEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    when(organizationMapper.toResponse(organization)).thenReturn(organizationResponse);

    UpdateOrganizationRequest request =
            new UpdateOrganizationRequest("  Updated JK Services  ", false);

    OrganizationResponse result = organizationService.updateOrganization(1L, request);

    assertEquals(organizationResponse, result);
    assertEquals("Updated JK Services", organization.getOrganizationName());
    assertFalse(organization.isActive());

    @SuppressWarnings("unchecked")
    ArgumentCaptor<Map<String, Object>> detailsCaptor =
            ArgumentCaptor.forClass((Class<Map<String, Object>>) (Class<?>) Map.class);

    verify(auditLogService)
            .record(
                    eq(1L),
                    isNull(),
                    isNull(),
                    eq("ORGANIZATION_UPDATED"),
                    eq("ORGANIZATION"),
                    eq("1"),
                    detailsCaptor.capture());

    Map<String, Object> details = detailsCaptor.getValue();

    Map<?, ?> before = (Map<?, ?>) details.get("before");
    Map<?, ?> after = (Map<?, ?>) details.get("after");

    assertEquals("JK Services", before.get("organizationName"));
    assertEquals(Boolean.TRUE, before.get("active"));

    assertEquals("Updated JK Services", after.get("organizationName"));
    assertEquals(Boolean.FALSE, after.get("active"));
  }

  @Test
  void updateOrganization_shouldReactivateInactiveOrganization() {
    organization.setActive(false);

    when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));

    when(organizationRepository.save(any(OrganizationEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    when(organizationMapper.toResponse(organization)).thenReturn(organizationResponse);

    UpdateOrganizationRequest request =
            new UpdateOrganizationRequest("JK Services Updated", true);

    OrganizationResponse result = organizationService.updateOrganization(1L, request);

    assertEquals(organizationResponse, result);
    assertEquals("JK Services Updated", organization.getOrganizationName());
    assertTrue(organization.isActive());

    verify(organizationRepository).save(organization);
    verify(auditLogService)
            .record(
                    eq(1L),
                    isNull(),
                    isNull(),
                    eq("ORGANIZATION_UPDATED"),
                    eq("ORGANIZATION"),
                    eq("1"),
                    anyMap());
  }

  @Test
  void updateOrganization_shouldThrowWhenOrganizationDoesNotExist() {
    when(organizationRepository.findById(99L)).thenReturn(Optional.empty());

    UpdateOrganizationRequest request =
            new UpdateOrganizationRequest("Updated Name", true);

    assertThrows(
            ResourceNotFoundException.class,
            () -> organizationService.updateOrganization(99L, request));

    verify(organizationRepository, never()).save(any());
    verifyNoInteractions(auditLogService);
  }

  @Test
  void deactivateOrganization_shouldDeactivateAndRecordAudit() {
    when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));

    when(organizationRepository.save(any(OrganizationEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    organizationService.deactivateOrganization(1L);

    assertFalse(organization.isActive());

    @SuppressWarnings("unchecked")
    ArgumentCaptor<Map<String, Object>> detailsCaptor =
            ArgumentCaptor.forClass((Class<Map<String, Object>>) (Class<?>) Map.class);

    verify(auditLogService)
            .record(
                    eq(1L),
                    isNull(),
                    isNull(),
                    eq("ORGANIZATION_DEACTIVATED"),
                    eq("ORGANIZATION"),
                    eq("1"),
                    detailsCaptor.capture());

    Map<String, Object> details = detailsCaptor.getValue();

    assertEquals(Boolean.TRUE, details.get("previousActive"));
    assertEquals(Boolean.FALSE, details.get("active"));
    assertEquals("JK Services", details.get("organizationName"));
  }

  @Test
  void deactivateOrganization_shouldDoNothingWhenAlreadyInactive() {
    organization.setActive(false);
    when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));

    organizationService.deactivateOrganization(1L);

    verify(organizationRepository, never()).save(any());
    verifyNoInteractions(auditLogService);
  }

  @Test
  void deactivateOrganization_shouldThrowWhenOrganizationDoesNotExist() {
    when(organizationRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(
            ResourceNotFoundException.class,
            () -> organizationService.deactivateOrganization(99L));

    verify(organizationRepository, never()).save(any());
    verifyNoInteractions(auditLogService);
  }
}
