import { useState } from "react";
import {
  Alert,
  Box,
  Button,
  Stack,
  TextField,
  FormControlLabel,
  Switch,
} from "@mui/material";

import { validateOrganization } from "../schemas/organizationSchema";

const initialValues = {
  organizationName: "",
  fullName: "",
  email: "",
  password: "",
};

export default function OrganizationForm({
  mode = "create",
  initialData = {},
  onSubmit,
  loading = false,
}) {
  const [values, setValues] = useState(() => ({
    ...initialValues,
    organizationName: initialData.organizationName ?? "",
    active: initialData.active ?? true,
  }));

  const [errors, setErrors] = useState({});
  const [submitError, setSubmitError] = useState("");

  const isCreate = mode === "create";

  const handleChange = (event) => {
    const { name, value } = event.target;

    setValues((previous) => ({
      ...previous,
      [name]: value,
    }));

    setErrors((previous) => ({
      ...previous,
      [name]: "",
    }));

    setSubmitError("");
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const validationErrors = validateOrganization(values, mode);
    setErrors(validationErrors);

    if (Object.keys(validationErrors).length > 0) {
      return;
    }

    setSubmitError("");

    try {
      const payload = isCreate
        ? {
            organizationName: values.organizationName.trim(),
            fullName: values.fullName.trim(),
            email: values.email.trim(),
            password: values.password,
          }
        : {
            organizationName: values.organizationName.trim(),
            active: values.active,
          };

      await onSubmit(payload);
    } catch (error) {
      setSubmitError(
        error?.response?.data?.detail ||
          error?.response?.data?.message ||
          "Unable to save organization.",
      );
    }
  };

  return (
    <Box component="form" onSubmit={handleSubmit} noValidate>
      <Stack spacing={2.5}>
        {submitError && <Alert severity="error">{submitError}</Alert>}

        <TextField
          label="Organization name"
          name="organizationName"
          value={values.organizationName}
          onChange={handleChange}
          required
          fullWidth
          inputProps={{ maxLength: 150 }}
          error={Boolean(errors.organizationName)}
          helperText={errors.organizationName}
        />

        {!isCreate && (
          <FormControlLabel
            control={
              <Switch
                checked={values.active}
                onChange={(event) =>
                  setValues((previous) => ({
                    ...previous,
                    active: event.target.checked,
                  }))
                }
                color="success"
              />
            }
            label={
              values.active ? "Active organization" : "Inactive organization"
            }
          />
        )}

        {isCreate && (
          <>
            <TextField
              label="Owner full name"
              name="fullName"
              value={values.fullName}
              onChange={handleChange}
              required
              fullWidth
              inputProps={{ maxLength: 150 }}
              error={Boolean(errors.fullName)}
              helperText={errors.fullName}
            />

            <TextField
              label="Owner email"
              name="email"
              type="email"
              value={values.email}
              onChange={handleChange}
              required
              fullWidth
              inputProps={{ maxLength: 255 }}
              error={Boolean(errors.email)}
              helperText={errors.email}
            />

            <TextField
              label="Password"
              name="password"
              type="password"
              value={values.password}
              onChange={handleChange}
              required
              fullWidth
              inputProps={{ maxLength: 100 }}
              error={Boolean(errors.password)}
              helperText={errors.password || "Minimum 8 characters"}
            />
          </>
        )}

        <Stack direction="row" justifyContent="flex-end" spacing={2}>
          <Button type="submit" variant="contained" disabled={loading}>
            {loading
              ? "Saving..."
              : isCreate
                ? "Create organization"
                : "Save changes"}
          </Button>
        </Stack>
      </Stack>
    </Box>
  );
}
