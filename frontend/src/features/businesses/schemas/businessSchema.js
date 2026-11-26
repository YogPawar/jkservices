export const BUSINESS_TYPES = [
  { value: "FARMING_SERVICES", label: "Farming Services" },
  { value: "EVENT_MANAGEMENT", label: "Event Management" },
];

export const BUSINESS_INITIAL_VALUES = {
  businessName: "",
  businessType: "FARMING_SERVICES",
  businessCode: "",
  contactNumber: "",
  email: "",
  address: "",
  currencyCode: "INR",
};

export const validateBusiness = (values) => {
  const errors = {};

  if (!values.businessName?.trim()) {
    errors.businessName = "Business name is required";
  } else if (values.businessName.trim().length > 150) {
    errors.businessName = "Business name cannot exceed 150 characters";
  }

  if (!values.businessType) {
    errors.businessType = "Business type is required";
  } else if (
    !BUSINESS_TYPES.some((type) => type.value === values.businessType)
  ) {
    errors.businessType = "Select a valid business type";
  }

  if (!values.businessCode?.trim()) {
    errors.businessCode = "Business code is required";
  } else if (values.businessCode.trim().length > 50) {
    errors.businessCode = "Business code cannot exceed 50 characters";
  }

  if (values.contactNumber && values.contactNumber.length > 20) {
    errors.contactNumber = "Contact number cannot exceed 20 characters";
  }

  if (values.email) {
    if (values.email.length > 255) {
      errors.email = "Email cannot exceed 255 characters";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(values.email)) {
      errors.email = "Enter a valid email address";
    }
  }

  if (!values.currencyCode?.trim()) {
    errors.currencyCode = "Currency code is required";
  } else if (!/^[A-Za-z]{3}$/.test(values.currencyCode.trim())) {
    errors.currencyCode = "Enter a valid 3-letter currency code";
  }

  return errors;
};
