import { useEffect, useState } from "react";
import { Alert, Box, Button, Stack, TextField } from "@mui/material";

import LoadingButton from "../../../components/common/LoadingButton";
import { validateCreateUser, validateUpdateUser } from "../schemas/userSchema";

const EMPTY_VALUES = {
  fullName: "",
  email: "",
  password: "",
};

export default function UserForm({
  mode = "create",
  initialValues = EMPTY_VALUES,
  onSubmit,
  submitting = false,
  error = "",
}) {
  const isCreate = mode === "create";

  const [values, setValues] = useState(EMPTY_VALUES);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    setValues({
      fullName: initialValues.fullName ?? "",
      email: initialValues.email ?? "",
      password: "",
    });
  }, [initialValues]);

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
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const validationErrors = isCreate
      ? validateCreateUser(values)
      : validateUpdateUser(values);

    setErrors(validationErrors);

    if (Object.keys(validationErrors).length > 0) {
      return;
    }

    const payload = isCreate
      ? {
          fullName: values.fullName.trim(),
          email: values.email.trim().toLowerCase(),
          password: values.password,
        }
      : {
          fullName: values.fullName.trim(),
        };

    await onSubmit(payload);
  };

  return (
    <Box component="form" onSubmit={handleSubmit} noValidate>
      <Stack spacing={3}>
        {error && <Alert severity="error">{error}</Alert>}

        <TextField
          name="fullName"
          label="Full name"
          value={values.fullName}
          onChange={handleChange}
          error={Boolean(errors.fullName)}
          helperText={errors.fullName}
          required
          fullWidth
          inputProps={{ maxLength: 150 }}
        />

        <TextField
          name="email"
          label="Email address"
          type="email"
          value={values.email}
          onChange={handleChange}
          error={Boolean(errors.email)}
          helperText={errors.email}
          required
          fullWidth
          disabled={!isCreate}
          inputProps={{ maxLength: 255 }}
        />

        {isCreate && (
          <TextField
            name="password"
            label="Initial password"
            type="password"
            value={values.password}
            onChange={handleChange}
            error={Boolean(errors.password)}
            helperText={errors.password || "Minimum 12 characters"}
            required
            fullWidth
            autoComplete="new-password"
            inputProps={{ minLength: 12, maxLength: 72 }}
          />
        )}

        <Stack direction="row" spacing={2}>
          <LoadingButton type="submit" loading={submitting} variant="contained">
            {isCreate ? "Create user" : "Save changes"}
          </LoadingButton>

          <Button
            type="button"
            color="inherit"
            href="/users"
            disabled={submitting}
          >
            Cancel
          </Button>
        </Stack>
      </Stack>
    </Box>
  );
}
