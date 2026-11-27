package com.jkservice.user.service;

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
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final EntityManager entityManager;

  public UserService(
      UserRepository userRepository,
      UserMapper userMapper,
      PasswordEncoder passwordEncoder,
      EntityManager entityManager) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
    this.entityManager = entityManager;
  }

  @Transactional
  public UserResponse createUser(CreateUserRequest request) {

    String email = normalizeEmail(request.email());

    if (userRepository.existsByEmailIgnoreCase(email)) {
      throw new DuplicateResourceException("User already exists with email: " + email);
    }

    UserEntity user =
        new UserEntity(
            request.fullName().trim(), email, passwordEncoder.encode(request.password()));

    UserEntity savedUser = userRepository.saveAndFlush(user);

    entityManager.refresh(savedUser);

    return userMapper.toResponse(savedUser);
  }

  @Transactional(readOnly = true)
  public Page<UserResponse> getUsers(Pageable pageable) {

    return userRepository.findAll(pageable).map(userMapper::toResponse);
  }

  @Transactional(readOnly = true)
  public UserResponse getUserById(Long id) {

    return userMapper.toResponse(findUser(id));
  }

  @Transactional
  public UserResponse updateUser(Long id, @Valid UpdateUserRequest request) {

    UserEntity user = findUser(id);

    user.setFullName(request.fullName().trim());

    UserEntity updatedUser = userRepository.saveAndFlush(user);

    entityManager.refresh(updatedUser);

    return userMapper.toResponse(updatedUser);
  }

  @Transactional
  public UserResponse updateUserStatus(Long id, @Valid UpdateUserStatusRequest request) {

    UserEntity user = findUser(id);

    user.setActive(request.active());

    UserEntity updatedUser = userRepository.saveAndFlush(user);

    entityManager.refresh(updatedUser);

    return userMapper.toResponse(updatedUser);
  }

  private UserEntity findUser(Long id) {

    return userRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
  }

  private String normalizeEmail(String email) {
    return email.trim().toLowerCase(java.util.Locale.ROOT);
  }
}
