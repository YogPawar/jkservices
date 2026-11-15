package com.jkservice.family.domain;

import jakarta.persistence.*;

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

  public Long getId() {
    return id;
  }

  public String getFamilyName() {
    return familyName;
  }

  public void setFamilyName(String v) {
    familyName = v;
  }

  public String getInviteCode() {
    return inviteCode;
  }

  public void setInviteCode(String v) {
    inviteCode = v;
  }

  public boolean isActive() {
    return active;
  }
}
