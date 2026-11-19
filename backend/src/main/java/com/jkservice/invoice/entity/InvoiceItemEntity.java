package com.jkservice.invoice.entity;

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
    name = "invoice_items",
    indexes = {
      @Index(
          name = "idx_invoice_items_invoice",
          columnList = "invoice_id, business_id, customer_id"),
      @Index(
          name = "idx_invoice_items_work",
          columnList = "work_entry_id, business_id, customer_id"),
      @Index(
          name = "idx_invoice_items_booking",
          columnList = "event_booking_id, business_id, customer_id")
    })
public class InvoiceItemEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "business_id", nullable = false)
  private Long businessId;

  @Column(name = "customer_id", nullable = false)
  private Long customerId;

  @Column(name = "invoice_id", nullable = false)
  private Long invoiceId;

  @Column(name = "work_entry_id")
  private Long workEntryId;

  @Column(name = "event_booking_id")
  private Long eventBookingId;

  @Column(name = "description", length = 500, nullable = false)
  private String description;

  @Column(name = "quantity", precision = 12, scale = 3, nullable = false)
  private BigDecimal quantity;

  @Column(name = "unit_price", precision = 14, scale = 2, nullable = false)
  private BigDecimal unitPrice;

  @Column(name = "amount", precision = 14, scale = 2, nullable = false)
  private BigDecimal amount;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;
}
