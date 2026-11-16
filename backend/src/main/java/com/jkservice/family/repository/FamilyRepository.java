package com.jkservice.family.repository;

import com.jkservice.family.entity.FamilyEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyRepository extends JpaRepository<FamilyEntity, Long> {
  Optional<FamilyEntity> findByInviteCodeAndActiveTrue(String inviteCode);
}
