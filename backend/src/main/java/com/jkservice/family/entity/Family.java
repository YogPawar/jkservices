package com.jkservice.family.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "families")
public class Family {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "family_name", nullable = false)
  private String familyName;

  @Column(name = "invite_code", nullable = false, unique = true)
  private String inviteCode;

  private boolean active = true;
}
