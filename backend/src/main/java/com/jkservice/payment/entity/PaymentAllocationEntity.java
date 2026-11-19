
package com.jkservice.payment.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "payment_allocations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_payment_allocations_payment_invoice",
                        columnNames = {"payment_id", "invoice_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_payment_allocations_payment_business_customer",
                        columnList = "payment_id, business_id, customer_id"
                ),
                @Index(
                        name = "idx_payment_allocations_invoice_business_customer",
                        columnList = "invoice_id, business_id, customer_id"
                )
        }
)
public class PaymentAllocationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "business_id", nullable = false)
  private Long businessId;

  @Column(name = "customer_id", nullable = false)
  private Long customerId;

  @Column(name = "payment_id", nullable = false)
  private Long paymentId;

  @Column(name = "invoice_id", nullable = false)
  private Long invoiceId;

  @Column(name = "allocated_amount", precision = 14, scale = 2, nullable = false)
  private BigDecimal allocatedAmount;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;
}
