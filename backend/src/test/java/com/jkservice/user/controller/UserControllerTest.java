package com.jkservice.user.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jkservice.user.dto.request.CreateUserRequest;
import com.jkservice.user.dto.request.UpdateUserRequest;
import com.jkservice.user.dto.request.UpdateUserStatusRequest;
import com.jkservice.user.dto.response.UserResponse;
import com.jkservice.user.service.UserService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private UserService userService;

  @Test
  void createUser_shouldReturn201() throws Exception {

    CreateUserRequest request =
        new CreateUserRequest("Yogesh Pawar", "yogesh@example.com", "Password12345");

    UserResponse response =
        new UserResponse(1L, "Yogesh Pawar", "yogesh@example.com", true, null, null);

    when(userService.createUser(any())).thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.email").value("yogesh@example.com"));
  }

  @Test
  void createUser_shouldRejectInvalidRequest() throws Exception {

    CreateUserRequest request = new CreateUserRequest("", "invalid-email", "123");

    mockMvc
        .perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());

    verify(userService, never()).createUser(any());
  }

  @Test
  void getUserById_shouldReturn200() throws Exception {

    when(userService.getUserById(1L))
        .thenReturn(new UserResponse(1L, "Yogesh Pawar", "yogesh@example.com", true, null, null));

    mockMvc
        .perform(get("/api/v1/users/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  void getUsers_shouldReturn200() throws Exception {

    UserResponse response =
        new UserResponse(1L, "Yogesh Pawar", "yogesh@example.com", true, null, null);

    Page<UserResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1);

    when(userService.getUsers(any())).thenReturn(page);

    mockMvc
        .perform(get("/api/v1/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1));
  }

  @Test
  void updateUser_shouldReturn200() throws Exception {

    UpdateUserRequest request = new UpdateUserRequest("Updated Name");

    when(userService.updateUser(eq(1L), any()))
        .thenReturn(new UserResponse(1L, "Updated Name", "yogesh@example.com", true, null, null));

    mockMvc
        .perform(
            put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.fullName").value("Updated Name"));
  }

  @Test
  void updateUserStatus_shouldReturn200() throws Exception {

    UpdateUserStatusRequest request = new UpdateUserStatusRequest(false);

    when(userService.updateUserStatus(eq(1L), any()))
        .thenReturn(new UserResponse(1L, "Yogesh Pawar", "yogesh@example.com", false, null, null));

    mockMvc
        .perform(
            patch("/api/v1/users/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(false));
  }
}
