package com.jkservice.farmer.entity;

import com.jkservice.family.entity.FamilyEntity;
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
@Table(
    name = "farmers",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_farmer_code_family",
          columnNames = {"family_id", "farmer_code"})
    },
    indexes = {@Index(name = "idx_farmers_family_name", columnList = "family_id, name")})
public class FarmerEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "family_id", nullable = false)
  private FamilyEntity family;

  @Column(name = "farmer_code", nullable = false, length = 30)
  private String farmerCode;

  @Column(name = "name", nullable = false, length = 150)
  private String name;

  @Column(name = "phone", length = 20)
  private String phone;

  @Column(name = "village", length = 100)
  private String village;

  @Column(name = "address", columnDefinition = "TEXT")
  private String address;

  @Column(name = "active", nullable = false)
  private boolean active = true;

  @Setter(AccessLevel.NONE)
  @Column(name = "created_at", insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", insertable = false, updatable = false)
  private LocalDateTime updatedAt;
}
