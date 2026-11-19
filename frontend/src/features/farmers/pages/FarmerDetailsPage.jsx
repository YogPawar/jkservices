import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  CircularProgress,
  Divider,
  Grid,
  Stack,
  Typography,
} from "@mui/material";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import EditIcon from "@mui/icons-material/Edit";

import { getFarmer } from "../api/farmerService";
import appConfig from "../../../app/appConfig";

function DetailItem({ label, value }) {
  return (
    <Box>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>
        {label}
      </Typography>

      <Typography variant="body1" fontWeight={600}>
        {value || "—"}
      </Typography>
    </Box>
  );
}

export default function FarmerDetailsPage() {
  const { farmerId } = useParams();
  const navigate = useNavigate();

  const [farmer, setFarmer] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let isMounted = true;

    async function loadFarmer() {
      setIsLoading(true);
      setError("");

      try {
        const response = await getFarmer(appConfig.businessId, farmerId);

        if (!response.success) {
          throw new Error(response.message || "Unable to retrieve farmer");
        }

        if (isMounted) {
          setFarmer(response.data);
        }
      } catch (err) {
        if (isMounted) {
          setError(
            err.response?.data?.detail ||
              err.response?.data?.message ||
              err.message ||
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

  if (isLoading) {
    return (
      <Box sx={{ display: "flex", justifyContent: "center", py: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Stack spacing={3}>
      <Stack
        direction={{ xs: "column", sm: "row" }}
        justifyContent="space-between"
        alignItems={{ sm: "center" }}
        spacing={2}
      >
        <Box>
          <Typography variant="h5" fontWeight={800}>
            Farmer Details
          </Typography>

          <Typography color="text.secondary" sx={{ mt: 0.5 }}>
            View registered farmer information.
          </Typography>
        </Box>

        <Stack direction="row" spacing={1}>
          <Button
            variant="outlined"
            color="inherit"
            startIcon={<ArrowBackIcon />}
            onClick={() => navigate("/farmers")}
          >
            Back
          </Button>

          {farmer && (
            <Button
              variant="contained"
              startIcon={<EditIcon />}
              onClick={() => navigate(`/farmers/${farmer.id}/edit`)}
            >
              Edit Farmer
            </Button>
          )}
        </Stack>
      </Stack>

      {error && <Alert severity="error">{error}</Alert>}

      {farmer && (
        <Card>
          <CardContent>
            <Stack spacing={3}>
              <Stack
                direction={{ xs: "column", sm: "row" }}
                justifyContent="space-between"
                alignItems={{ sm: "center" }}
                spacing={2}
              >
                <Box>
                  <Typography variant="h5" fontWeight={800}>
                    {farmer.name}
                  </Typography>

                  <Typography color="text.secondary" sx={{ mt: 0.5 }}>
                    Farmer Code: {farmer.farmerCode || "—"}
                  </Typography>
                </Box>

                <Chip
                  label={farmer.active ? "Active" : "Inactive"}
                  color={farmer.active ? "success" : "default"}
                  variant="outlined"
                />
              </Stack>

              <Divider />

              <Grid container spacing={3}>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <DetailItem label="Farmer Name" value={farmer.name} />
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                  <DetailItem label="Mobile Number" value={farmer.phone} />
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                  <DetailItem label="Village" value={farmer.village} />
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                  <DetailItem
                    label="Status"
                    value={farmer.active ? "Active" : "Inactive"}
                  />
                </Grid>

                <Grid size={{ xs: 12 }}>
                  <DetailItem label="Address" value={farmer.address} />
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                  <DetailItem
                    label="Registered On"
                    value={
                      farmer.createdAt
                        ? new Date(farmer.createdAt).toLocaleString()
                        : "—"
                    }
                  />
                </Grid>
              </Grid>
            </Stack>
          </CardContent>
        </Card>
      )}
    </Stack>
  );
}
