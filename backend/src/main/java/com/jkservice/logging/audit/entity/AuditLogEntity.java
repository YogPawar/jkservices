package com.jkservice.logging.audit.entity;

import com.jkservice.auth.entity.AppUserEntity;
import com.jkservice.family.entity.FamilyEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "audit_logs",
    indexes = {@Index(name = "idx_audit_family_time", columnList = "family_id, created_at")})
public class AuditLogEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "family_id", nullable = false)
  private FamilyEntity family;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "actor_user_id")
  private AppUserEntity actorUser;

  @Column(name = "action", nullable = false, length = 40)
  private String action;

  @Column(name = "entity_type", nullable = false, length = 60)
  private String entityType;

  @Column(name = "entity_id")
  private Long entityId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "details", columnDefinition = "json")
  private Map<String, Object> details;

  @Column(name = "created_at", insertable = false, updatable = false)
  private LocalDateTime createdAt;
}
