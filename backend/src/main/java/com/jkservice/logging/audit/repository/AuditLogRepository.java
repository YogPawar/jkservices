package com.jkservice.logging.audit.repository;

import com.jkservice.logging.audit.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {}
