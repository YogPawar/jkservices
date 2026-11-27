  package com.jkservice.logging.audit.service;

  import static org.assertj.core.api.Assertions.assertThat;
  import static org.assertj.core.api.Assertions.assertThatThrownBy;

  import com.fasterxml.jackson.databind.ObjectMapper;
  import com.jkservice.user.entity.UserEntity;
  import com.jkservice.business.entity.BusinessEntity;
  import com.jkservice.business.mapper.BusinessMapper;
  import com.jkservice.business.service.impl.BusinessServiceImpl;
  import com.jkservice.logging.audit.entity.AuditLogEntity;
  import com.jkservice.logging.audit.repository.AuditLogRepository;
  import com.jkservice.organization.entity.OrganizationEntity;
  import jakarta.persistence.EntityManager;
  import java.util.Map;
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
  class AuditLogIntegrationTest {

    @Container static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4");

    @Autowired private EntityManager entityManager;

    @Autowired private AuditLogRepository auditLogRepository;

    @Autowired private AuditLogService auditLogService;

    @Autowired private ObjectMapper objectMapper;

    @Autowired private PlatformTransactionManager transactionManager;

    @Autowired private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
      registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
      registry.add("spring.datasource.username", MYSQL::getUsername);
      registry.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Test
    void shouldPersistAuditLogWithJsonDetailsAndTenantReferences() throws Exception {

      OrganizationEntity organization = createOrganization("Audit Organization", "AUD-001");

      UserEntity actor = createUser("audit-user@example.com");

      BusinessEntity business = createBusiness(organization, "Audit Business", "AUD-BUS-001");

      Map<String, Object> details =
          Map.of("businessName", "Audit Business", "changes", Map.of("currencyCode", "INR"));

      auditLogService.record(
          organization.getId(),
          business.getId(),
          actor.getId(),
          "BUSINESS_CREATED",
          "BUSINESS",
          business.getId().toString(),
          details);

      entityManager.flush();
      entityManager.clear();

      AuditLogEntity savedAuditLog =
          auditLogRepository.findAll().stream()
              .filter(log -> "BUSINESS_CREATED".equals(log.getAction()))
              .findFirst()
              .orElseThrow();

      assertThat(savedAuditLog.getId()).isNotNull();
      assertThat(savedAuditLog.getOrganizationId()).isEqualTo(organization.getId());
      assertThat(savedAuditLog.getBusinessId()).isEqualTo(business.getId());
      assertThat(savedAuditLog.getActorUserId()).isEqualTo(actor.getId());
      assertThat(savedAuditLog.getEntityType()).isEqualTo("BUSINESS");
      assertThat(savedAuditLog.getEntityId()).isEqualTo(business.getId().toString());
      assertThat(savedAuditLog.getCreatedAt()).isNotNull();

      var persistedDetails = objectMapper.readTree(savedAuditLog.getDetails());

      assertThat(persistedDetails.path("businessName").asText()).isEqualTo("Audit Business");

      assertThat(persistedDetails.path("changes").path("currencyCode").asText()).isEqualTo("INR");
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldRollbackBusinessMutationWhenAuditPersistenceFails() {

      TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

      transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

      assertThatThrownBy(() ->
              transactionTemplate.execute(status -> {
                OrganizationEntity organization =
                    createOrganization("Rollback Organization", "ROLL-001");

                BusinessEntity business =
                    createBusiness(organization, "Business To Roll Back", "ROLL-BUS-001");

                // Deliberately use a non-existent business ID.
                // The audit_logs composite foreign key must reject it.
                auditLogService.record(
                    organization.getId(),
                    Long.MAX_VALUE,
                    null,
                    "BUSINESS_CREATED",
                    "BUSINESS",
                    business.getId().toString(),
                    null);

                // Force the database constraint to be checked now.
                entityManager.flush();

                return null;
              }))
          .isInstanceOf(RuntimeException.class);

      Integer remainingBusinesses =
          jdbcTemplate.queryForObject(
              "SELECT COUNT(*) FROM businesses WHERE business_code = ?",
              Integer.class,
              "ROLL-BUS-001");

      Integer remainingOrganizations =
          jdbcTemplate.queryForObject(
              "SELECT COUNT(*) FROM organizations WHERE invite_code = ?", Integer.class, "ROLL-001");

      assertThat(remainingBusinesses).isZero();
      assertThat(remainingOrganizations).isZero();
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

    private UserEntity createUser(String email) {

      UserEntity user = new UserEntity();
      user.setFullName("Audit Test User");
      user.setEmail(email);
      user.setPasswordHash("test-password-hash");
      user.setActive(true);

      entityManager.persist(user);
      entityManager.flush();

      return user;
    }

    private BusinessEntity createBusiness(OrganizationEntity organization, String name, String code) {

      BusinessEntity business = new BusinessEntity();
      business.setOrganizationId(organization.getId());
      business.setBusinessName(name);
      business.setBusinessType("FARMING_SERVICES");
      business.setBusinessCode(code);
      business.setCurrencyCode("INR");
      business.setActive(true);

      entityManager.persist(business);
      entityManager.flush();

      return business;
    }
  }
