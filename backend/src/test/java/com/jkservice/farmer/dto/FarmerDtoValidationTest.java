package com.jkservice.farmer.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class FarmerDtoValidationTest {

  private static ValidatorFactory validatorFactory;
  private static Validator validator;

  @BeforeAll
  static void setUp() {
    validatorFactory = Validation.buildDefaultValidatorFactory();
    validator = validatorFactory.getValidator();
  }

  @AfterAll
  static void tearDown() {
    if (validatorFactory != null) {
      validatorFactory.close();
    }
  }

  // --------------------------------------------------
  // CreateFarmerRequest tests
  // --------------------------------------------------

  @Test
  void shouldAcceptValidCreateFarmerRequest() {

    CreateFarmerRequest request =
        new CreateFarmerRequest("Ramesh Pawar", "9876543210", "Wagholi", "Pune, Maharashtra", true);

    Set<ConstraintViolation<CreateFarmerRequest>> violations = validator.validate(request);

    assertTrue(violations.isEmpty());
  }

  @Test
  void shouldRejectNullFarmerName() {

    CreateFarmerRequest request =
        new CreateFarmerRequest(null, "9876543210", "Wagholi", null, true);

    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void shouldRejectBlankFarmerName() {

    CreateFarmerRequest request = new CreateFarmerRequest("   ", null, null, null, true);

    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void shouldRejectFarmerNameWithLessThanTwoCharacters() {

    CreateFarmerRequest request = new CreateFarmerRequest("R", null, null, null, true);

    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void shouldRejectFarmerNameExceeding150Characters() {

    String name = "A".repeat(151);

    CreateFarmerRequest request = new CreateFarmerRequest(name, null, null, null, true);

    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void shouldRejectPhoneExceeding20Characters() {

    CreateFarmerRequest request =
        new CreateFarmerRequest("Ramesh Pawar", "123456789012345678901", null, null, true);

    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void shouldRejectVillageExceeding100Characters() {

    String village = "V".repeat(101);

    CreateFarmerRequest request =
        new CreateFarmerRequest("Ramesh Pawar", null, village, null, true);

    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void shouldRejectNullActiveStatus() {

    CreateFarmerRequest request = new CreateFarmerRequest("Ramesh Pawar", null, null, null, null);

    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void shouldAcceptNullOptionalFields() {

    CreateFarmerRequest request = new CreateFarmerRequest("Ramesh Pawar", null, null, null, true);

    assertTrue(validator.validate(request).isEmpty());
  }

  // --------------------------------------------------
  // UpdateFarmerRequest tests
  // --------------------------------------------------

  @Test
  void shouldAcceptValidUpdateFarmerRequest() {

    UpdateFarmerRequest request =
        new UpdateFarmerRequest("Ramesh Pawar", "9876543210", "Wagholi", "Pune, Maharashtra", true);

    assertTrue(validator.validate(request).isEmpty());
  }

  @Test
  void shouldRejectNullNameInUpdateRequest() {

    UpdateFarmerRequest request = new UpdateFarmerRequest(null, null, null, null, true);

    assertHasViolation(request, "name");
  }

  @Test
  void shouldRejectNullActiveStatusInUpdateRequest() {

    UpdateFarmerRequest request = new UpdateFarmerRequest("Ramesh Pawar", null, null, null, null);

    assertHasViolation(request, "active");
  }

  @Test
  void shouldAcceptNullOptionalFieldsInUpdateRequest() {

    UpdateFarmerRequest request = new UpdateFarmerRequest("Ramesh Pawar", null, null, null, false);

    assertTrue(validator.validate(request).isEmpty());
  }

  private <T> void assertHasViolation(T request, String property) {

    Set<ConstraintViolation<T>> violations = validator.validate(request);

    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals(property)),
        "Expected validation error for property: " + property);
  }

  private <T> void assertNoViolations(T request) {

    Set<ConstraintViolation<T>> violations = validator.validate(request);

    assertTrue(violations.isEmpty(), "Expected no validation errors but found: " + violations);
  }
}
