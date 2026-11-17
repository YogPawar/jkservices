export function validateFarmer(values) {
  const errors = {};

  const name = values.name?.trim() ?? "";
  const phone = values.phone?.trim() ?? "";
  const village = values.village?.trim() ?? "";

  if (!name) {
    errors.name = "Farmer name is required";
  } else if (name.length < 2 || name.length > 150) {
    errors.name = "Name must be between 2 and 150 characters";
  }

  if (phone && phone.length > 20) {
    errors.phone = "Phone number cannot exceed 20 characters";
  } else if (phone && !/^[6-9]\d{9}$/.test(phone)) {
    errors.phone = "Enter a valid 10-digit Indian mobile number";
  }

  if (village.length > 100) {
    errors.village = "Village cannot exceed 100 characters";
  }

  if (typeof values.active !== "boolean") {
    errors.active = "Please select farmer status";
  }

  return errors;
}

export function isFarmerFormValid(values) {
  return Object.keys(validateFarmer(values)).length === 0;
}
