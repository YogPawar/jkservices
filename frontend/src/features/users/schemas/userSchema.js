const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export function validateCreateUser(values) {
  const errors = {};

  if (!values.fullName?.trim()) {
    errors.fullName = "Full name is required";
  } else if (values.fullName.trim().length > 150) {
    errors.fullName = "Full name cannot exceed 150 characters";
  }

  if (!values.email?.trim()) {
    errors.email = "Email is required";
  } else if (
    values.email.trim().length > 255 ||
    !EMAIL_REGEX.test(values.email.trim())
  ) {
    errors.email = "Enter a valid email address";
  }

  if (!values.password) {
    errors.password = "Password is required";
  } else if (values.password.length < 12) {
    errors.password = "Password must contain at least 12 characters";
  } else if (values.password.length > 72) {
    errors.password = "Password cannot exceed 72 characters";
  }

  return errors;
}

export function validateUpdateUser(values) {
  const errors = {};

  if (!values.fullName?.trim()) {
    errors.fullName = "Full name is required";
  } else if (values.fullName.trim().length > 150) {
    errors.fullName = "Full name cannot exceed 150 characters";
  }

  return errors;
}
