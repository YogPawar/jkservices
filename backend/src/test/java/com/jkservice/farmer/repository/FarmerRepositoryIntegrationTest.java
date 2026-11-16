package com.jkservice.farmer.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.jkservice.family.entity.FamilyEntity;
import com.jkservice.family.repository.FamilyRepository;
import com.jkservice.farmer.entity.FarmerEntity;
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

  @Autowired private FamilyRepository familyRepository;

  @DynamicPropertySource
  static void configureDatasource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
    registry.add("spring.datasource.username", MYSQL::getUsername);
    registry.add("spring.datasource.password", MYSQL::getPassword);
  }

  @Test
  void shouldPersistAndRetrieveFarmerWithinOwningFamily() {

    FamilyEntity family = createFamily("Family One", "INVITE-001");
    FarmerEntity farmer = createFarmer(family, "F-001", "Ramesh");

    FarmerEntity savedFarmer = farmerRepository.saveAndFlush(farmer);

    assertThat(savedFarmer.getId()).isNotNull();

    var result = farmerRepository.findByIdAndFamily_Id(savedFarmer.getId(), family.getId());

    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Ramesh");
    assertThat(result.get().getFarmerCode()).isEqualTo("F-001");
  }

  @Test
  void shouldNotFindFarmerWhenRequestedByAnotherFamily() {

    FamilyEntity ownerFamily = createFamily("Owner Family", "INVITE-002");
    FamilyEntity anotherFamily = createFamily("Another Family", "INVITE-003");

    FarmerEntity farmer =
        farmerRepository.saveAndFlush(createFarmer(ownerFamily, "F-002", "Suresh"));

    var result = farmerRepository.findByIdAndFamily_Id(farmer.getId(), anotherFamily.getId());

    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnOnlyFarmersBelongingToRequestedFamily() {

    FamilyEntity familyOne = createFamily("Family Alpha", "INVITE-004");
    FamilyEntity familyTwo = createFamily("Family Beta", "INVITE-005");

    farmerRepository.saveAndFlush(createFarmer(familyOne, "F-003", "Mahesh"));

    farmerRepository.saveAndFlush(createFarmer(familyOne, "F-004", "Ganesh"));

    farmerRepository.saveAndFlush(createFarmer(familyTwo, "F-005", "Dinesh"));

    List<FarmerEntity> farmers = farmerRepository.findAllByFamily_Id(familyOne.getId());

    assertThat(farmers)
        .hasSize(2)
        .extracting(FarmerEntity::getName)
        .containsExactlyInAnyOrder("Mahesh", "Ganesh");
  }

  private FamilyEntity createFamily(String name, String inviteCode) {
    FamilyEntity family = new FamilyEntity();
    family.setFamilyName(name);
    family.setInviteCode(inviteCode);
    family.setActive(true);

    return familyRepository.saveAndFlush(family);
  }

  private FarmerEntity createFarmer(FamilyEntity family, String farmerCode, String name) {

    FarmerEntity farmer = new FarmerEntity();
    farmer.setFamily(family);
    farmer.setFarmerCode(farmerCode);
    farmer.setName(name);
    farmer.setActive(true);

    return farmer;
  }
}
