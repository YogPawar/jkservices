package com.jkservice.organization.entity;

import com.jkservice.organization.enums.OrganizationMembershipStatus;
import com.jkservice.organization.enums.OrganizationRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "organization_members",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_org_member_user",
          columnNames = {"organization_id", "user_id"}),
      @UniqueConstraint(
          name = "uk_org_member_id_org",
          columnNames = {"id", "organization_id"})
    },
    indexes = {
      @Index(name = "idx_org_members_user", columnList = "user_id"),
      @Index(name = "idx_org_members_status", columnList = "organization_id, membership_status")
    })
public class OrganizationMemberEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "organization_id", nullable = false)
  private Long organizationId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", length = 30, nullable = false)
  private OrganizationRole role;

  @Enumerated(EnumType.STRING)
  @Column(name = "membership_status", length = 30, nullable = false)
  private OrganizationMembershipStatus membershipStatus;

  @Column(name = "joined_at")
  private Instant joinedAt;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private Instant updatedAt;
}
