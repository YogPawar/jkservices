import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Box, Button, Stack, Typography } from "@mui/material";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";

import FarmerForm from "../components/FarmerForm";
import { createFarmer } from "../api/farmerService";
import appConfig from "../../../app/appConfig";

export default function AddFarmerPage() {
  const navigate = useNavigate();

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState("");

  async function handleSubmit(farmerData) {
    setIsSubmitting(true);
    setSubmitError("");

    try {
      const response = await createFarmer(appConfig.familyId, farmerData);

      if (!response.success) {
        throw new Error(response.message || "Unable to create farmer");
      }

      navigate("/farmers", {
        replace: true,
        state: {
          successMessage: "Farmer created successfully",
        },
      });
    } catch (error) {
      const problem = error.response?.data;

      setSubmitError(
        problem?.detail ||
          problem?.message ||
          error.message ||
          "Something went wrong while creating the farmer",
      );
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <Box>
      <Stack direction="row" alignItems="center" spacing={2} sx={{ mb: 3 }}>
        <Button
          variant="outlined"
          color="inherit"
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate("/farmers")}
        >
          Back
        </Button>

        <Box>
          <Typography variant="h5" fontWeight={700}>
            Add Farmer
          </Typography>

          <Typography variant="body2" color="text.secondary">
            Register a farmer in your family workspace.
          </Typography>
        </Box>
      </Stack>

      <FarmerForm
        onSubmit={handleSubmit}
        onCancel={() => navigate("/farmers")}
        isSubmitting={isSubmitting}
        submitError={submitError}
        submitLabel="Save Farmer"
      />
    </Box>
  );
}
