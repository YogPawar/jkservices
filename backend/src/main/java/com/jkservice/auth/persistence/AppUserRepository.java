package com.jkservice.auth.persistence;

import com.jkservice.auth.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
  boolean existsByEmail(String email);
}
