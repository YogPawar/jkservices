package com.jkservice.farmer.entity;

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
    name = "farmers",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_farmer_customer",
          columnNames = {"customer_id"}),
      @UniqueConstraint(
          name = "uk_farmer_code",
          columnNames = {"business_id", "farmer_code"}),
      @UniqueConstraint(
          name = "uk_farmer_id_business_customer",
          columnNames = {"id", "business_id", "customer_id"})
    },
    indexes = {
      @Index(name = "idx_farmers_customer_business", columnList = "customer_id, business_id")
    })
public class FarmerEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "business_id", nullable = false)
  private Long businessId;

  @Column(name = "customer_id", nullable = false)
  private Long customerId;

  @Column(name = "farmer_code", length = 50, nullable = false)
  private String farmerCode;

  @Column(name = "village", length = 120)
  private String village;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime updatedAt;
}
