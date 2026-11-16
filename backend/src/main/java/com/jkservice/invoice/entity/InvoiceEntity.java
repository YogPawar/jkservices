package com.jkservice.invoice.entity;

import com.jkservice.auth.entity.AppUserEntity;
import com.jkservice.family.entity.FamilyEntity;
import com.jkservice.farmer.entity.FarmerEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "invoices",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_invoice_number_family",
          columnNames = {"family_id", "invoice_number"})
    },
    indexes = {
      @Index(name = "idx_invoice_due", columnList = "family_id, due_date, status"),
      @Index(name = "idx_invoice_farmer_date", columnList = "farmer_id, invoice_date")
    })
public class InvoiceEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "family_id", nullable = false)
  private FamilyEntity family;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "farmer_id", nullable = false)
  private FarmerEntity farmer;

  @Column(name = "invoice_number", nullable = false, length = 40)
  private String invoiceNumber;

  @Column(name = "invoice_date", nullable = false)
  private LocalDate invoiceDate;

  @Column(name = "due_date", nullable = false)
  private LocalDate dueDate;

  @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal totalAmount;

  @Column(name = "status", nullable = false, length = 20)
  private String status = "ISSUED";

  @Column(name = "notes", columnDefinition = "TEXT")
  private String notes;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "created_by", nullable = false)
  private AppUserEntity createdBy;

  @Column(name = "created_at", insertable = false, updatable = false)
  private LocalDateTime createdAt;

  protected InvoiceEntity() {}

  public Long getId() {
    return id;
  }

  public FamilyEntity getFamily() {
    return family;
  }

  public void setFamily(FamilyEntity family) {
    this.family = family;
  }

  public FarmerEntity getFarmer() {
    return farmer;
  }

  public void setFarmer(FarmerEntity farmer) {
    this.farmer = farmer;
  }

  public String getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  public LocalDate getInvoiceDate() {
    return invoiceDate;
  }

  public void setInvoiceDate(LocalDate invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public AppUserEntity getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(AppUserEntity createdBy) {
    this.createdBy = createdBy;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
