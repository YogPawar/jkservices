package com.jkservice.organization.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jkservice.organization.dto.request.CreateOrganizationMemberRequest;
import com.jkservice.organization.dto.request.UpdateOrganizationMemberRequest;
import com.jkservice.organization.dto.response.OrganizationMemberResponse;
import com.jkservice.organization.enums.OrganizationMembershipStatus;
import com.jkservice.organization.enums.OrganizationRole;
import com.jkservice.organization.service.OrganizationMemberService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrganizationMemberController.class)
class OrganizationMemberControllerTest {

  private static final Long ORGANIZATION_ID = 1L;
  private static final Long MEMBER_ID = 101L;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private OrganizationMemberService organizationMemberService;

  private OrganizationMemberResponse response() {
    Instant now = Instant.parse("2026-11-27T10:30:00Z");

    return new OrganizationMemberResponse(
        MEMBER_ID,
        ORGANIZATION_ID,
        12L,
        OrganizationRole.MEMBER,
        OrganizationMembershipStatus.ACTIVE,
        now,
        now,
        now);
  }

  @Test
  void addMember_shouldReturnCreated() throws Exception {
    CreateOrganizationMemberRequest request =
        new CreateOrganizationMemberRequest(12L, OrganizationRole.MEMBER);

    when(organizationMemberService.addMember(
            eq(ORGANIZATION_ID), any(CreateOrganizationMemberRequest.class)))
        .thenReturn(response());

    mockMvc
        .perform(
            post("/api/v1/organizations/{organizationId}/members", ORGANIZATION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    verify(organizationMemberService)
        .addMember(eq(ORGANIZATION_ID), any(CreateOrganizationMemberRequest.class));
  }

  @Test
  void addMember_withMissingUserId_shouldReturnBadRequest() throws Exception {
    String request =
        """
        {
          "role": "MEMBER"
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/organizations/{organizationId}/members", ORGANIZATION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest());
  }

  @Test
  void addMember_withMissingRole_shouldReturnBadRequest() throws Exception {
    String request =
        """
        {
          "userId": 12
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/organizations/{organizationId}/members", ORGANIZATION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getMembers_shouldReturnOk() throws Exception {
    when(organizationMemberService.getMembers(ORGANIZATION_ID)).thenReturn(List.of(response()));

    mockMvc
        .perform(get("/api/v1/organizations/{organizationId}/members", ORGANIZATION_ID))
        .andExpect(status().isOk());

    verify(organizationMemberService).getMembers(ORGANIZATION_ID);
  }

  @Test
  void getMember_shouldReturnOk() throws Exception {
    when(organizationMemberService.getMember(ORGANIZATION_ID, MEMBER_ID)).thenReturn(response());

    mockMvc
        .perform(
            get(
                "/api/v1/organizations/{organizationId}/members/{memberId}",
                ORGANIZATION_ID,
                MEMBER_ID))
        .andExpect(status().isOk());

    verify(organizationMemberService).getMember(ORGANIZATION_ID, MEMBER_ID);
  }

  @Test
  void updateMember_shouldReturnOk() throws Exception {
    UpdateOrganizationMemberRequest request =
        new UpdateOrganizationMemberRequest(
            OrganizationRole.ADMIN, OrganizationMembershipStatus.ACTIVE);

    when(organizationMemberService.updateMember(
            eq(ORGANIZATION_ID), eq(MEMBER_ID), any(UpdateOrganizationMemberRequest.class)))
        .thenReturn(response());

    mockMvc
        .perform(
            put(
                    "/api/v1/organizations/{organizationId}/members/{memberId}",
                    ORGANIZATION_ID,
                    MEMBER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    verify(organizationMemberService)
        .updateMember(
            eq(ORGANIZATION_ID), eq(MEMBER_ID), any(UpdateOrganizationMemberRequest.class));
  }

  @Test
  void updateMember_withMissingRole_shouldReturnBadRequest() throws Exception {
    String request =
        """
        {
          "membershipStatus": "ACTIVE"
        }
        """;

    mockMvc
        .perform(
            put(
                    "/api/v1/organizations/{organizationId}/members/{memberId}",
                    ORGANIZATION_ID,
                    MEMBER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest());
  }

  @Test
  void removeMember_shouldReturnOk() throws Exception {
    doNothing().when(organizationMemberService).removeMember(ORGANIZATION_ID, MEMBER_ID);

    mockMvc
        .perform(
            delete(
                "/api/v1/organizations/{organizationId}/members/{memberId}",
                ORGANIZATION_ID,
                MEMBER_ID))
        .andExpect(status().isOk());

    verify(organizationMemberService).removeMember(ORGANIZATION_ID, MEMBER_ID);
  }
}
