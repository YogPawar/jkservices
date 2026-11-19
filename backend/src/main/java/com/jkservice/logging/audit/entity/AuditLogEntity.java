package com.jkservice.logging.audit.entity;

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
    name = "audit_logs",
    indexes = {
      @Index(name = "idx_audit_org_time", columnList = "organization_id, created_at"),
      @Index(name = "idx_audit_business_time", columnList = "business_id, created_at"),
      @Index(name = "idx_audit_business_org", columnList = "business_id, organization_id"),
      @Index(name = "idx_audit_entity", columnList = "entity_type, entity_id"),
      @Index(name = "idx_audit_actor_time", columnList = "actor_user_id, created_at")
    })
public class AuditLogEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "organization_id", nullable = false)
  private Long organizationId;

  @Column(name = "business_id")
  private Long businessId;

  @Column(name = "actor_user_id")
  private Long actorUserId;

  @Column(name = "action", length = 100, nullable = false)
  private String action;

  @Column(name = "entity_type", length = 100, nullable = false)
  private String entityType;

  @Column(name = "entity_id", length = 100)
  private String entityId;

  @Column(name = "details", columnDefinition = "json")
  private String details;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;
}
