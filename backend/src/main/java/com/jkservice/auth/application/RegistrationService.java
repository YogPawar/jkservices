package com.jkservice.auth.application;

import com.jkservice.auth.entity.AppUserEntity;
import com.jkservice.auth.persistence.AppUserRepository;
import com.jkservice.family.entity.FamilyEntity;
import com.jkservice.family.repository.FamilyRepository;
import com.jkservice.auth.api.dto.request.CreateFamilyRequest;
import com.jkservice.auth.api.dto.request.JoinFamilyRequest;
import com.jkservice.auth.api.dto.response.RegistrationResponse;
import com.jkservice.common.exception.BusinessException;
import com.jkservice.common.exception.DuplicateResourceException;
import jakarta.validation.Valid;
import java.security.SecureRandom;
import java.util.Locale;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {
  private final FamilyRepository families;
  private final AppUserRepository users;
  private final BCryptPasswordEncoder encoder;
  private final SecureRandom random = new SecureRandom();

  public RegistrationService(FamilyRepository f, AppUserRepository u, BCryptPasswordEncoder e) {
    families = f;
    users = u;
    encoder = e;
  }

  @Transactional
  public RegistrationResponse registerFamily(@Valid CreateFamilyRequest r) {
    ensureEmailAvailable(r.email());
    FamilyEntity f = new FamilyEntity();
    f.setFamilyName(r.familyName());
    f.setInviteCode(newInviteCode());
    f = families.save(f);
    AppUserEntity u = new AppUserEntity();
    u.setFamily(f);
    u.setFullName(r.fullName());
    u.setEmail(normalize(r.email()));
    u.setPasswordHash(encoder.encode(r.password()));
    u.setRole("OWNER");
    u.setMembershipStatus("ACTIVE");
    u = users.save(u);
    return new RegistrationResponse(
        u.getId(),
        f.getId(),
        u.getRole(),
        u.getMembershipStatus(),
        "Family created. Invite code: " + f.getInviteCode());
  }

  @Transactional
  public RegistrationResponse joinFamily(JoinFamilyRequest r) {
    ensureEmailAvailable(r.email());
    FamilyEntity f =
        families
            .findByInviteCodeAndActiveTrue(r.inviteCode().trim().toUpperCase(Locale.ROOT))
            .orElseThrow(() -> new BusinessException("Invalid or inactive family invite code"));
    AppUserEntity u = new AppUserEntity();
    u.setFamily(f);
    u.setFullName(r.fullName());
    u.setEmail(normalize(r.email()));
    u.setPasswordHash(encoder.encode(r.password()));
    u.setRole("VIEWER");
    u.setMembershipStatus("PENDING");
    u = users.save(u);
    return new RegistrationResponse(
        u.getId(),
        f.getId(),
        u.getRole(),
        u.getMembershipStatus(),
        "Registration submitted. An OWNER or ADMIN must approve access.");
  }

  private void ensureEmailAvailable(String email) {
    if (users.existsByEmail(normalize(email)))
      throw new DuplicateResourceException("Email is already registered");
  }

  private String normalize(String email) {
    return email.trim().toLowerCase(Locale.ROOT);
  }

  private String newInviteCode() {
    String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    StringBuilder b = new StringBuilder("SH");
    for (int i = 0; i < 8; i++) b.append(chars.charAt(random.nextInt(chars.length())));
    return b.toString();
  }
}
