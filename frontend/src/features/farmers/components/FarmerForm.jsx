import { useEffect, useState } from "react";
import {
  Alert,
  Box,
  Button,
  FormControl,
  FormControlLabel,
  FormHelperText,
  InputLabel,
  MenuItem,
  Paper,
  Select,
  Stack,
  TextField,
} from "@mui/material";

import { validateFarmer } from "../schemas/FarmerSchema";

const EMPTY_FORM = {
  name: "",
  phone: "",
  village: "",
  address: "",
  active: true,
};

export default function FarmerForm({
  initialValues,
  onSubmit,
  onCancel,
  isSubmitting = false,
  submitLabel = "Save Farmer",
  submitError = "",
}) {
  const [values, setValues] = useState(EMPTY_FORM);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    setValues({ ...EMPTY_FORM, ...initialValues });
    setErrors({});
  }, [initialValues]);

  function handleChange(event) {
    const { name, value } = event.target;

    setValues((previous) => ({
      ...previous,
      [name]: value,
    }));

    setErrors((previous) => ({
      ...previous,
      [name]: "",
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    const validationErrors = validateFarmer(values);
    setErrors(validationErrors);

    if (Object.keys(validationErrors).length > 0) {
      return;
    }

    const payload = {
      name: values.name.trim(),
      phone: values.phone.trim() || null,
      village: values.village.trim() || null,
      address: values.address.trim() || null,
      active: values.active,
    };

    await onSubmit(payload);
  }

  return (
    <Paper variant="outlined" sx={{ p: 3 }}>
      {submitError && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {submitError}
        </Alert>
      )}

      <Box component="form" onSubmit={handleSubmit} noValidate>
        <Stack spacing={2.5}>
          <TextField
            name="name"
            label="Farmer Name"
            value={values.name}
            onChange={handleChange}
            required
            fullWidth
            inputProps={{ maxLength: 150 }}
            error={Boolean(errors.name)}
            helperText={errors.name}
          />

          <TextField
            name="phone"
            label="Mobile Number"
            value={values.phone}
            onChange={handleChange}
            fullWidth
            inputProps={{ maxLength: 20 }}
            error={Boolean(errors.phone)}
            helperText={errors.phone || "Enter a 10-digit Indian mobile number"}
          />

          <TextField
            name="village"
            label="Village"
            value={values.village}
            onChange={handleChange}
            fullWidth
            inputProps={{ maxLength: 100 }}
            error={Boolean(errors.village)}
            helperText={errors.village}
          />

          <TextField
            name="address"
            label="Address"
            value={values.address}
            onChange={handleChange}
            fullWidth
            multiline
            minRows={3}
          />

          <FormControl fullWidth error={Boolean(errors.active)}>
            <InputLabel id="farmer-status-label">Status</InputLabel>
            <Select
              labelId="farmer-status-label"
              name="active"
              value={values.active}
              label="Status"
              onChange={(event) =>
                handleChange({
                  target: {
                    name: "active",
                    value: event.target.value === "true",
                  },
                })
              }
            >
              <MenuItem value="true">Active</MenuItem>
              <MenuItem value="false">Inactive</MenuItem>
            </Select>
            {errors.active && <FormHelperText>{errors.active}</FormHelperText>}
          </FormControl>

          <Stack direction="row" spacing={2} justifyContent="flex-end">
            <Button
              variant="outlined"
              onClick={onCancel}
              disabled={isSubmitting}
            >
              Cancel
            </Button>

            <Button type="submit" variant="contained" disabled={isSubmitting}>
              {isSubmitting ? "Saving..." : submitLabel}
            </Button>
          </Stack>
        </Stack>
      </Box>
    </Paper>
  );
}
