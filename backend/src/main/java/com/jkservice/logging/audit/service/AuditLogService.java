package com.jkservice.logging.audit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jkservice.logging.audit.entity.AuditLogEntity;
import com.jkservice.logging.audit.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

  private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

  private final AuditLogRepository repository;
  private final ObjectMapper objectMapper;

  public AuditLogService(AuditLogRepository repository, ObjectMapper objectMapper) {
    this.repository = repository;
    this.objectMapper = objectMapper;
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public void record(
      Long organizationId,
      Long businessId,
      Long actorUserId,
      String action,
      String entityType,
      String entityId,
      Object details) {

    if (organizationId == null) {
      throw new IllegalArgumentException("Organization ID is required for audit logging");
    }

    if (action == null || action.isBlank()) {
      throw new IllegalArgumentException("Audit action is required");
    }

    if (entityType == null || entityType.isBlank()) {
      throw new IllegalArgumentException("Audit entity type is required");
    }

    try {
      AuditLogEntity auditLog = new AuditLogEntity();

      auditLog.setOrganizationId(organizationId);
      auditLog.setBusinessId(businessId);
      auditLog.setActorUserId(actorUserId);
      auditLog.setAction(action);
      auditLog.setEntityType(entityType);
      auditLog.setEntityId(entityId);
      auditLog.setDetails(details == null ? null : objectMapper.writeValueAsString(details));

      repository.save(auditLog);

      log.info(
          "Audit event persisted: action={}, entityType={}, entityId={}, organizationId={}, businessId={}",
          action,
          entityType,
          entityId,
          organizationId,
          businessId);

    } catch (JsonProcessingException exception) {
      log.error(
          "Failed to serialize audit details: action={}, entityType={}, entityId={}",
          action,
          entityType,
          entityId,
          exception);

      throw new IllegalStateException("Unable to serialize audit log details", exception);
    } catch (RuntimeException exception) {
      log.error(
          "Failed to persist audit event: action={}, entityType={}, entityId={}, organizationId={}",
          action,
          entityType,
          entityId,
          organizationId,
          exception);

      throw exception;
    }
  }
}
