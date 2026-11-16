package com.jkservice.farmer.service;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FarmerCodeGeneratorTest {

  private final FarmerCodeGenerator generator = new FarmerCodeGenerator();

  @Test
  void shouldGenerateCodeWithExpectedFormat() {
    String farmerCode = generator.generate();

    assertTrue(
        farmerCode.matches("F-[23456789ABCDEFGHJKLMNPQRSTUVWXYZ]{12}"),
        "Farmer code must contain F- followed by 12 valid characters");
  }

  @Test
  void shouldGenerateDifferentCodesForDifferentRequests() {
    String firstCode = generator.generate();
    String secondCode = generator.generate();

    assertNotEquals(firstCode, secondCode);
  }
}
