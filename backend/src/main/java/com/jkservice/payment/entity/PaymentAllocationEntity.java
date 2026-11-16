package com.jkservice.payment.entity;

import com.jkservice.family.entity.FamilyEntity;
import com.jkservice.invoice.entity.InvoiceEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(
        name = "payment_allocations",
        indexes = {
                @Index(name = "idx_alloc_invoice", columnList = "invoice_id"),
                @Index(name = "idx_alloc_payment", columnList = "payment_id")
        }
)
public class PaymentAllocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "family_id", nullable = false)
    private FamilyEntity family;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentEntity payment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private InvoiceEntity invoice;

    @Column(name = "allocated_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal allocatedAmount;

    protected PaymentAllocationEntity() {}

    public Long getId() { return id; }
    public FamilyEntity getFamily() { return family; }
    public void setFamily(FamilyEntity family) { this.family = family; }
    public PaymentEntity getPayment() { return payment; }
    public void setPayment(PaymentEntity payment) { this.payment = payment; }
    public InvoiceEntity getInvoice() { return invoice; }
    public void setInvoice(InvoiceEntity invoice) { this.invoice = invoice; }
    public BigDecimal getAllocatedAmount() { return allocatedAmount; }
    public void setAllocatedAmount(BigDecimal allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }
}