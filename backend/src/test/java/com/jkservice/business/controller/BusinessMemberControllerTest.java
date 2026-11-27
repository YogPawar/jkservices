package com.jkservice.business.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jkservice.business.dto.request.AssignBusinessMemberRequest;
import com.jkservice.business.dto.request.UpdateBusinessMemberRequest;
import com.jkservice.business.dto.response.BusinessMemberResponse;
import com.jkservice.business.enums.BusinessMemberRole;
import com.jkservice.business.enums.BusinessMembershipStatus;
import com.jkservice.business.service.BusinessMemberService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class BusinessMemberControllerTest {

  private static final Long ORGANIZATION_ID = 1L;
  private static final Long BUSINESS_ID = 10L;
  private static final Long MEMBER_ID = 30L;

  @Mock private BusinessMemberService businessMemberService;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  private BusinessMemberResponse response;

  private String baseUrl() {
    return "/api/v1/organizations/" + ORGANIZATION_ID + "/businesses/" + BUSINESS_ID + "/members";
  }

  @BeforeEach
  void setUp() {
    BusinessMemberController controller = new BusinessMemberController(businessMemberService);

    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

    objectMapper = new ObjectMapper();

    response =
        new BusinessMemberResponse(
            MEMBER_ID,
            ORGANIZATION_ID,
            BUSINESS_ID,
            20L,
            BusinessMemberRole.MANAGER,
            BusinessMembershipStatus.ACTIVE,
            null,
            null);
  }

  @Test
  void assignMember_shouldReturn201() throws Exception {
    AssignBusinessMemberRequest request =
        new AssignBusinessMemberRequest(20L, BusinessMemberRole.MANAGER);

    when(businessMemberService.assignMember(ORGANIZATION_ID, BUSINESS_ID, request))
        .thenReturn(response);

    mockMvc
        .perform(
            post(baseUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    verify(businessMemberService)
        .assignMember(eq(ORGANIZATION_ID), eq(BUSINESS_ID), any(AssignBusinessMemberRequest.class));
  }

  @Test
  void getMembers_shouldReturn200() throws Exception {
    when(businessMemberService.getMembers(ORGANIZATION_ID, BUSINESS_ID))
        .thenReturn(List.of(response));

    mockMvc.perform(get(baseUrl())).andExpect(status().isOk());

    verify(businessMemberService).getMembers(ORGANIZATION_ID, BUSINESS_ID);
  }

  @Test
  void getMember_shouldReturn200() throws Exception {
    when(businessMemberService.getMember(ORGANIZATION_ID, BUSINESS_ID, MEMBER_ID))
        .thenReturn(response);

    mockMvc.perform(get(baseUrl() + "/" + MEMBER_ID)).andExpect(status().isOk());

    verify(businessMemberService).getMember(ORGANIZATION_ID, BUSINESS_ID, MEMBER_ID);
  }

  @Test
  void updateMember_shouldReturn200() throws Exception {
    UpdateBusinessMemberRequest request =
        new UpdateBusinessMemberRequest(
            BusinessMemberRole.OPERATOR, BusinessMembershipStatus.ACTIVE);

    when(businessMemberService.updateMember(
            eq(ORGANIZATION_ID),
            eq(BUSINESS_ID),
            eq(MEMBER_ID),
            any(UpdateBusinessMemberRequest.class)))
        .thenReturn(response);

    mockMvc
        .perform(
            put(baseUrl() + "/" + MEMBER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    verify(businessMemberService)
        .updateMember(
            eq(ORGANIZATION_ID),
            eq(BUSINESS_ID),
            eq(MEMBER_ID),
            any(UpdateBusinessMemberRequest.class));
  }

  @Test
  void unassignMember_shouldReturn200() throws Exception {
    doNothing().when(businessMemberService).unassignMember(ORGANIZATION_ID, BUSINESS_ID, MEMBER_ID);

    mockMvc.perform(delete(baseUrl() + "/" + MEMBER_ID)).andExpect(status().isOk());

    verify(businessMemberService).unassignMember(ORGANIZATION_ID, BUSINESS_ID, MEMBER_ID);
  }
}
