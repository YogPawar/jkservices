package com.jkservice.organization.service.impl;

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
import com.jkservice.organization.service.OrganizationMemberService;
import com.jkservice.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationMemberServiceImpl implements OrganizationMemberService {

  private static final String ENTITY_TYPE = "ORGANIZATION_MEMBER";

  private final OrganizationMemberRepository memberRepository;
  private final OrganizationRepository organizationRepository;
  private final UserRepository userRepository;
  private final OrganizationMemberMapper memberMapper;
  private final AuditLogService auditLogService;
  private final EntityManager entityManager;

  @Override
  public OrganizationMemberResponse addMember(
      Long organizationId, CreateOrganizationMemberRequest request) {

    validateOrganizationExists(organizationId);

    var user =
        userRepository
            .findById(request.userId())
            .orElseThrow(
                () -> new ResourceNotFoundException("User not found with ID: " + request.userId()));

    if (!user.isActive()) {
      throw new IllegalArgumentException("Cannot add an inactive user to an organization");
    }

    if (memberRepository.existsByOrganizationIdAndUserId(organizationId, request.userId())) {
      throw new DuplicateResourceException("User is already a member of this organization");
    }

    OrganizationMemberEntity member = new OrganizationMemberEntity();
    member.setOrganizationId(organizationId);
    member.setUserId(request.userId());
    member.setRole(request.role());
    member.setMembershipStatus(OrganizationMembershipStatus.ACTIVE);
    member.setJoinedAt(Instant.now());

    OrganizationMemberEntity saved = memberRepository.save(member);

    recordAudit(
        organizationId,
        saved,
        "ORGANIZATION_MEMBER_ADDED",
        Map.of(
            "userId", saved.getUserId(),
            "role", saved.getRole().name(),
            "membershipStatus", saved.getMembershipStatus().name()));

    log.info(
        "Organization member added: organizationId={}, memberId={}, userId={}",
        organizationId,
        saved.getId(),
        saved.getUserId());

    return memberMapper.toResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<OrganizationMemberResponse> getMembers(Long organizationId) {

    validateOrganizationExists(organizationId);

    return memberRepository.findAllByOrganizationIdOrderByIdAsc(organizationId).stream()
        .map(memberMapper::toResponse)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public OrganizationMemberResponse getMember(Long organizationId, Long memberId) {

    return memberMapper.toResponse(findMember(organizationId, memberId));
  }

  @Override
  public OrganizationMemberResponse updateMember(
      Long organizationId, Long memberId, UpdateOrganizationMemberRequest request) {

    OrganizationMemberEntity member = findMember(organizationId, memberId);

    validateLastOwnerProtection(member, request.role(), request.membershipStatus());

    OrganizationRole previousRole = member.getRole();
    OrganizationMembershipStatus previousStatus = member.getMembershipStatus();

    member.setRole(request.role());
    member.setMembershipStatus(request.membershipStatus());

    if (request.membershipStatus() == OrganizationMembershipStatus.ACTIVE
        && member.getJoinedAt() == null) {
      member.setJoinedAt(Instant.now());
    }

    OrganizationMemberEntity updated = memberRepository.save(member);

    recordAudit(
        organizationId,
        updated,
        "ORGANIZATION_MEMBER_UPDATED",
        Map.of(
            "before",
            Map.of(
                "role", previousRole.name(),
                "membershipStatus", previousStatus.name()),
            "after",
            Map.of(
                "role",
                updated.getRole().name(),
                "membershipStatus",
                updated.getMembershipStatus().name())));

    log.info(
        "Organization member updated: organizationId={}, memberId={}", organizationId, memberId);

    return memberMapper.toResponse(updated);
  }

  @Override
  public void removeMember(Long organizationId, Long memberId) {

    OrganizationMemberEntity member = findMember(organizationId, memberId);

    validateLastOwnerProtection(member, null, OrganizationMembershipStatus.SUSPENDED);

    // The database has a foreign key from business_members to
    // organization_members. Revoke assignments before deleting membership.
    entityManager
        .createNativeQuery(
            """
            DELETE FROM business_members
            WHERE organization_member_id = :memberId
              AND organization_id = :organizationId
            """)
        .setParameter("memberId", memberId)
        .setParameter("organizationId", organizationId)
        .executeUpdate();

    recordAudit(
        organizationId,
        member,
        "ORGANIZATION_MEMBER_REMOVED",
        Map.of(
            "userId", member.getUserId(),
            "role", member.getRole().name(),
            "membershipStatus", member.getMembershipStatus().name()));

    memberRepository.delete(member);

    log.info(
        "Organization member removed: organizationId={}, memberId={}, userId={}",
        organizationId,
        memberId,
        member.getUserId());
  }

  private OrganizationMemberEntity findMember(Long organizationId, Long memberId) {

    validateOrganizationExists(organizationId);

    return memberRepository
        .findByIdAndOrganizationId(memberId, organizationId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Organization member not found: " + memberId));
  }

  private void validateOrganizationExists(Long organizationId) {

    if (!organizationRepository.existsById(organizationId)) {
      throw new ResourceNotFoundException("Organization not found with ID: " + organizationId);
    }
  }

  private void validateLastOwnerProtection(
      OrganizationMemberEntity member,
      OrganizationRole requestedRole,
      OrganizationMembershipStatus requestedStatus) {

    boolean currentlyActiveOwner =
        member.getRole() == OrganizationRole.OWNER
            && member.getMembershipStatus() == OrganizationMembershipStatus.ACTIVE;

    if (!currentlyActiveOwner) {
      return;
    }

    boolean remainsActiveOwner =
        requestedRole == OrganizationRole.OWNER
            && requestedStatus == OrganizationMembershipStatus.ACTIVE;

    if (remainsActiveOwner) {
      return;
    }

    long activeOwners =
        memberRepository.countByOrganizationIdAndRoleAndMembershipStatus(
            member.getOrganizationId(),
            OrganizationRole.OWNER,
            OrganizationMembershipStatus.ACTIVE);

    if (activeOwners <= 1) {
      throw new IllegalArgumentException(
          "Cannot remove, suspend, or demote the last active organization owner");
    }
  }

  private void recordAudit(
      Long organizationId,
      OrganizationMemberEntity member,
      String action,
      Map<String, Object> details) {

    auditLogService.record(
        organizationId, null, null, action, ENTITY_TYPE, member.getId().toString(), details);
  }
}
