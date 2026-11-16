package com.jkservice.family.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "families")
public class FamilyEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "family_name", nullable = false, length = 120)
  private String familyName;

  @Column(name = "invite_code", nullable = false, unique = true, length = 32)
  private String inviteCode;

  @Column(name = "active", nullable = false)
  private boolean active = true;

  @Setter(AccessLevel.NONE)
  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;
}
