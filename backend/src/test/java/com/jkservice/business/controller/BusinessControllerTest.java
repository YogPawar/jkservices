package com.jkservice.business.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jkservice.business.dto.request.CreateBusinessRequest;
import com.jkservice.business.dto.request.UpdateBusinessRequest;
import com.jkservice.business.dto.response.BusinessResponse;
import com.jkservice.business.service.BusinessService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class BusinessControllerTest {

  private static final Long ORGANIZATION_ID = 10L;
  private static final Long BUSINESS_ID = 101L;

  private BusinessService businessService;
  private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

  private BusinessResponse businessResponse;

  @BeforeEach
  void setUp() {
    businessService = mock(BusinessService.class);

    BusinessController controller = new BusinessController(businessService);

    LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    validator.afterPropertiesSet();

    mockMvc = MockMvcBuilders.standaloneSetup(controller).setValidator(validator).build();

    businessResponse =
        new BusinessResponse(
            BUSINESS_ID,
            "Test Farming Business",
            "FARMING_SERVICES",
            "FARM001",
            "9876543210",
            "test@example.com",
            "Pune",
            "INR",
            true,
            LocalDateTime.now(),
            LocalDateTime.now());
  }

  private String validCreateRequest() {
    return """
    {
        "businessName": "Test Farming Business",
        "businessType": "FARMING_SERVICES",
        "businessCode": "FARM001",
        "contactNumber": "9876543210",
        "email": "test@example.com",
        "address": "Pune",
        "currencyCode": "INR"
    }
    """;
  }

  private String validUpdateRequest() {
    return """
    {
        "businessName": "Updated Farming Business",
        "businessType": "FARMING_SERVICES",
        "businessCode": "FARM002",
        "contactNumber": "9876543210",
        "email": "test@example.com",
        "address": "Pune",
        "currencyCode": "INR"
    }
    """;
  }

  @Test
  void createBusiness_shouldReturnCreatedResponse() throws Exception {

    when(businessService.createBusiness(eq(ORGANIZATION_ID), any(CreateBusinessRequest.class)))
        .thenReturn(businessResponse);

    mockMvc
        .perform(
            post("/api/v1/organizations/{organizationId}/businesses", ORGANIZATION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validCreateRequest()))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/" + BUSINESS_ID)));

    verify(businessService).createBusiness(eq(ORGANIZATION_ID), any(CreateBusinessRequest.class));
  }

  @Test
  void getBusinesses_shouldReturnOrganizationBusinesses() throws Exception {

    when(businessService.getBusinesses(ORGANIZATION_ID)).thenReturn(List.of(businessResponse));

    mockMvc
        .perform(get("/api/v1/organizations/{organizationId}/businesses", ORGANIZATION_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    verify(businessService).getBusinesses(ORGANIZATION_ID);
  }

  @Test
  void getBusinesses_shouldReturnEmptyListWhenNoBusinessesExist() throws Exception {

    when(businessService.getBusinesses(ORGANIZATION_ID)).thenReturn(List.of());

    mockMvc
        .perform(get("/api/v1/organizations/{organizationId}/businesses", ORGANIZATION_ID))
        .andExpect(status().isOk());

    verify(businessService).getBusinesses(ORGANIZATION_ID);
  }

  @Test
  void getBusiness_shouldReturnBusinessDetails() throws Exception {

    when(businessService.getBusiness(ORGANIZATION_ID, BUSINESS_ID)).thenReturn(businessResponse);

    mockMvc
        .perform(
            get(
                "/api/v1/organizations/{organizationId}/businesses/{businessId}",
                ORGANIZATION_ID,
                BUSINESS_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    verify(businessService).getBusiness(ORGANIZATION_ID, BUSINESS_ID);
  }

  @Test
  void updateBusiness_shouldReturnUpdatedBusiness() throws Exception {

    when(businessService.updateBusiness(
            eq(ORGANIZATION_ID), eq(BUSINESS_ID), any(UpdateBusinessRequest.class)))
        .thenReturn(businessResponse);

    mockMvc
        .perform(
            put(
                    "/api/v1/organizations/{organizationId}/businesses/{businessId}",
                    ORGANIZATION_ID,
                    BUSINESS_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validUpdateRequest()))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    verify(businessService)
        .updateBusiness(eq(ORGANIZATION_ID), eq(BUSINESS_ID), any(UpdateBusinessRequest.class));
  }

  @Test
  void deactivateBusiness_shouldReturnSuccessResponse() throws Exception {

    doNothing().when(businessService).deactivateBusiness(ORGANIZATION_ID, BUSINESS_ID);

    mockMvc
        .perform(
            delete(
                "/api/v1/organizations/{organizationId}/businesses/{businessId}",
                ORGANIZATION_ID,
                BUSINESS_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    verify(businessService).deactivateBusiness(ORGANIZATION_ID, BUSINESS_ID);
  }

  @Test
  void createBusiness_shouldRejectInvalidRequest() throws Exception {

    String invalidRequest =
        """
        {
            "businessName": "",
            "businessType": "FARMING_SERVICES",
            "businessCode": "FARM001",
            "contactNumber": "9876543210",
            "email": "test@example.com",
            "address": "Pune",
            "currencyCode": "INR"
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/organizations/{organizationId}/businesses", ORGANIZATION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(businessService);
  }
}
