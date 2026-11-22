package com.jkservice.organization.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "organizations",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_organizations_invite_code",
          columnNames = {"invite_code"})
    })
public class OrganizationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "organization_name", length = 150, nullable = false)
  private String organizationName;

  @Column(name = "invite_code", length = 50, nullable = false)
  private String inviteCode;

  @Column(name = "active", nullable = false)
  private boolean active;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private Instant updatedAt;
}
