package com.jkservice.farmer.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.jkservice.business.entity.BusinessEntity;
import com.jkservice.customer.entity.CustomerEntity;
import com.jkservice.farmer.entity.FarmerEntity;
import com.jkservice.organization.entity.OrganizationEntity;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class FarmerRepositoryIntegrationTest {

  @Container static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4");

  @Autowired private FarmerRepository farmerRepository;

  @Autowired private EntityManager entityManager;

  @DynamicPropertySource
  static void configureDatasource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
    registry.add("spring.datasource.username", MYSQL::getUsername);
    registry.add("spring.datasource.password", MYSQL::getPassword);
  }

  @Test
  void shouldPersistAndRetrieveFarmerWithinOwningBusiness() {

    BusinessEntity business = createBusiness("Business One", "ORG-001", "BUS-001");
    CustomerEntity customer = createCustomer(business, "C-001", "Ramesh");

    FarmerEntity farmer = createFarmer(business, customer, "F-001", "Pune");

    FarmerEntity savedFarmer = farmerRepository.saveAndFlush(farmer);

    assertThat(savedFarmer.getId()).isNotNull();

    var result = farmerRepository.findByIdAndBusinessId(savedFarmer.getId(), business.getId());

    assertThat(result).isPresent();
    assertThat(result.get().getFarmerCode()).isEqualTo("F-001");
    assertThat(result.get().getCustomerId()).isEqualTo(customer.getId());
    assertThat(result.get().getVillage()).isEqualTo("Pune");

    CustomerEntity persistedCustomer =
        entityManager.find(CustomerEntity.class, result.get().getCustomerId());

    assertThat(persistedCustomer.getName()).isEqualTo("Ramesh");
  }

  @Test
  void shouldNotFindFarmerWhenRequestedByAnotherBusiness() {

    BusinessEntity ownerBusiness = createBusiness("Owner Business", "ORG-002", "BUS-002");

    BusinessEntity anotherBusiness = createBusiness("Another Business", "ORG-003", "BUS-003");

    CustomerEntity customer = createCustomer(ownerBusiness, "C-002", "Suresh");

    FarmerEntity farmer =
        farmerRepository.saveAndFlush(createFarmer(ownerBusiness, customer, "F-002", "Nashik"));

    var result = farmerRepository.findByIdAndBusinessId(farmer.getId(), anotherBusiness.getId());

    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnOnlyFarmersBelongingToRequestedBusiness() {

    BusinessEntity businessOne = createBusiness("Business Alpha", "ORG-004", "BUS-004");

    BusinessEntity businessTwo = createBusiness("Business Beta", "ORG-005", "BUS-005");

    CustomerEntity customerOne = createCustomer(businessOne, "C-003", "Mahesh");

    CustomerEntity customerTwo = createCustomer(businessOne, "C-004", "Ganesh");

    CustomerEntity customerThree = createCustomer(businessTwo, "C-005", "Dinesh");

    farmerRepository.saveAndFlush(createFarmer(businessOne, customerOne, "F-003", "Pune"));

    farmerRepository.saveAndFlush(createFarmer(businessOne, customerTwo, "F-004", "Satara"));

    farmerRepository.saveAndFlush(createFarmer(businessTwo, customerThree, "F-005", "Nashik"));

    List<FarmerEntity> farmers = farmerRepository.findAllByBusinessId(businessOne.getId());

    assertThat(farmers)
        .hasSize(2)
        .extracting(FarmerEntity::getFarmerCode)
        .containsExactlyInAnyOrder("F-003", "F-004");
  }

  private BusinessEntity createBusiness(
      String businessName, String inviteCode, String businessCode) {

    OrganizationEntity organization = new OrganizationEntity();
    organization.setOrganizationName(businessName + " Organization");
    organization.setInviteCode(inviteCode);
    organization.setActive(true);

    entityManager.persist(organization);
    entityManager.flush();

    BusinessEntity business = new BusinessEntity();
    business.setOrganizationId(organization.getId());
    business.setBusinessName(businessName);
    business.setBusinessType("FARMING_SERVICES");
    business.setBusinessCode(businessCode);
    business.setCurrencyCode("INR");
    business.setActive(true);

    entityManager.persist(business);
    entityManager.flush();

    return business;
  }

  private CustomerEntity createCustomer(BusinessEntity business, String customerCode, String name) {

    CustomerEntity customer = new CustomerEntity();
    customer.setBusinessId(business.getId());
    customer.setCustomerCode(customerCode);
    customer.setName(name);
    customer.setActive(true);

    entityManager.persist(customer);
    entityManager.flush();

    return customer;
  }

  private FarmerEntity createFarmer(
      BusinessEntity business, CustomerEntity customer, String farmerCode, String village) {

    FarmerEntity farmer = new FarmerEntity();
    farmer.setBusinessId(business.getId());
    farmer.setCustomerId(customer.getId());
    farmer.setFarmerCode(farmerCode);
    farmer.setVillage(village);

    return farmer;
  }
}
