package com.jkservice.invoice.entity;

import com.jkservice.family.entity.FamilyEntity;
import com.jkservice.work.domain.WorkEntryEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "invoice_items",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_invoice_work",
          columnNames = {"invoice_id", "work_entry_id"}),
      @UniqueConstraint(
          name = "uq_work_billed",
          columnNames = {"work_entry_id"})
    })
public class InvoiceItemEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "family_id", nullable = false)
  private FamilyEntity family;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "invoice_id", nullable = false)
  private InvoiceEntity invoice;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "work_entry_id", nullable = false)
  private WorkEntryEntity workEntry;

  @Column(name = "description", length = 255)
  private String description;

  @Column(name = "amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;
}
