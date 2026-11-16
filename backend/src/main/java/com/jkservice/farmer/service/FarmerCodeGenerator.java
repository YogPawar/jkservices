package com.jkservice.farmer.service;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class FarmerCodeGenerator {

  private static final String PREFIX = "F-";
  private static final String ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
  private static final int CODE_LENGTH = 12;

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  public String generate() {
    StringBuilder code = new StringBuilder(PREFIX);

    for (int i = 0; i < CODE_LENGTH; i++) {
      int index = SECURE_RANDOM.nextInt(ALPHABET.length());
      code.append(ALPHABET.charAt(index));
    }

    return code.toString();
  }
}
