package com.jkservice.customer.service;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class CustomerCodeGenerator {

  private static final String PREFIX = "C-";
  private static final String ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
  private static final int CODE_LENGTH = 12;

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  public String generate() {
    StringBuilder code = new StringBuilder(PREFIX);

    for (int i = 0; i < CODE_LENGTH; i++) {
      code.append(ALPHABET.charAt(SECURE_RANDOM.nextInt(ALPHABET.length())));
    }

    return code.toString();
  }
}
