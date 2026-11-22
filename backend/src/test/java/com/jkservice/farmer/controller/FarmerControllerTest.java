package com.jkservice.farmer.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jkservice.common.exception.BusinessException;
import com.jkservice.common.exception.DuplicateResourceException;
import com.jkservice.common.exception.GlobalExceptionHandler;
import com.jkservice.common.exception.ResourceNotFoundException;
import com.jkservice.farmer.dto.CreateFarmerRequest;
import com.jkservice.farmer.dto.FarmerResponse;
import com.jkservice.farmer.dto.UpdateFarmerRequest;
import com.jkservice.farmer.service.FarmerService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class FarmerControllerTest {

  @Mock private FarmerService farmerService;

  @InjectMocks private FarmerController farmerController;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(farmerController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void shouldCreateFarmerAndReturn201() throws Exception {

    FarmerResponse response = farmerResponse();

    when(farmerService.createFarmer(eq(10L), any(CreateFarmerRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/businesses/10/farmers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequest()))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/100")))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Farmer created successfully"))
        .andExpect(jsonPath("$.data.id").value(100))
        .andExpect(jsonPath("$.data.farmerCode").value("F-K7M4X9P2Q8RT"))
        .andExpect(jsonPath("$.data.name").value("Ramesh"))
        .andExpect(jsonPath("$.data.active").value(true))
        .andExpect(jsonPath("$.timestamp").exists());

    verify(farmerService).createFarmer(eq(10L), any(CreateFarmerRequest.class));
  }

  @Test
  void shouldReturn400WhenCreateRequestValidationFails() throws Exception {

    mockMvc
        .perform(
            post("/api/v1/businesses/10/farmers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "name": "",
                      "phone": "9876543210",
                      "village": "Pune",
                      "address": "Farm address",
                      "active": true
                    }
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
        .andExpect(jsonPath("$.fieldErrors.name").exists());

    verifyNoInteractions(farmerService);
  }

  @Test
  void shouldReturn404WhenBusinessDoesNotExistDuringCreation() throws Exception {

    when(farmerService.createFarmer(eq(10L), any(CreateFarmerRequest.class)))
        .thenThrow(new ResourceNotFoundException("Business not found with ID: 10"));

    mockMvc
        .perform(
            post("/api/v1/businesses/10/farmers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequest()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"))
        .andExpect(jsonPath("$.detail").value("Business not found with ID: 10"));
  }

  @Test
  void shouldReturn400WhenBusinessIsInactiveDuringCreation() throws Exception {

    when(farmerService.createFarmer(eq(10L), any(CreateFarmerRequest.class)))
        .thenThrow(new BusinessException("Cannot create a farmer under an inactive business"));

    mockMvc
        .perform(
            post("/api/v1/businesses/10/farmers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequest()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("BUSINESS_RULE_VIOLATION"));
  }

  @Test
  void shouldReturn409WhenFarmerCodeIsDuplicate() throws Exception {

    when(farmerService.createFarmer(eq(10L), any(CreateFarmerRequest.class)))
        .thenThrow(new DuplicateResourceException("Farmer code already exists"));

    mockMvc
        .perform(
            post("/api/v1/businesses/10/farmers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequest()))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errorCode").value("DUPLICATE_RESOURCE"));
  }

  @Test
  void shouldReturnAllFarmersForBusiness() throws Exception {

    when(farmerService.getFarmers(10L)).thenReturn(List.of(farmerResponse()));

    mockMvc
        .perform(get("/api/v1/businesses/10/farmers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Farmers retrieved successfully"))
        .andExpect(jsonPath("$.data.length()").value(1))
        .andExpect(jsonPath("$.data[0].id").value(100))
        .andExpect(jsonPath("$.data[0].name").value("Ramesh"));

    verify(farmerService).getFarmers(10L);
  }

  @Test
  void shouldReturnEmptyListWhenBusinessBusinessHasNoFarmers() throws Exception {

    when(farmerService.getFarmers(10L)).thenReturn(List.of());

    mockMvc
        .perform(get("/api/v1/businesses/10/farmers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isEmpty());

    verify(farmerService).getFarmers(10L);
  }

  @Test
  void shouldReturnFarmerById() throws Exception {

    when(farmerService.getFarmer(10L, 100L)).thenReturn(farmerResponse());

    mockMvc
        .perform(get("/api/v1/businesses/10/farmers/100"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(100))
        .andExpect(jsonPath("$.data.farmerCode").value("F-K7M4X9P2Q8RT"));

    verify(farmerService).getFarmer(10L, 100L);
  }

  @Test
  void shouldReturn404WhenFarmerDoesNotExist() throws Exception {

    when(farmerService.getFarmer(10L, 999L))
        .thenThrow(new ResourceNotFoundException("Farmer not found with ID: 999"));

    mockMvc
        .perform(get("/api/v1/businesses/10/farmers/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"));
  }

  @Test
  void shouldUpdateFarmerAndReturn200() throws Exception {

    FarmerResponse updatedResponse =
        new FarmerResponse(
            100L,
            "F-K7M4X9P2Q8RT",
            "Ramesh Updated",
            "9876543210",
            "Mumbai",
            "Updated address",
            true,
            LocalDateTime.now());

    when(farmerService.updateFarmer(eq(10L), eq(100L), any(UpdateFarmerRequest.class)))
        .thenReturn(updatedResponse);

    mockMvc
        .perform(
            put("/api/v1/businesses/10/farmers/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "name": "Ramesh Updated",
                      "phone": "9876543210",
                      "village": "Mumbai",
                      "address": "Updated address",
                      "active": true
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Farmer updated successfully"))
        .andExpect(jsonPath("$.data.id").value(100))
        .andExpect(jsonPath("$.data.name").value("Ramesh Updated"))
        .andExpect(jsonPath("$.data.village").value("Mumbai"));

    verify(farmerService).updateFarmer(eq(10L), eq(100L), any(UpdateFarmerRequest.class));
  }

  @Test
  void shouldReturn400WhenUpdateRequestValidationFails() throws Exception {

    mockMvc
        .perform(
            put("/api/v1/businesses/10/farmers/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "name": "",
                      "phone": "9876543210",
                      "village": "Pune",
                      "address": "Farm address",
                      "active": true
                    }
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
        .andExpect(jsonPath("$.fieldErrors.name").exists());

    verifyNoInteractions(farmerService);
  }

  @Test
  void shouldDeactivateFarmerAndReturn200() throws Exception {

    doNothing().when(farmerService).deactivateFarmer(10L, 100L);

    mockMvc
        .perform(delete("/api/v1/businesses/10/farmers/100"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Farmer deactivated successfully"))
        .andExpect(jsonPath("$.data").doesNotExist())
        .andExpect(jsonPath("$.timestamp").exists());

    verify(farmerService).deactivateFarmer(10L, 100L);
  }

  @Test
  void shouldReturn404WhenFarmerDoesNotExistDuringDeactivation() throws Exception {

    doThrow(new ResourceNotFoundException("Farmer not found with ID: 999"))
        .when(farmerService)
        .deactivateFarmer(10L, 999L);

    mockMvc
        .perform(delete("/api/v1/businesses/10/farmers/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"));
  }

  private FarmerResponse farmerResponse() {
    return new FarmerResponse(
        100L,
        "F-K7M4X9P2Q8RT",
        "Ramesh",
        "9876543210",
        "Pune",
        "Farm address",
        true,
        LocalDateTime.now());
  }

  private String validRequest() {
    return """
    {
      "name": "Ramesh",
      "phone": "9876543210",
      "village": "Pune",
      "address": "Farm address",
      "active": true
    }
    """;
  }
}
