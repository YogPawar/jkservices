package com.jkservice.organization.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jkservice.organization.dto.request.CreateOrganizationRequest;
import com.jkservice.organization.dto.request.UpdateOrganizationRequest;
import com.jkservice.organization.dto.response.OrganizationResponse;
import com.jkservice.organization.service.OrganizationService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrganizationController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrganizationControllerTest {

  private static final String BASE_URL = "/api/v1/organizations";

  @Autowired private MockMvc mockMvc;

  @MockitoBean private OrganizationService organizationService;

  private OrganizationResponse organizationResponse;

  @BeforeEach
  void setUp() {
    organizationResponse =
        new OrganizationResponse(1L, "JK Services", "ABC123", true, Instant.now(), Instant.now());
  }

  @Test
  void createOrganization_shouldReturn201AndLocationHeader() throws Exception {
    when(organizationService.createOrganization(any(CreateOrganizationRequest.class)))
        .thenReturn(organizationResponse);

    String requestBody =
        """
        {
          "organizationName": "JK Services",
          "fullName": "Yogesh Pawar",
          "email": "yogesh@example.com",
          "password": "password123"
        }
        """;

    mockMvc
        .perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(
            header().string("Location", org.hamcrest.Matchers.endsWith("/api/v1/organizations/1")))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    verify(organizationService).createOrganization(any(CreateOrganizationRequest.class));
  }

  @Test
  void createOrganization_shouldReturn400WhenRequestIsInvalid() throws Exception {
    String requestBody =
        """
        {
          "organizationName": "",
          "fullName": "Yogesh Pawar",
          "email": "invalid-email",
          "password": "123"
        }
        """;

    mockMvc
        .perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(organizationService);
  }

  @Test
  void getOrganizations_shouldReturn200() throws Exception {
    when(organizationService.getOrganizations()).thenReturn(List.of(organizationResponse));

    mockMvc
        .perform(get(BASE_URL))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    verify(organizationService).getOrganizations();
  }

  @Test
  void getOrganization_shouldReturn200WhenOrganizationExists() throws Exception {
    when(organizationService.getOrganization(1L)).thenReturn(organizationResponse);

    mockMvc
        .perform(get(BASE_URL + "/1"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    verify(organizationService).getOrganization(1L);
  }

  @Test
  void getOrganization_shouldReturn404WhenOrganizationDoesNotExist() throws Exception {
    when(organizationService.getOrganization(99L))
        .thenThrow(
            new com.jkservice.common.exception.ResourceNotFoundException(
                "Organization not found with ID: 99"));

    mockMvc.perform(get(BASE_URL + "/99")).andExpect(status().isNotFound());

    verify(organizationService).getOrganization(99L);
  }

  @Test
  void updateOrganization_shouldReturn200() throws Exception {
    when(organizationService.updateOrganization(eq(1L), any(UpdateOrganizationRequest.class)))
        .thenReturn(organizationResponse);

    String requestBody =
        """
        {
          "organizationName": "Updated JK Services",
          "active": true
        }
        """;

    mockMvc
        .perform(put(BASE_URL + "/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    verify(organizationService)
        .updateOrganization(
            eq(1L),
            argThat(request ->
                "Updated JK Services".equals(request.organizationName())
                    && Boolean.TRUE.equals(request.active())));
  }

  @Test
  void updateOrganization_shouldReturn200WhenReactivatingOrganization() throws Exception {
    when(organizationService.updateOrganization(eq(1L), any(UpdateOrganizationRequest.class)))
        .thenReturn(organizationResponse);

    String requestBody =
        """
        {
          "organizationName": "JK Services",
          "active": true
        }
        """;

    mockMvc
        .perform(put(BASE_URL + "/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk());

    verify(organizationService)
        .updateOrganization(eq(1L), argThat(request -> Boolean.TRUE.equals(request.active())));
  }

  @Test
  void updateOrganization_shouldReturn400WhenNameIsBlank() throws Exception {
    String requestBody =
        """
        {
          "organizationName": "   ",
          "active": true
        }
        """;

    mockMvc
        .perform(put(BASE_URL + "/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(organizationService);
  }

  @Test
  void updateOrganization_shouldReturn400WhenActiveIsMissing() throws Exception {
    String requestBody =
        """
        {
          "organizationName": "Updated JK Services"
        }
        """;

    mockMvc
        .perform(put(BASE_URL + "/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(organizationService);
  }

  @Test
  void deactivateOrganization_shouldReturn200() throws Exception {
    doNothing().when(organizationService).deactivateOrganization(1L);

    mockMvc
        .perform(delete(BASE_URL + "/1"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    verify(organizationService).deactivateOrganization(1L);
  }

  @Test
  void deactivateOrganization_shouldReturn404WhenOrganizationDoesNotExist() throws Exception {
    doThrow(
            new com.jkservice.common.exception.ResourceNotFoundException(
                "Organization not found with ID: 99"))
        .when(organizationService)
        .deactivateOrganization(99L);

    mockMvc.perform(delete(BASE_URL + "/99")).andExpect(status().isNotFound());

    verify(organizationService).deactivateOrganization(99L);
  }
}
