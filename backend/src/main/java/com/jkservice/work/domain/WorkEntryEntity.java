package com.jkservice.work.domain;

import com.jkservice.auth.entity.AppUserEntity;
import com.jkservice.family.entity.FamilyEntity;
import com.jkservice.farmer.entity.FarmerEntity;
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
    indexes = {
      @Index(name = "idx_work_family_date", columnList = "family_id, work_date"),
      @Index(name = "idx_work_farmer_date", columnList = "farmer_id, work_date"),
      @Index(name = "idx_work_billing", columnList = "family_id, billing_status, work_date")
    })
public class WorkEntryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "family_id", nullable = false)
  private FamilyEntity family;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "farmer_id", nullable = false)
  private FarmerEntity farmer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "work_type_id")
  private WorkTypeEntity workType;

  @Column(name = "work_date", nullable = false)
  private LocalDate workDate;

  @Column(name = "location", length = 200)
  private String location;

  @Column(name = "quantity_description", length = 200)
  private String quantityDescription;

  @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal totalAmount = BigDecimal.ZERO;

  @Column(name = "notes", columnDefinition = "TEXT")
  private String notes;

  @Column(name = "billing_status", nullable = false, length = 20)
  private String billingStatus = "UNBILLED";

  @Column(name = "verification_status", nullable = false, length = 20)
  private String verificationStatus = "PENDING";

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "created_by", nullable = false)
  private AppUserEntity createdBy;

  @Column(name = "created_at", insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", insertable = false, updatable = false)
  private LocalDateTime updatedAt;

  @Version
  @Column(name = "version", nullable = false)
  private Long version;
}
