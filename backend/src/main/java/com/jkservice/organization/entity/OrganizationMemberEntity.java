package com.jkservice.organization.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
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

  @Column(name = "role", length = 30, nullable = false)
  private String role;

  @Column(name = "membership_status", length = 30, nullable = false)
  private String membershipStatus;

  @Column(name = "joined_at")
  private LocalDateTime joinedAt;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime updatedAt;
}
