package com.jkservice.payment.entity;

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
    name = "payments",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_payment_idempotency",
          columnNames = {"family_id", "idempotency_key"})
    },
    indexes = {
      @Index(name = "idx_payment_farmer_date", columnList = "family_id, farmer_id, payment_date")
    })
public class PaymentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "family_id", nullable = false)
  private FamilyEntity family;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "farmer_id", nullable = false)
  private FarmerEntity farmer;

  @Column(name = "payment_date", nullable = false)
  private LocalDate paymentDate;

  @Column(name = "amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;

  @Column(name = "payment_mode", nullable = false, length = 30)
  private String paymentMode;

  @Column(name = "reference_number", length = 100)
  private String referenceNumber;

  @Column(name = "idempotency_key", nullable = false, length = 80)
  private String idempotencyKey;

  @Column(name = "notes", columnDefinition = "TEXT")
  private String notes;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "created_by", nullable = false)
  private AppUserEntity createdBy;

  @Column(name = "created_at", insertable = false, updatable = false)
  private LocalDateTime createdAt;
}
