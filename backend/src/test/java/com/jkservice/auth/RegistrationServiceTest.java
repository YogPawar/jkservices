package com.jkservice.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jkservice.auth.api.dto.response.RegistrationResponse;
import com.jkservice.auth.application.RegistrationService;
import com.jkservice.auth.entity.AppUserEntity;
import com.jkservice.auth.persistence.AppUserRepository;
import com.jkservice.common.exception.BusinessException;
import com.jkservice.common.exception.DuplicateResourceException;
import com.jkservice.organization.dto.CreateOrganizationRequest;
import com.jkservice.organization.dto.JoinOrganizationRequest;
import com.jkservice.organization.entity.OrganizationEntity;
import com.jkservice.organization.entity.OrganizationMemberEntity;
import com.jkservice.organization.repository.OrganizationMemberRepository;
import com.jkservice.organization.repository.OrganizationRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

  @Mock private OrganizationRepository organizations;

  @Mock private OrganizationMemberRepository organizationMembers;

  @Mock private AppUserRepository users;

  @Mock private BCryptPasswordEncoder encoder;

  @InjectMocks private RegistrationService service;

  @Test
  void registerOrganization_shouldCreateOrganizationAndOwner() {

    CreateOrganizationRequest request =
        new CreateOrganizationRequest(
            " Test Organization ", " Test Owner ", " OWNER@EXAMPLE.COM ", "password123");

    when(users.existsByEmail("owner@example.com")).thenReturn(false);
    when(encoder.encode("password123")).thenReturn("encoded-password");
    when(organizations.existsByInviteCode(any())).thenReturn(false);

    when(organizations.save(any(OrganizationEntity.class)))
        .thenAnswer(invocation -> {
          OrganizationEntity entity = invocation.getArgument(0);
          entity.setId(10L);
          return entity;
        });

    when(users.save(any(AppUserEntity.class)))
        .thenAnswer(invocation -> {
          AppUserEntity entity = invocation.getArgument(0);
          entity.setId(20L);
          return entity;
        });

    when(organizationMembers.save(any(OrganizationMemberEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    RegistrationResponse response = service.registerOrganization(request);

    assertEquals(20L, response.userId());
    assertEquals(10L, response.organizationId());
    assertEquals("OWNER", response.role());
    assertEquals("ACTIVE", response.membershipStatus());

    verify(users).existsByEmail("owner@example.com");
    verify(organizations).save(any(OrganizationEntity.class));
    verify(users).save(any(AppUserEntity.class));
    verify(organizationMembers).save(any(OrganizationMemberEntity.class));
  }

  @Test
  void joinOrganization_shouldCreateInvitedMember() {

    JoinOrganizationRequest request =
        new JoinOrganizationRequest(
            " jkABC1234 ", " Test Member ", " MEMBER@EXAMPLE.COM ", "password123");

    OrganizationEntity organization = new OrganizationEntity();
    organization.setId(10L);
    organization.setActive(true);

    when(users.existsByEmail("member@example.com")).thenReturn(false);
    when(encoder.encode("password123")).thenReturn("encoded-password");

    when(organizations.findByInviteCodeAndActiveTrue("JKABC1234"))
        .thenReturn(Optional.of(organization));

    when(users.save(any(AppUserEntity.class)))
        .thenAnswer(invocation -> {
          AppUserEntity entity = invocation.getArgument(0);
          entity.setId(20L);
          return entity;
        });

    when(organizationMembers.save(any(OrganizationMemberEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    RegistrationResponse response = service.joinOrganization(request);

    assertEquals(20L, response.userId());
    assertEquals(10L, response.organizationId());
    assertEquals("MEMBER", response.role());
    assertEquals("INVITED", response.membershipStatus());

    verify(users).existsByEmail("member@example.com");
    verify(organizations).findByInviteCodeAndActiveTrue("JKABC1234");
    verify(users).save(any(AppUserEntity.class));
    verify(organizationMembers).save(any(OrganizationMemberEntity.class));
  }

  @Test
  void joinOrganization_shouldRejectInvalidInviteCode() {

    JoinOrganizationRequest request =
        new JoinOrganizationRequest(
            "INVALID123", "Test Member", "member@example.com", "password123");

    when(users.existsByEmail("member@example.com")).thenReturn(false);

    when(organizations.findByInviteCodeAndActiveTrue("INVALID123")).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> service.joinOrganization(request));

    verify(users, never()).save(any(AppUserEntity.class));
    verify(organizationMembers, never()).save(any(OrganizationMemberEntity.class));
  }

  @Test
  void registerOrganization_shouldRejectDuplicateEmail() {

    CreateOrganizationRequest request =
        new CreateOrganizationRequest(
            "Test Organization", "Test Owner", "owner@example.com", "password123");

    when(users.existsByEmail("owner@example.com")).thenReturn(true);

    assertThrows(DuplicateResourceException.class, () -> service.registerOrganization(request));

    verify(organizations, never()).save(any(OrganizationEntity.class));
    verify(users, never()).save(any(AppUserEntity.class));
    verify(organizationMembers, never()).save(any(OrganizationMemberEntity.class));
  }
}
