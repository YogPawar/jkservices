package com.jkservice.business.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jkservice.business.dto.request.CreateBusinessRequest;
import com.jkservice.business.dto.request.UpdateBusinessRequest;
import com.jkservice.business.enums.BusinessType;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BusinessDtoValidationTest {

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

  @Test
  void shouldAcceptValidCreateBusinessRequest() {
    CreateBusinessRequest request =
        new CreateBusinessRequest(
            "Pawar Farming",
            BusinessType.FARMING_SERVICES,
            "PAWAR-001",
            "9876543210",
            "test@example.com",
            "Pune",
            null);

    assertTrue(validator.validate(request).isEmpty());
  }

  @Test
  void shouldRejectMissingRequiredCreateFields() {
    CreateBusinessRequest request =
        new CreateBusinessRequest(null, null, null, null, null, null, null);

    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void shouldRejectBusinessNameExceeding150Characters() {
    CreateBusinessRequest request =
        new CreateBusinessRequest(
            "A".repeat(151), BusinessType.FARMING_SERVICES, "PAWAR-001", null, null, null, null);

    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void shouldRejectInvalidEmail() {
    CreateBusinessRequest request =
        new CreateBusinessRequest(
            "Pawar Farming",
            BusinessType.FARMING_SERVICES,
            "PAWAR-001",
            null,
            "invalid-email",
            null,
            null);

    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void shouldRejectInvalidCurrencyCode() {
    CreateBusinessRequest request =
        new CreateBusinessRequest(
            "Pawar Farming", BusinessType.FARMING_SERVICES, "PAWAR-001", null, null, null, "IN");

    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void shouldAcceptNullCurrencyCodeOnCreate() {
    CreateBusinessRequest request =
        new CreateBusinessRequest(
            "Pawar Farming", BusinessType.FARMING_SERVICES, "PAWAR-001", null, null, null, null);

    assertTrue(validator.validate(request).isEmpty());
  }

  @Test
  void shouldAcceptValidUpdateBusinessRequest() {
    UpdateBusinessRequest request =
        new UpdateBusinessRequest(
            "Pawar Farming",
            BusinessType.FARMING_SERVICES,
            "PAWAR-001",
            "9876543210",
            "test@example.com",
            "Pune",
            "INR");

    assertTrue(validator.validate(request).isEmpty());
  }

  @Test
  void shouldRejectMissingCurrencyCodeOnUpdate() {
    UpdateBusinessRequest request =
        new UpdateBusinessRequest(
            "Pawar Farming", BusinessType.FARMING_SERVICES, "PAWAR-001", null, null, null, null);

    assertFalse(validator.validate(request).isEmpty());
  }
}
