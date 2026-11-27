package com.jkservice.user.repository;

import com.jkservice.user.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
  boolean existsByEmail(String email);

  Optional<UserEntity> findByEmail(String email);

  Optional<Object> findByEmailIgnoreCase(String mail);

  boolean existsByEmailIgnoreCase(String mail);
}
