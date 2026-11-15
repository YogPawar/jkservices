package com.jkservice.family.persistence;

import java.util.Optional;

import com.jkservice.family.domain.Family;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyRepository extends JpaRepository<Family, Long> {
  Optional<Family> findByInviteCodeAndActiveTrue(String inviteCode);
}
