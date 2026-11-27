package com.jkservice.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.jkservice.common.exception.DuplicateResourceException;
import com.jkservice.common.exception.ResourceNotFoundException;
import com.jkservice.user.dto.request.CreateUserRequest;
import com.jkservice.user.dto.request.UpdateUserRequest;
import com.jkservice.user.dto.request.UpdateUserStatusRequest;
import com.jkservice.user.dto.response.UserResponse;
import com.jkservice.user.entity.UserEntity;
import com.jkservice.user.mapper.UserMapper;
import com.jkservice.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private UserMapper userMapper;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private EntityManager entityManager;

  @InjectMocks private UserService userService;

  private UserEntity user;

  private UserResponse response;

  @BeforeEach
  void setUp() {

    user = new UserEntity("Yogesh Pawar", "yogesh@example.com", "encoded-password");

    response = new UserResponse(1L, "Yogesh Pawar", "yogesh@example.com", true, null, null);
  }

  @Test
  void createUser_shouldCreateUserSuccessfully() {

    CreateUserRequest request =
        new CreateUserRequest("Yogesh Pawar", " YOGESH@example.com ", "Password123");

    when(userRepository.existsByEmailIgnoreCase("yogesh@example.com")).thenReturn(false);

    when(passwordEncoder.encode("Password123")).thenReturn("encoded-password");

    when(userRepository.saveAndFlush(any(UserEntity.class))).thenReturn(user);

    when(userMapper.toResponse(user)).thenReturn(response);

    UserResponse result = userService.createUser(request);

    assertEquals("yogesh@example.com", result.email());

    verify(passwordEncoder).encode("Password123");
    verify(userRepository).saveAndFlush(any(UserEntity.class));
    verify(entityManager).refresh(user);
  }

  @Test
  void createUser_shouldRejectDuplicateEmail() {

    CreateUserRequest request =
        new CreateUserRequest("Yogesh Pawar", "yogesh@example.com", "Password123");

    when(userRepository.existsByEmailIgnoreCase("yogesh@example.com")).thenReturn(true);

    assertThrows(DuplicateResourceException.class, () -> userService.createUser(request));

    verify(userRepository, never()).saveAndFlush(any(UserEntity.class));

    verify(passwordEncoder, never()).encode(any());
  }

  @Test
  void getUsers_shouldReturnPagedUsers() {

    PageRequest pageable = PageRequest.of(0, 20);

    Page<UserEntity> userPage = new PageImpl<>(List.of(user), pageable, 1);

    when(userRepository.findAll(pageable)).thenReturn(userPage);

    when(userMapper.toResponse(user)).thenReturn(response);

    Page<UserResponse> result = userService.getUsers(pageable);

    assertEquals(1, result.getTotalElements());
    assertEquals("yogesh@example.com", result.getContent().get(0).email());
  }

  @Test
  void getUserById_shouldReturnUser() {

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    when(userMapper.toResponse(user)).thenReturn(response);

    UserResponse result = userService.getUserById(1L);

    assertEquals(1L, result.id());
  }

  @Test
  void getUserById_shouldThrowWhenUserDoesNotExist() {

    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
  }

  @Test
  void updateUser_shouldUpdateFullName() {

    UpdateUserRequest request = new UpdateUserRequest("Yogesh Updated");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    when(userRepository.saveAndFlush(user)).thenReturn(user);

    when(userMapper.toResponse(user))
        .thenReturn(new UserResponse(1L, "Yogesh Updated", "yogesh@example.com", true, null, null));

    UserResponse result = userService.updateUser(1L, request);

    assertEquals("Yogesh Updated", user.getFullName());
    assertEquals("Yogesh Updated", result.fullName());

    verify(entityManager).refresh(user);
  }

  @Test
  void updateUser_shouldThrowWhenUserDoesNotExist() {

    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> userService.updateUser(99L, new UpdateUserRequest("Updated Name")));
  }

  @Test
  void updateUserStatus_shouldDeactivateUser() {

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    when(userRepository.saveAndFlush(user)).thenReturn(user);

    when(userMapper.toResponse(user))
        .thenReturn(new UserResponse(1L, "Yogesh Pawar", "yogesh@example.com", false, null, null));

    UserResponse result = userService.updateUserStatus(1L, new UpdateUserStatusRequest(false));

    assertFalse(user.isActive());
    assertFalse(result.active());
  }

  @Test
  void updateUserStatus_shouldActivateUser() {

    user.setActive(false);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    when(userRepository.saveAndFlush(user)).thenReturn(user);

    when(userMapper.toResponse(user))
        .thenReturn(new UserResponse(1L, "Yogesh Pawar", "yogesh@example.com", true, null, null));

    UserResponse result = userService.updateUserStatus(1L, new UpdateUserStatusRequest(true));

    assertTrue(user.isActive());
    assertTrue(result.active());
  }
}
