package com.jkservice.auth.entity;

import com.jkservice.family.entity.FamilyEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_users")
public class AppUserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "family_id", nullable = false)
  private FamilyEntity family;

  @Column(name = "full_name", nullable = false, length = 120)
  private String fullName;

  @Column(name = "email", nullable = false, unique = true, length = 190)
  private String email;

  @Column(name = "password_hash", nullable = false, length = 255)
  private String passwordHash;

  @Column(name = "role", nullable = false, length = 20)
  private String role;

  @Column(name = "membership_status", nullable = false, length = 20)
  private String membershipStatus;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  public AppUserEntity() {}

  public Long getId() {
    return id;
  }

  public FamilyEntity getFamily() {
    return family;
  }

  public void setFamily(FamilyEntity family) {
    this.family = family;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getMembershipStatus() {
    return membershipStatus;
  }

  public void setMembershipStatus(String membershipStatus) {
    this.membershipStatus = membershipStatus;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
