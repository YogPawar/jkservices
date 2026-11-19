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
    name = "businesses",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_business_code",
          columnNames = {"organization_id", "business_code"}),
      @UniqueConstraint(
          name = "uk_business_id_org",
          columnNames = {"id", "organization_id"})
    },
    indexes = {
      @Index(name = "idx_businesses_organization", columnList = "organization_id, active")
    })
public class BusinessEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "organization_id", nullable = false)
  private Long organizationId;

  @Column(name = "business_name", length = 150, nullable = false)
  private String businessName;

  @Column(name = "business_type", length = 40, nullable = false)
  private String businessType;

  @Column(name = "business_code", length = 50, nullable = false)
  private String businessCode;

  @Column(name = "contact_number", length = 20)
  private String contactNumber;

  @Column(name = "email", length = 255)
  private String email;

  @Column(name = "address", columnDefinition = "text")
  private String address;

  @Column(name = "currency_code", length = 3, nullable = false)
  private String currencyCode;

  @Column(name = "active", nullable = false)
  private boolean active;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime updatedAt;
}
