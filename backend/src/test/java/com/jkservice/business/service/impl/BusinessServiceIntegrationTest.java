package com.jkservice.business.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jkservice.business.dto.request.CreateBusinessRequest;
import com.jkservice.business.dto.request.UpdateBusinessRequest;
import com.jkservice.business.dto.response.BusinessResponse;
import com.jkservice.business.entity.BusinessEntity;
import com.jkservice.business.enums.BusinessType;
import com.jkservice.business.mapper.BusinessMapper;
import com.jkservice.business.repository.BusinessRepository;
import com.jkservice.business.service.BusinessService;
import com.jkservice.logging.audit.entity.AuditLogEntity;
import com.jkservice.logging.audit.repository.AuditLogRepository;
import com.jkservice.logging.audit.service.AuditLogService;
import com.jkservice.organization.entity.OrganizationEntity;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import({
  BusinessServiceImpl.class,
  BusinessMapper.class,
  AuditLogService.class,
  JacksonAutoConfiguration.class
})
class BusinessServiceIntegrationTest {

  @Container static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4");

  @Autowired private EntityManager entityManager;

  @Autowired private BusinessService businessService;

  @Autowired private BusinessRepository businessRepository;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private PlatformTransactionManager transactionManager;

  @Autowired private JdbcTemplate jdbcTemplate;

  @MockitoSpyBean private AuditLogRepository auditLogRepository;

