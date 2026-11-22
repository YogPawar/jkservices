export function validateOrganization(values, mode = "create") {
  const errors = {};

  if (!values.organizationName?.trim()) {
    errors.organizationName = "Organization name is required";
  } else if (values.organizationName.trim().length > 150) {
    errors.organizationName = "Maximum 150 characters allowed";
  }

  if (mode === "create") {
    if (!values.fullName?.trim()) {
      errors.fullName = "Owner name is required";
    } else if (values.fullName.trim().length > 150) {
      errors.fullName = "Maximum 150 characters allowed";
    }

    if (!values.email?.trim()) {
      errors.email = "Email is required";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(values.email)) {
      errors.email = "Enter a valid email address";
    } else if (values.email.length > 255) {
      errors.email = "Maximum 255 characters allowed";
    }

    if (!values.password) {
      errors.password = "Password is required";
    } else if (values.password.length < 8 || values.password.length > 100) {
      errors.password = "Password must contain 8 to 100 characters";
    }
  }

  return errors;
}
