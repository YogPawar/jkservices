import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Stack,
  Typography,
} from "@mui/material";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";

import FarmerForm from "../components/FarmerForm";
import { getFarmer, updateFarmer } from "../api/farmerService";
import appConfig from "../../../app/appConfig";

const EMPTY_FORM = {
  name: "",
  phone: "",
  village: "",
  address: "",
  active: true,
};

export default function EditFarmerPage() {
  const { farmerId } = useParams();
  const navigate = useNavigate();

  const [initialValues, setInitialValues] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [pageError, setPageError] = useState("");
  const [submitError, setSubmitError] = useState("");

  useEffect(() => {
    let isMounted = true;

    async function loadFarmer() {
      setIsLoading(true);
      setPageError("");

      try {
        const response = await getFarmer(appConfig.familyId, farmerId);

        if (!response.success) {
          throw new Error(response.message || "Unable to retrieve farmer");
        }

        if (isMounted) {
          const farmer = response.data;

          setInitialValues({
            ...EMPTY_FORM,
            name: farmer.name ?? "",
            phone: farmer.phone ?? "",
            village: farmer.village ?? "",
            address: farmer.address ?? "",
            active: farmer.active ?? true,
          });
        }
      } catch (error) {
        if (isMounted) {
          setPageError(
            error.response?.data?.detail ||
              error.response?.data?.message ||
              error.message ||
              "Unable to load farmer details",
          );
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    loadFarmer();

    return () => {
      isMounted = false;
    };
  }, [farmerId]);

  async function handleSubmit(farmerData) {
    setIsSubmitting(true);
    setSubmitError("");

    try {
      const response = await updateFarmer(
        appConfig.familyId,
        farmerId,
        farmerData,
      );

      if (!response.success) {
        throw new Error(response.message || "Unable to update farmer");
      }

      navigate("/farmers", {
        replace: true,
        state: {
          successMessage: "Farmer updated successfully",
        },
      });
    } catch (error) {
      setSubmitError(
        error.response?.data?.detail ||
          error.response?.data?.message ||
          error.message ||
          "Something went wrong while updating the farmer",
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
            Edit Farmer
          </Typography>

          <Typography variant="body2" color="text.secondary">
            Update the farmer's information.
          </Typography>
        </Box>
      </Stack>

      {isLoading && (
        <Box sx={{ display: "flex", justifyContent: "center", py: 6 }}>
          <CircularProgress />
        </Box>
      )}

      {!isLoading && pageError && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {pageError}
        </Alert>
      )}

      {!isLoading && initialValues && (
        <FarmerForm
          initialValues={initialValues}
          onSubmit={handleSubmit}
          onCancel={() => navigate("/farmers")}
          isSubmitting={isSubmitting}
          submitError={submitError}
          submitLabel="Update Farmer"
        />
      )}
    </Box>
  );
}
