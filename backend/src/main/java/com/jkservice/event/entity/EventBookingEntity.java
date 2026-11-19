package com.jkservice.event.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(name = "event_bookings")
public class EventBookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "business_id", nullable = false)
    private Long businessId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "booking_number", length = 50, nullable = false)
    private String bookingNumber;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "service_description", columnDefinition = "text")
    private String serviceDescription;

    @Column(name = "agreed_amount", precision = 14, scale = 2, nullable = false)
    private BigDecimal agreedAmount;

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