  @DynamicPropertySource
  static void configureDatasource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
    registry.add("spring.datasource.username", MYSQL::getUsername);
    registry.add("spring.datasource.password", MYSQL::getPassword);
  }

  @Test
  void shouldCreateBusinessAndPersistCreatedAuditLog() throws Exception {

    OrganizationEntity organization =
        createOrganization("Business Create Organization", "BC-ORG-001");

    CreateBusinessRequest request = createRequest("Pawar Farming", "pawar-001");

    BusinessResponse response = businessService.createBusiness(organization.getId(), request);

    entityManager.flush();
    entityManager.clear();

    BusinessEntity savedBusiness =
        businessRepository
            .findByIdAndOrganizationId(response.id(), organization.getId())
            .orElseThrow();

    assertThat(savedBusiness.getId()).isNotNull();
    assertThat(savedBusiness.getBusinessName()).isEqualTo("Pawar Farming");
    assertThat(savedBusiness.getBusinessType()).isEqualTo("FARMING_SERVICES");
    assertThat(savedBusiness.getBusinessCode()).isEqualTo("PAWAR-001");
    assertThat(savedBusiness.getCurrencyCode()).isEqualTo("INR");
    assertThat(savedBusiness.isActive()).isTrue();

    AuditLogEntity auditLog = findAuditLog(savedBusiness.getId(), "BUSINESS_CREATED");

    assertThat(auditLog.getOrganizationId()).isEqualTo(organization.getId());
    assertThat(auditLog.getBusinessId()).isEqualTo(savedBusiness.getId());
    assertThat(auditLog.getActorUserId()).isNull();
    assertThat(auditLog.getEntityType()).isEqualTo("BUSINESS");
    assertThat(auditLog.getEntityId()).isEqualTo(savedBusiness.getId().toString());
    assertThat(auditLog.getCreatedAt()).isNotNull();

    JsonNode details = objectMapper.readTree(auditLog.getDetails());

    assertThat(details.path("businessName").asText()).isEqualTo("Pawar Farming");
    assertThat(details.path("businessType").asText()).isEqualTo("FARMING_SERVICES");
    assertThat(details.path("businessCode").asText()).isEqualTo("PAWAR-001");
    assertThat(details.path("currencyCode").asText()).isEqualTo("INR");
  }

  @Test
  void shouldUpdateBusinessAndPersistBeforeAndAfterAuditSnapshots() throws Exception {

    OrganizationEntity organization =
        createOrganization("Business Update Organization", "BU-ORG-001");

    BusinessResponse createdBusiness =
        businessService.createBusiness(
            organization.getId(), createRequest("Pawar Farming", "pawar-001"));

    UpdateBusinessRequest updateRequest =
        new UpdateBusinessRequest(
            "Updated Pawar Farming",
            BusinessType.FARMING_SERVICES,
            "pawar-002",
            "9876543210",
            "updated@pawarfarming.com",
            "Pune, Maharashtra",
            "USD");

    BusinessResponse updatedResponse =
        businessService.updateBusiness(organization.getId(), createdBusiness.id(), updateRequest);

    entityManager.flush();
    entityManager.clear();

    BusinessEntity updatedBusiness =
        businessRepository
            .findByIdAndOrganizationId(updatedResponse.id(), organization.getId())
            .orElseThrow();

    assertThat(updatedBusiness.getBusinessName()).isEqualTo("Updated Pawar Farming");
    assertThat(updatedBusiness.getBusinessCode()).isEqualTo("PAWAR-002");
    assertThat(updatedBusiness.getCurrencyCode()).isEqualTo("USD");

    AuditLogEntity auditLog = findAuditLog(updatedBusiness.getId(), "BUSINESS_UPDATED");

    JsonNode details = objectMapper.readTree(auditLog.getDetails());

    JsonNode before = details.path("before");
    JsonNode after = details.path("after");

    assertThat(before.path("businessName").asText()).isEqualTo("Pawar Farming");
    assertThat(before.path("businessCode").asText()).isEqualTo("PAWAR-001");
    assertThat(before.path("currencyCode").asText()).isEqualTo("INR");

    assertThat(after.path("businessName").asText()).isEqualTo("Updated Pawar Farming");
    assertThat(after.path("businessCode").asText()).isEqualTo("PAWAR-002");
    assertThat(after.path("currencyCode").asText()).isEqualTo("USD");
  }

  @Test
  void shouldDeactivateBusinessAndPersistDeactivatedAuditLog() throws Exception {

    OrganizationEntity organization =
        createOrganization("Business Deactivate Organization", "BD-ORG-001");

    BusinessResponse createdBusiness =
        businessService.createBusiness(
            organization.getId(), createRequest("Pawar Farming", "pawar-001"));

    businessService.deactivateBusiness(organization.getId(), createdBusiness.id());

    entityManager.flush();
    entityManager.clear();

    BusinessEntity deactivatedBusiness =
        businessRepository
            .findByIdAndOrganizationId(createdBusiness.id(), organization.getId())
            .orElseThrow();

    assertThat(deactivatedBusiness.isActive()).isFalse();

    AuditLogEntity auditLog = findAuditLog(deactivatedBusiness.getId(), "BUSINESS_DEACTIVATED");

    JsonNode details = objectMapper.readTree(auditLog.getDetails());

    assertThat(details.path("businessName").asText()).isEqualTo("Pawar Farming");
    assertThat(details.path("businessCode").asText()).isEqualTo("PAWAR-001");
    assertThat(details.path("previousActive").asBoolean()).isTrue();
    assertThat(details.path("active").asBoolean()).isFalse();
  }

  @Test
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  void shouldRollbackBusinessCreationWhenAuditLoggingFails() {

    doThrow(new IllegalStateException("Simulated audit persistence failure"))
        .when(auditLogRepository)
        .save(any(AuditLogEntity.class));

    TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

    transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

    assertThatThrownBy(() ->
            transactionTemplate.execute(status -> {
              OrganizationEntity organization =
                  createOrganization("Rollback Organization", "ROLL-BUS-001");

              businessService.createBusiness(
                  organization.getId(), createRequest("Business To Roll Back", "ROLL-BUS-001"));

              return null;
            }))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Simulated audit persistence failure");

    Integer remainingBusinesses =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM businesses WHERE business_code = ?",
            Integer.class,
            "ROLL-BUS-001");

    Integer remainingOrganizations =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM organizations WHERE invite_code = ?",
            Integer.class,
            "ROLL-BUS-001");

    assertThat(remainingBusinesses).isZero();
    assertThat(remainingOrganizations).isZero();
  }

  private CreateBusinessRequest createRequest(String name, String code) {
    return new CreateBusinessRequest(
        name,
        BusinessType.FARMING_SERVICES,
        code,
        "9876543210",
        "contact@pawarfarming.com",
        "Pune, Maharashtra",
        null);
  }

  private OrganizationEntity createOrganization(String name, String inviteCode) {
    OrganizationEntity organization = new OrganizationEntity();

    organization.setOrganizationName(name);
    organization.setInviteCode(inviteCode);
    organization.setActive(true);

    entityManager.persist(organization);
    entityManager.flush();

    return organization;
  }

  private AuditLogEntity findAuditLog(Long businessId, String action) {
    List<AuditLogEntity> matchingLogs =
        auditLogRepository.findAll().stream()
            .filter(log -> businessId.equals(log.getBusinessId()))
            .filter(log -> action.equals(log.getAction()))
            .toList();

    assertThat(matchingLogs)
        .as("Expected exactly one %s audit log for business %s", action, businessId)
        .hasSize(1);

    return matchingLogs.get(0);
  }
}
