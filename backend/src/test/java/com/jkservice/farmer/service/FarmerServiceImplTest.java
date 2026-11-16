package com.jkservice.farmer.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.jkservice.common.exception.BusinessException;
import com.jkservice.common.exception.ResourceNotFoundException;
import com.jkservice.family.entity.FamilyEntity;
import com.jkservice.family.repository.FamilyRepository;
import com.jkservice.farmer.dto.CreateFarmerRequest;
import com.jkservice.farmer.dto.FarmerResponse;
import com.jkservice.farmer.entity.FarmerEntity;
import com.jkservice.farmer.mapper.FarmerMapper;
import com.jkservice.farmer.repository.FarmerRepository;
import com.jkservice.farmer.service.impl.FarmerServiceImpl;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FarmerServiceImplTest {

  @Mock private FarmerRepository farmerRepository;

  @Mock private FamilyRepository familyRepository;

  @Mock private FarmerMapper farmerMapper;

  @Mock private FarmerCodeGenerator farmerCodeGenerator;

  @InjectMocks private FarmerServiceImpl farmerService;

  private FamilyEntity family;
  private CreateFarmerRequest request;

  @BeforeEach
  void setUp() {
    family = new FamilyEntity();
    family.setId(10L);
    family.setFamilyName("Pawar Family");
    family.setInviteCode("INVITE-001");
    family.setActive(true);

    request = new CreateFarmerRequest("Ramesh", "9876543210", "Pune", "Farm address", true);
  }

  @Test
  void shouldCreateFarmerSuccessfully() {

    String farmerCode = "F-K7M4X9P2Q8RT";
    FarmerEntity farmer = new FarmerEntity();

    FarmerResponse expectedResponse =
        new FarmerResponse(
            100L,
            farmerCode,
            "Ramesh",
            "9876543210",
            "Pune",
            "Farm address",
            true,
            LocalDateTime.now());

    when(familyRepository.findById(10L)).thenReturn(Optional.of(family));

    when(farmerCodeGenerator.generate()).thenReturn(farmerCode);

    when(farmerMapper.toEntity(request, family, farmerCode)).thenReturn(farmer);

    when(farmerRepository.save(farmer)).thenReturn(farmer);

    when(farmerMapper.toResponse(farmer)).thenReturn(expectedResponse);

    FarmerResponse actualResponse = farmerService.createFarmer(10L, request);

    assertSame(expectedResponse, actualResponse);

    verify(familyRepository).findById(10L);
    verify(farmerCodeGenerator).generate();
    verify(farmerRepository).save(farmer);
    verify(farmerMapper).toResponse(farmer);
  }

  @Test
  void shouldThrowExceptionWhenFamilyDoesNotExist() {

    when(familyRepository.findById(10L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> farmerService.createFarmer(10L, request));

    verifyNoInteractions(farmerCodeGenerator);
    verifyNoInteractions(farmerRepository);
  }

  @Test
  void shouldRejectFarmerCreationForInactiveFamily() {

    family.setActive(false);

    when(familyRepository.findById(10L)).thenReturn(Optional.of(family));

    assertThrows(BusinessException.class, () -> farmerService.createFarmer(10L, request));

    verifyNoInteractions(farmerCodeGenerator);
    verifyNoInteractions(farmerRepository);
  }
}
