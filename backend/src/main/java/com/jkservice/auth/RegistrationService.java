package com.jkservice.auth;

import java.security.SecureRandom;
import java.util.Locale;

import com.jkservice.exception.BusinessException;
import com.jkservice.exception.DuplicateResourceException;
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
  public AuthController.RegistrationResponse registerFamily(AuthController.CreateFamilyRequest r) {
    ensureEmailAvailable(r.email());
    Family f = new Family();
    f.setFamilyName(r.familyName());
    f.setInviteCode(newInviteCode());
    f = families.save(f);
    AppUser u = new AppUser();
    u.setFamily(f);
    u.setFullName(r.fullName());
    u.setEmail(normalize(r.email()));
    u.setPasswordHash(encoder.encode(r.password()));
    u.setRole("OWNER");
    u.setMembershipStatus("ACTIVE");
    u = users.save(u);
    return new AuthController.RegistrationResponse(
        u.getId(),
        f.getId(),
        u.getRole(),
        u.getMembershipStatus(),
        "Family created. Invite code: " + f.getInviteCode());
  }

  @Transactional
  public AuthController.RegistrationResponse joinFamily(AuthController.JoinFamilyRequest r) {
    ensureEmailAvailable(r.email());
    Family f =
        families
            .findByInviteCodeAndActiveTrue(r.inviteCode().trim().toUpperCase(Locale.ROOT))
            .orElseThrow(() -> new BusinessException("Invalid or inactive family invite code"));
    AppUser u = new AppUser();
    u.setFamily(f);
    u.setFullName(r.fullName());
    u.setEmail(normalize(r.email()));
    u.setPasswordHash(encoder.encode(r.password()));
    u.setRole("VIEWER");
    u.setMembershipStatus("PENDING");
    u = users.save(u);
    return new AuthController.RegistrationResponse(
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
