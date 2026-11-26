import { useEffect, useState } from "react";
import {
  Alert,
  Box,
  Button,
  Grid,
  MenuItem,
  Stack,
  TextField,
} from "@mui/material";
import {
  BUSINESS_INITIAL_VALUES,
  BUSINESS_TYPES,
  validateBusiness,
} from "../schemas/businessSchema";

const BusinessForm = ({
  initialData,
  onSubmit,
  onCancel,
  loading = false,
  submitLabel = "Save Business",
}) => {
  const [values, setValues] = useState(BUSINESS_INITIAL_VALUES);
  const [errors, setErrors] = useState({});
  const [submitError, setSubmitError] = useState("");

  useEffect(() => {
    if (initialData) {
      setValues({
        ...BUSINESS_INITIAL_VALUES,
        ...initialData,
        contactNumber: initialData.contactNumber || "",
        email: initialData.email || "",
        address: initialData.address || "",
      });
    }
  }, [initialData]);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setValues((current) => ({
      ...current,
      [name]: name === "currencyCode" ? value.toUpperCase() : value,
    }));

    setErrors((current) => ({
      ...current,
      [name]: undefined,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSubmitError("");

    const validationErrors = validateBusiness(values);
    setErrors(validationErrors);

    if (Object.keys(validationErrors).length > 0) {
      return;
    }

    const payload = {
      ...values,
      businessName: values.businessName.trim(),
      businessCode: values.businessCode.trim(),
      contactNumber: values.contactNumber.trim() || null,
      email: values.email.trim() || null,
      address: values.address.trim() || null,
      currencyCode: values.currencyCode.trim().toUpperCase(),
    };

    try {
      await onSubmit(payload);
    } catch (error) {
      setSubmitError(
        error.response?.data?.detail ||
          error.response?.data?.message ||
          "Unable to save business. Please try again.",
      );
    }
  };

  return (
    <Box component="form" onSubmit={handleSubmit} noValidate>
      <Stack spacing={3}>
        {submitError && <Alert severity="error">{submitError}</Alert>}

        <Grid container spacing={2}>
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              required
              name="businessName"
              label="Business Name"
              value={values.businessName}
              onChange={handleChange}
              error={!!errors.businessName}
              helperText={errors.businessName}
              inputProps={{ maxLength: 150 }}
            />
          </Grid>

          <Grid item xs={12} md={6}>
            <TextField
              select
              fullWidth
              required
              name="businessType"
              label="Business Type"
              value={values.businessType}
              onChange={handleChange}
              error={!!errors.businessType}
              helperText={errors.businessType}
            >
              {BUSINESS_TYPES.map((type) => (
                <MenuItem key={type.value} value={type.value}>
                  {type.label}
                </MenuItem>
              ))}
            </TextField>
          </Grid>

          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              required
              name="businessCode"
              label="Business Code"
              value={values.businessCode}
              onChange={handleChange}
              error={!!errors.businessCode}
              helperText={errors.businessCode}
              inputProps={{ maxLength: 50 }}
            />
          </Grid>

          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              name="contactNumber"
              label="Contact Number"
              value={values.contactNumber}
              onChange={handleChange}
              error={!!errors.contactNumber}
              helperText={errors.contactNumber}
              inputProps={{ maxLength: 20 }}
            />
          </Grid>

          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              name="email"
              label="Email"
              type="email"
              value={values.email}
              onChange={handleChange}
              error={!!errors.email}
              helperText={errors.email}
              inputProps={{ maxLength: 255 }}
            />
          </Grid>

          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              required
              name="currencyCode"
              label="Currency Code"
              value={values.currencyCode}
              onChange={handleChange}
              error={!!errors.currencyCode}
              helperText={errors.currencyCode || "Example: INR"}
              inputProps={{ maxLength: 3 }}
            />
          </Grid>

          <Grid item xs={12}>
            <TextField
              fullWidth
              multiline
              minRows={3}
              name="address"
              label="Address"
              value={values.address}
              onChange={handleChange}
            />
          </Grid>
        </Grid>

        <Stack direction="row" spacing={2} justifyContent="flex-end">
          <Button variant="outlined" onClick={onCancel} disabled={loading}>
            Cancel
          </Button>

          <Button type="submit" variant="contained" disabled={loading}>
            {loading ? "Saving..." : submitLabel}
          </Button>
        </Stack>
      </Stack>
    </Box>
  );
};

export default BusinessForm;
