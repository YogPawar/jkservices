package com.jkservice.payment.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "payments",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_payments_business_idempotency",
          columnNames = {"business_id", "idempotency_key"}),
      @UniqueConstraint(
          name = "uk_payments_id_business_customer",
          columnNames = {"id", "business_id", "customer_id"})
    },
    indexes = {
      @Index(
          name = "idx_payments_customer_business_date",
          columnList = "customer_id, business_id, payment_date")
    })
public class PaymentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "business_id", nullable = false)
  private Long businessId;

  @Column(name = "customer_id", nullable = false)
  private Long customerId;

  @Column(name = "payment_date", nullable = false)
  private LocalDate paymentDate;

  @Column(name = "amount", precision = 14, scale = 2, nullable = false)
  private BigDecimal amount;

  @Column(name = "payment_mode", length = 30, nullable = false)
  private String paymentMode;

  @Column(name = "reference_number", length = 100)
  private String referenceNumber;

  @Column(name = "idempotency_key", length = 100)
  private String idempotencyKey;

  @Column(name = "notes", columnDefinition = "text")
  private String notes;

  @Column(name = "created_by", nullable = false)
  private Long createdBy;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;
}
