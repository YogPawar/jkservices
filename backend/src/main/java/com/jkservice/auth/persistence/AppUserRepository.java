package com.jkservice.auth.persistence;

import com.jkservice.auth.entity.AppUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUserEntity, Long> {
  boolean existsByEmail(String email);
}
