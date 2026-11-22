package com.jkservice.auth.application;

import com.jkservice.auth.api.dto.response.RegistrationResponse;
import com.jkservice.auth.entity.AppUserEntity;
import com.jkservice.auth.repository.AppUserRepository;
import com.jkservice.common.exception.BusinessException;
import com.jkservice.common.exception.DuplicateResourceException;
import com.jkservice.organization.dto.request.CreateOrganizationRequest;
import com.jkservice.organization.dto.request.JoinOrganizationRequest;
import com.jkservice.organization.entity.OrganizationEntity;
import com.jkservice.organization.entity.OrganizationMemberEntity;
import com.jkservice.organization.enums.OrganizationMembershipStatus;
import com.jkservice.organization.enums.OrganizationRole;
import com.jkservice.organization.repository.OrganizationMemberRepository;
import com.jkservice.organization.repository.OrganizationRepository;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Locale;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

  private static final String INVITE_CODE_CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

  private static final int INVITE_CODE_RANDOM_LENGTH = 8;
  private static final int MAX_INVITE_CODE_ATTEMPTS = 10;

  private final OrganizationRepository organizations;
  private final OrganizationMemberRepository organizationMembers;
  private final AppUserRepository users;
  private final BCryptPasswordEncoder encoder;
  private final SecureRandom random = new SecureRandom();

  public RegistrationService(
      OrganizationRepository organizations,
      OrganizationMemberRepository organizationMembers,
      AppUserRepository users,
      BCryptPasswordEncoder encoder) {
    this.organizations = organizations;
    this.organizationMembers = organizationMembers;
    this.users = users;
    this.encoder = encoder;
  }

  @Transactional
  public RegistrationResponse registerOrganization(CreateOrganizationRequest request) {

    String email = normalizeEmail(request.email());
    ensureEmailAvailable(email);

    OrganizationEntity organization = new OrganizationEntity();
    organization.setOrganizationName(request.organizationName().trim());
    organization.setInviteCode(generateUniqueInviteCode());
    organization.setActive(true);

    organization = organizations.save(organization);

    AppUserEntity user = new AppUserEntity();
    user.setFullName(request.fullName().trim());
    user.setEmail(email);
    user.setPasswordHash(encoder.encode(request.password()));
    user.setActive(true);

    user = users.save(user);

    OrganizationMemberEntity membership = new OrganizationMemberEntity();
    membership.setOrganizationId(organization.getId());
    membership.setUserId(user.getId());
    membership.setRole(OrganizationRole.OWNER);
    membership.setMembershipStatus(OrganizationMembershipStatus.ACTIVE);
    membership.setJoinedAt(Instant.now());

    membership = organizationMembers.save(membership);

    return new RegistrationResponse(
        user.getId(),
        organization.getId(),
        membership.getRole().name(),
        membership.getMembershipStatus().name(),
        "Organization created. Invite code: " + organization.getInviteCode());
  }

  @Transactional
  public RegistrationResponse joinOrganization(JoinOrganizationRequest request) {

    String email = normalizeEmail(request.email());
    ensureEmailAvailable(email);

    String inviteCode = normalizeInviteCode(request.inviteCode());

    OrganizationEntity organization =
        organizations
            .findByInviteCodeAndActiveTrue(inviteCode)
            .orElseThrow(
                () -> new BusinessException("Invalid or inactive organization invite code"));

    AppUserEntity user = new AppUserEntity();
    user.setFullName(request.fullName().trim());
    user.setEmail(email);
    user.setPasswordHash(encoder.encode(request.password()));
    user.setActive(true);

    user = users.save(user);

    OrganizationMemberEntity membership = new OrganizationMemberEntity();
    membership.setOrganizationId(organization.getId());
    membership.setUserId(user.getId());
    membership.setRole(OrganizationRole.MEMBER);
    membership.setMembershipStatus(OrganizationMembershipStatus.INVITED);
    membership.setJoinedAt(null);

    membership = organizationMembers.save(membership);

    return new RegistrationResponse(
        user.getId(),
        organization.getId(),
        membership.getRole().name(),
        membership.getMembershipStatus().name(),
        "Registration submitted. An OWNER or ADMIN must approve access.");
  }

  private void ensureEmailAvailable(String email) {
    if (users.existsByEmail(email)) {
      throw new DuplicateResourceException("Email is already registered");
    }
  }

  private String normalizeEmail(String email) {
    return email.trim().toLowerCase(Locale.ROOT);
  }

  private String normalizeInviteCode(String inviteCode) {
    return inviteCode.trim().toUpperCase(Locale.ROOT);
  }

  private String generateUniqueInviteCode() {

    for (int attempt = 0; attempt < MAX_INVITE_CODE_ATTEMPTS; attempt++) {

      StringBuilder code = new StringBuilder("JK");

      for (int i = 0; i < INVITE_CODE_RANDOM_LENGTH; i++) {
        int index = random.nextInt(INVITE_CODE_CHARACTERS.length());
        code.append(INVITE_CODE_CHARACTERS.charAt(index));
      }

      String candidate = code.toString();

      if (!organizations.existsByInviteCode(candidate)) {
        return candidate;
      }
    }

    throw new BusinessException("Unable to generate a unique organization invite code");
  }
}
