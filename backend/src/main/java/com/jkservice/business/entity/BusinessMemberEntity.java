package com.jkservice.business.entity;

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
    name = "business_members",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_business_members_business_org_member",
          columnNames = {"business_id", "organization_member_id"}),
      @UniqueConstraint(
          name = "uk_business_members_id_business_org",
          columnNames = {"id", "business_id", "organization_id"})
    },
    indexes = {
      @Index(name = "idx_business_members_org", columnList = "organization_id"),
      @Index(
          name = "idx_business_members_business_org",
          columnList = "business_id, organization_id"),
      @Index(
          name = "idx_business_members_org_member_org",
          columnList = "organization_member_id, organization_id"),
      @Index(name = "idx_business_members_status", columnList = "business_id, membership_status")
    })
public class BusinessMemberEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "organization_id", nullable = false)
  private Long organizationId;

  @Column(name = "business_id", nullable = false)
  private Long businessId;

  @Column(name = "organization_member_id", nullable = false)
  private Long organizationMemberId;

  @Column(name = "role", length = 30, nullable = false)
  private String role;

  @Column(name = "membership_status", length = 30, nullable = false)
  private String membershipStatus;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime updatedAt;
}
