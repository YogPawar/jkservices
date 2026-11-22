package com.jkservice.auth.repository;

import com.jkservice.auth.entity.AppUserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUserEntity, Long> {
  boolean existsByEmail(String email);

  Optional<AppUserEntity> findByEmail(String email);
}
