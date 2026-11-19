package com.jkservice.event.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "events")
public class EventEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "business_id", nullable = false)
  private Long businessId;

  @Column(name = "event_code", length = 50, nullable = false)
  private String eventCode;

  @Column(name = "event_name", length = 180, nullable = false)
  private String eventName;

  @Column(name = "event_type", length = 100)
  private String eventType;

  @Column(name = "event_date", nullable = false)
  private LocalDate eventDate;

  @Column(name = "end_date")
  private LocalDate endDate;

  @Column(name = "venue", length = 255)
  private String venue;

  @Column(name = "expected_guests")
  private Integer expectedGuests;

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
