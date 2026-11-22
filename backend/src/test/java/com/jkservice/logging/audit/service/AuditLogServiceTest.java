package com.jkservice.logging.audit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jkservice.logging.audit.entity.AuditLogEntity;
import com.jkservice.logging.audit.repository.AuditLogRepository;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

  @Mock private AuditLogRepository repository;

  private ObjectMapper objectMapper;

  private AuditLogService service;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    service = new AuditLogService(repository, objectMapper);
  }

  @Test
  void shouldPersistAuditLogWithStructuredDetails() throws Exception {

    Map<String, Object> details = Map.of("businessName", "Pawar Farming", "active", true);

    service.record(10L, 20L, 30L, "BUSINESS_CREATED", "BUSINESS", "100", details);

    ArgumentCaptor<AuditLogEntity> captor = ArgumentCaptor.forClass(AuditLogEntity.class);

    verify(repository).save(captor.capture());

    AuditLogEntity saved = captor.getValue();

    assertEquals(10L, saved.getOrganizationId());
    assertEquals(20L, saved.getBusinessId());
    assertEquals(30L, saved.getActorUserId());
    assertEquals("BUSINESS_CREATED", saved.getAction());
    assertEquals("BUSINESS", saved.getEntityType());
    assertEquals("100", saved.getEntityId());

    assertEquals(
        "Pawar Farming", objectMapper.readTree(saved.getDetails()).get("businessName").asText());

    assertTrue(objectMapper.readTree(saved.getDetails()).get("active").asBoolean());
  }

  @Test
  void shouldAllowNullBusinessAndActorForOrganizationLevelEvents() {

    service.record(10L, null, null, "ORGANIZATION_CREATED", "ORGANIZATION", "10", null);

    ArgumentCaptor<AuditLogEntity> captor = ArgumentCaptor.forClass(AuditLogEntity.class);

    verify(repository).save(captor.capture());

    assertNull(captor.getValue().getBusinessId());
    assertNull(captor.getValue().getActorUserId());
    assertNull(captor.getValue().getDetails());
  }

  @Test
  void shouldRejectMissingOrganizationId() {

    assertThrows(
        IllegalArgumentException.class,
        () -> service.record(null, 20L, 30L, "BUSINESS_CREATED", "BUSINESS", "100", null));

    verifyNoInteractions(repository);
  }

  @Test
  void shouldRejectBlankAction() {

    assertThrows(
        IllegalArgumentException.class,
        () -> service.record(10L, null, null, " ", "BUSINESS", "100", null));

    verifyNoInteractions(repository);
  }

  @Test
  void shouldRejectBlankEntityType() {

    assertThrows(
        IllegalArgumentException.class,
        () -> service.record(10L, null, null, "BUSINESS_CREATED", " ", "100", null));

    verifyNoInteractions(repository);
  }

  @Test
  void shouldPropagateRepositoryFailure() {

    when(repository.save(any(AuditLogEntity.class)))
        .thenThrow(new RuntimeException("Database unavailable"));

    assertThrows(
        RuntimeException.class,
        () -> service.record(10L, 20L, 30L, "BUSINESS_CREATED", "BUSINESS", "100", null));
  }
}
