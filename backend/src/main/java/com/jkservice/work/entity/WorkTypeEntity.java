package com.jkservice.work.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "work_types",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_work_types_business_name",
          columnNames = {"business_id", "name"}),
      @UniqueConstraint(
          name = "uk_work_types_id_business",
          columnNames = {"id", "business_id"})
    },
    indexes = {@Index(name = "idx_work_types_business_active", columnList = "business_id, active")})
public class WorkTypeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "business_id", nullable = false)
  private Long businessId;

  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @Column(name = "active", nullable = false)
  private boolean active;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime updatedAt;
}
