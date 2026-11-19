package com.jkservice.invoice.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "invoices",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_invoices_business_number",
          columnNames = {"business_id", "invoice_number"}),
      @UniqueConstraint(
          name = "uk_invoices_id_business_customer",
          columnNames = {"id", "business_id", "customer_id"})
    },
    indexes = {
      @Index(
          name = "idx_invoices_customer_business_date",
          columnList = "customer_id, business_id, invoice_date")
      // Retain the other indexes defined in your V1 SQL.
    })
public class InvoiceEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "business_id", nullable = false)
  private Long businessId;

  @Column(name = "customer_id", nullable = false)
  private Long customerId;

  @Column(name = "invoice_number", length = 50, nullable = false)
  private String invoiceNumber;

  @Column(name = "invoice_date", nullable = false)
  private LocalDate invoiceDate;

  @Column(name = "due_date")
  private LocalDate dueDate;

  @Column(name = "total_amount", precision = 14, scale = 2, nullable = false)
  private BigDecimal totalAmount;

  @Column(name = "status", length = 30, nullable = false)
  private String status;

  @Column(name = "notes", columnDefinition = "text")
  private String notes;

  @Column(name = "created_by", nullable = false)
  private Long createdBy;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime updatedAt;
}
