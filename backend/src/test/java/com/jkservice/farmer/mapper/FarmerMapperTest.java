package com.jkservice.farmer.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.jkservice.family.entity.FamilyEntity;
import com.jkservice.farmer.dto.CreateFarmerRequest;
import com.jkservice.farmer.dto.FarmerResponse;
import com.jkservice.farmer.dto.UpdateFarmerRequest;
import com.jkservice.farmer.entity.FarmerEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FarmerMapperTest {

  private FarmerMapper farmerMapper;
  private FamilyEntity family;

  @BeforeEach
  void setUp() {
    farmerMapper = new FarmerMapper();

    family = new FamilyEntity();
    family.setId(1L);
    family.setFamilyName("Pawar Family");
    family.setActive(true);
  }

  @Test
  void shouldMapCreateRequestToFarmerEntity() {

    CreateFarmerRequest request =
        new CreateFarmerRequest(
            "  Yogesh Pawar  ", " 9876543210 ", "  Pune  ", "  Maharashtra  ", true);

    FarmerEntity farmer = farmerMapper.toEntity(request, family, "FAM001");

    assertAll(
        () -> assertNull(farmer.getId()),
        () -> assertSame(family, farmer.getFamily()),
        () -> assertEquals("FAM001", farmer.getFarmerCode()),
        () -> assertEquals("Yogesh Pawar", farmer.getName()),
        () -> assertEquals("9876543210", farmer.getPhone()),
        () -> assertEquals("Pune", farmer.getVillage()),
        () -> assertEquals("Maharashtra", farmer.getAddress()),
        () -> assertTrue(farmer.isActive()));
  }

  @Test
  void shouldNormalizeNullAndBlankOptionalFieldsDuringCreate() {

    CreateFarmerRequest request = new CreateFarmerRequest("Yogesh Pawar", null, "   ", null, true);

    FarmerEntity farmer = farmerMapper.toEntity(request, family, "FAM002");

    assertAll(
        () -> assertNull(farmer.getPhone()),
        () -> assertNull(farmer.getVillage()),
        () -> assertNull(farmer.getAddress()));
  }

  @Test
  void shouldMapUpdateRequestToExistingFarmerEntity() {

    FarmerEntity farmer = new FarmerEntity();
    farmer.setFamily(family);
    farmer.setFarmerCode("FAM003");
    farmer.setName("Old Name");
    farmer.setPhone("9999999999");
    farmer.setVillage("Old Village");
    farmer.setAddress("Old Address");
    farmer.setActive(false);

    UpdateFarmerRequest request =
        new UpdateFarmerRequest(
            "  Updated Name  ", " 8888888888 ", "  Updated Village ", " Updated Address ", true);

    farmerMapper.updateEntity(request, farmer);

    assertAll(
        () -> assertSame(family, farmer.getFamily()),
        () -> assertEquals("FAM003", farmer.getFarmerCode()),
        () -> assertEquals("Updated Name", farmer.getName()),
        () -> assertEquals("8888888888", farmer.getPhone()),
        () -> assertEquals("Updated Village", farmer.getVillage()),
        () -> assertEquals("Updated Address", farmer.getAddress()),
        () -> assertTrue(farmer.isActive()));
  }

  @Test
  void shouldNormalizeNullAndBlankOptionalFieldsDuringUpdate() {

    FarmerEntity farmer = new FarmerEntity();
    farmer.setFamily(family);
    farmer.setFarmerCode("FAM004");
    farmer.setName("Existing Name");

    UpdateFarmerRequest request = new UpdateFarmerRequest("Updated Name", null, "   ", null, true);

    farmerMapper.updateEntity(request, farmer);

    assertAll(
        () -> assertNull(farmer.getPhone()),
        () -> assertNull(farmer.getVillage()),
        () -> assertNull(farmer.getAddress()));
  }

  @Test
  void shouldMapFarmerEntityToResponse() {

    FarmerEntity farmer = new FarmerEntity();
    farmer.setFamily(family);
    farmer.setFarmerCode("FAM005");
    farmer.setName("Yogesh Pawar");
    farmer.setPhone("9876543210");
    farmer.setVillage("Pune");
    farmer.setAddress("Maharashtra");
    farmer.setActive(true);

    FarmerResponse response = farmerMapper.toResponse(farmer);

    assertAll(
        () -> assertNull(response.id()),
        () -> assertEquals("FAM005", response.farmerCode()),
        () -> assertEquals("Yogesh Pawar", response.name()),
        () -> assertEquals("9876543210", response.phone()),
        () -> assertEquals("Pune", response.village()),
        () -> assertEquals("Maharashtra", response.address()),
        () -> assertTrue(response.active()),
        () -> assertNull(response.createdAt()));
  }
}
