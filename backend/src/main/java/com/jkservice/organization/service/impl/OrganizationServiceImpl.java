package com.jkservice.organization.service.impl;

import com.jkservice.auth.entity.AppUserEntity;
import com.jkservice.auth.repository.AppUserRepository;
import com.jkservice.common.exception.DuplicateResourceException;
import com.jkservice.common.exception.ResourceNotFoundException;
import com.jkservice.logging.audit.service.AuditLogService;
import com.jkservice.organization.dto.request.CreateOrganizationRequest;
import com.jkservice.organization.dto.request.JoinOrganizationRequest;
import com.jkservice.organization.dto.request.UpdateOrganizationRequest;
import com.jkservice.organization.dto.response.OrganizationResponse;
import com.jkservice.organization.dto.response.RegistrationResponse;
import com.jkservice.organization.entity.OrganizationEntity;
import com.jkservice.organization.entity.OrganizationMemberEntity;
import com.jkservice.organization.enums.OrganizationMembershipStatus;
import com.jkservice.organization.enums.OrganizationRole;
import com.jkservice.organization.mapper.OrganizationMapper;
import com.jkservice.organization.repository.OrganizationMemberRepository;
import com.jkservice.organization.repository.OrganizationRepository;
import com.jkservice.organization.service.OrganizationService;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

  private static final String ENTITY_TYPE = "ORGANIZATION";

  private final OrganizationRepository organizationRepository;
  private final OrganizationMemberRepository organizationMemberRepository;
  private final AppUserRepository appUserRepository;
  private final OrganizationMapper organizationMapper;
  private final PasswordEncoder passwordEncoder;
  private final AuditLogService auditLogService;

  @Override
  public OrganizationResponse createOrganization(CreateOrganizationRequest request) {

    log.info("Processing organization creation request");

    String email = normalizeEmail(request.email());

    // 1. Check whether the user already exists.
    if (appUserRepository.existsByEmail(email)) {
      log.warn("Organization creation rejected: user email already registered");
      throw new DuplicateResourceException("User email already exists");
    }

    // 2. Create the initial user.
    AppUserEntity user = new AppUserEntity();
    user.setFullName(request.fullName().trim());
    user.setEmail(email);
    user.setPasswordHash(passwordEncoder.encode(request.password()));
    user.setActive(true);

    AppUserEntity savedUser = appUserRepository.save(user);

    // 3. Create the organization.
    OrganizationEntity organization = new OrganizationEntity();
    organization.setOrganizationName(request.organizationName().trim());
    organization.setInviteCode(generateInviteCode());
    organization.setActive(true);

    OrganizationEntity savedOrganization = organizationRepository.save(organization);

    // 4. Create the initial organization owner membership.
    OrganizationMemberEntity member = new OrganizationMemberEntity();
    member.setOrganizationId(savedOrganization.getId());
    member.setUserId(savedUser.getId());
    member.setRole(OrganizationRole.OWNER);
    member.setMembershipStatus(OrganizationMembershipStatus.ACTIVE);
    member.setJoinedAt(Instant.now());

    // Persist membership exactly once.
    organizationMemberRepository.save(member);

    log.debug(
        "Organization owner membership created: organizationId={}, userId={}",
        savedOrganization.getId(),
        savedUser.getId());

    // 5. Record organization creation.
    auditLogService.record(
        savedOrganization.getId(),
        null,
        null,
        "ORGANIZATION_CREATED",
        ENTITY_TYPE,
        savedOrganization.getId().toString(),
        Map.of(
            "organizationName", savedOrganization.getOrganizationName(),
            "ownerUserId", savedUser.getId()));

    log.info("Organization created successfully: organizationId={}", savedOrganization.getId());

    return organizationMapper.toResponse(savedOrganization);
  }

  @Override
  @Transactional(readOnly = true)
  public List<OrganizationResponse> getOrganizations() {

    List<OrganizationResponse> organizations =
        organizationRepository.findAll().stream().map(organizationMapper::toResponse).toList();

    log.info("Retrieved organizations: count={}", organizations.size());

    return organizations;
  }

  @Override
  @Transactional(readOnly = true)
  public OrganizationResponse getOrganization(Long organizationId) {

    log.debug("Retrieving organization: organizationId={}", organizationId);

    OrganizationEntity organization = findOrganization(organizationId);

    return organizationMapper.toResponse(organization);
  }

  @Override
  public OrganizationResponse updateOrganization(
      Long organizationId, UpdateOrganizationRequest request) {
    log.info("Updating organization: organizationId={}", organizationId);

    OrganizationEntity organization = findOrganization(organizationId);

    String previousName = organization.getOrganizationName();
    boolean previousActive = organization.isActive();

    organization.setOrganizationName(request.organizationName().trim());
    organization.setActive(request.active());

    OrganizationEntity updatedOrganization = organizationRepository.save(organization);

    // Record before/after values for audit.
    auditLogService.record(
        organizationId,
        null,
        null,
        "ORGANIZATION_UPDATED",
        ENTITY_TYPE,
        organizationId.toString(),
        Map.of(
            "before",
            Map.of(
                "organizationName", previousName,
                "active", previousActive),
            "after",
            Map.of(
                "organizationName", updatedOrganization.getOrganizationName(),
                "active", updatedOrganization.isActive())));

    log.info(
        "Organization updated successfully: organizationId={}, active={}",
        organizationId,
        updatedOrganization.isActive());

    return organizationMapper.toResponse(updatedOrganization);
  }

  @Override
  public void deactivateOrganization(Long organizationId) {

    log.info("Deactivating organization: organizationId={}", organizationId);

    OrganizationEntity organization = findOrganization(organizationId);

    if (!organization.isActive()) {
      log.debug("Organization is already inactive: organizationId={}", organizationId);
      return;
    }

    organization.setActive(false);

    OrganizationEntity deactivatedOrganization = organizationRepository.save(organization);

    // Record deactivation for audit.
    auditLogService.record(
        organizationId,
        null,
        null,
        "ORGANIZATION_DEACTIVATED",
        ENTITY_TYPE,
        organizationId.toString(),
        Map.of(
            "organizationName",
            deactivatedOrganization.getOrganizationName(),
            "previousActive",
            true,
            "active",
            false));

    log.info("Organization deactivated successfully: organizationId={}", organizationId);
  }

  @Override
  public RegistrationResponse joinOrganization(JoinOrganizationRequest request) {

    String normalizedEmail = normalizeEmail(request.email());
    String inviteCode = request.inviteCode().trim();

    log.info("Processing organization join request");

    // 1. Find an active organization using the invite code.
    OrganizationEntity organization =
        organizationRepository
            .findByInviteCodeAndActiveTrue(inviteCode)
            .orElseThrow(() -> {
              log.warn("Organization join rejected: invalid or inactive invite code");
              return new ResourceNotFoundException("Invalid or inactive organization invite code");
            });

    // 2. Prevent duplicate user registration.
    if (appUserRepository.existsByEmail(normalizedEmail)) {
      log.warn("Organization join rejected: user email already registered");

      throw new DuplicateResourceException("An account with this email already exists");
    }

    // 3. Create the application user.
    AppUserEntity user = new AppUserEntity();
    user.setFullName(request.fullName().trim());
    user.setEmail(normalizedEmail);
    user.setPasswordHash(passwordEncoder.encode(request.password()));
    user.setActive(true);

    AppUserEntity savedUser = appUserRepository.save(user);

    // 4. Create organization membership.
    OrganizationMemberEntity membership = new OrganizationMemberEntity();
    membership.setOrganizationId(organization.getId());
    membership.setUserId(savedUser.getId());
    membership.setRole(OrganizationRole.OWNER);
    membership.setMembershipStatus(OrganizationMembershipStatus.ACTIVE);
    membership.setJoinedAt(Instant.now());

    OrganizationMemberEntity savedMembership = organizationMemberRepository.save(membership);

    // 5. Record membership creation in the audit log.
    auditLogService.record(
        organization.getId(),
        savedUser.getId(),
        savedUser.getId(),
        "ORGANIZATION_MEMBER_JOINED",
        "ORGANIZATION_MEMBER",
        savedMembership.getId().toString(),
        Map.of(
            "userId", savedUser.getId(),
            "organizationId", organization.getId(),
            "role", savedMembership.getRole(),
            "membershipStatus", savedMembership.getMembershipStatus()));

    log.info(
        "User joined organization successfully: organizationId={}, userId={}, membershipId={}",
        organization.getId(),
        savedUser.getId(),
        savedMembership.getId());

    return new RegistrationResponse(
        savedUser.getId(),
        organization.getId(),
        savedMembership.getRole().name(),
        savedMembership.getMembershipStatus().name(),
        "Successfully joined organization");
  }

  private OrganizationEntity findOrganization(Long organizationId) {

    return organizationRepository
        .findById(organizationId)
        .orElseThrow(() ->
            new ResourceNotFoundException("Organization not found with ID: " + organizationId));
  }

  private String normalizeEmail(String email) {
    return email.trim().toLowerCase(Locale.ROOT);
  }

  private String generateInviteCode() {

    String inviteCode;

    do {
      inviteCode = UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.ROOT);
    } while (organizationRepository.existsByInviteCode(inviteCode));

    return inviteCode;
  }
}
