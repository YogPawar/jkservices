package com.jkservice.customer.entity;

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
    name = "customers",
    indexes = {
      @Index(name = "idx_customers_business_name", columnList = "business_id, name"),
      @Index(name = "idx_customers_business_phone", columnList = "business_id, phone")
    },
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_customer_code",
          columnNames = {"business_id", "customer_code"})
    })
public class CustomerEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "business_id", nullable = false)
  private Long businessId;

  @Column(name = "customer_code", length = 50, nullable = false)
  private String customerCode;

  @Column(name = "name", length = 150, nullable = false)
  private String name;

  @Column(name = "phone", length = 20)
  private String phone;

  @Column(name = "email", length = 255)
  private String email;

  @Column(name = "address", columnDefinition = "text")
  private String address;

  @Column(name = "active", nullable = false)
  private boolean active;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime updatedAt;
}
