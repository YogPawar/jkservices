package com.jkservice.auth.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.jkservice.user.entity.UserEntity;
import com.jkservice.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserRepositoryTest {

  @Autowired private UserRepository userRepository;

  @Autowired private EntityManager entityManager;

  @Test
  void shouldSaveUserSuccessfully() {
    UserEntity user = createUser("user1@test.com");

    UserEntity savedUser = userRepository.saveAndFlush(user);

    assertNotNull(savedUser.getId());
    assertEquals("Test User", savedUser.getFullName());
    assertEquals("user1@test.com", savedUser.getEmail());
    assertTrue(savedUser.isActive());
  }

  @Test
  void shouldFindUserByEmail() {
    UserEntity user = userRepository.saveAndFlush(createUser("user2@test.com"));

    assertTrue(userRepository.findByEmailIgnoreCase(user.getEmail()).isPresent());
  }

  @Test
  void shouldFindUserByEmailIgnoringCase() {
    userRepository.saveAndFlush(createUser("user3@test.com"));

    assertTrue(userRepository.findByEmailIgnoreCase("USER3@TEST.COM").isPresent());
  }

  @Test
  void shouldReturnTrueWhenEmailExists() {
    userRepository.saveAndFlush(createUser("user4@test.com"));

    assertTrue(userRepository.existsByEmailIgnoreCase("user4@test.com"));
  }

  @Test
  void shouldReturnFalseWhenEmailDoesNotExist() {
    assertFalse(userRepository.existsByEmailIgnoreCase("unknown@test.com"));
  }

  @Test
  void shouldRejectDuplicateEmail() {
    userRepository.saveAndFlush(createUser("duplicate@test.com"));

    UserEntity duplicate = createUser("duplicate@test.com");

    assertThrows(
        DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(duplicate));
  }

  @Test
  void shouldPopulateDatabaseManagedTimestamps() {
    UserEntity user = userRepository.saveAndFlush(createUser("timestamp@test.com"));

    entityManager.refresh(user);

    assertNotNull(user.getCreatedAt());
    assertNotNull(user.getUpdatedAt());
  }

  private UserEntity createUser(String email) {
    UserEntity user = new UserEntity();
    user.setFullName("Test User");
    user.setEmail(email);
    user.setPasswordHash("test-password-hash");
    user.setActive(true);

    return user;
  }
}
