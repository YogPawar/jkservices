package com.jkservice.work.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "work_entries",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_work_entries_id_business_customer",
          columnNames = {"id", "business_id", "customer_id"})
    },
    indexes = {
      @Index(name = "idx_work_entries_farmer_date", columnList = "farmer_id, work_date"),
      @Index(
          name = "idx_work_entries_farmer_business_customer",
          columnList = "farmer_id, business_id, customer_id")
      // Retain the other indexes from your V1 SQL here.
    })
public class WorkEntryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "business_id", nullable = false)
  private Long businessId;

  @Column(name = "farmer_id", nullable = false)
  private Long farmerId;

  @Column(name = "customer_id", nullable = false)
  private Long customerId;

  @Column(name = "work_type_id")
  private Long workTypeId;

  @Column(name = "work_date", nullable = false)
  private LocalDate workDate;

  @Column(name = "location", length = 255)
  private String location;

  @Column(name = "quantity_description", length = 255)
  private String quantityDescription;

  @Column(name = "total_amount", precision = 14, scale = 2, nullable = false)
  private BigDecimal totalAmount;

  @Column(name = "notes", columnDefinition = "text")
  private String notes;

  @Column(name = "billing_status", length = 20, nullable = false)
  private String billingStatus;

  @Column(name = "verification_status", length = 20, nullable = false)
  private String verificationStatus;

  @Column(name = "created_by", nullable = false)
  private Long createdBy;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime updatedAt;

  @Version
  @Column(name = "version", nullable = false)
  private Long version;
}
