package com.jkservice.user.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "app_users",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_app_users_email",
          columnNames = {"email"})
    })
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "full_name", length = 150, nullable = false)
  private String fullName;

  @Column(name = "email", length = 255, nullable = false)
  private String email;

  @Column(name = "password_hash", length = 255, nullable = false)
  private String passwordHash;

  @Column(name = "active", nullable = false)
  private boolean active;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private Instant updatedAt;

  public UserEntity(String fullName, String email, String passwordHash) {
    this.fullName = fullName;
    this.email = email;
    this.passwordHash = passwordHash;
    this.active = true;
  }
}
