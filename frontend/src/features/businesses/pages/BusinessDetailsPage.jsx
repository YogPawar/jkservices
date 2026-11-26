import { useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Grid,
  Paper,
  Stack,
  Typography,
} from "@mui/material";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import PageContainer from "../../../components/common/PageContainer";
import PageHeader from "../../../components/common/PageHeader";
import BusinessStatusChip from "../components/BusinessStatusChip";
import useBusinesses from "../hooks/useBusinesses";

const BusinessDetailsPage = () => {
  const { organizationId, businessId } = useParams();
  const navigate = useNavigate();

  const { business, loading, error, loadBusiness } = useBusinesses();

  useEffect(() => {
    loadBusiness(organizationId, businessId).catch(() => {});
  }, [loadBusiness, organizationId, businessId]);

  if (loading && !business) {
    return (
      <PageContainer>
        <Box display="flex" justifyContent="center" p={4}>
          <CircularProgress />
        </Box>
      </PageContainer>
    );
  }

  if (error || !business) {
    return (
      <PageContainer>
        <Alert severity="error">{error || "Business not found."}</Alert>
        <Button
          sx={{ mt: 2 }}
          startIcon={<ArrowBackIcon />}
          onClick={() =>
            navigate(`/organizations/${organizationId}/businesses`)
          }
        >
          Back to Businesses
        </Button>
      </PageContainer>
    );
  }

  const fields = [
    ["Business Name", business.businessName],
    ["Business Code", business.businessCode],
    [
      "Business Type",
      business.businessType === "FARMING_SERVICES"
        ? "Farming Services"
        : "Event Management",
    ],
    ["Contact Number", business.contactNumber],
    ["Email", business.email],
    ["Currency", business.currencyCode],
    ["Address", business.address],
    ["Created At", business.createdAt],
    ["Last Updated", business.updatedAt],
  ];

  return (
    <PageContainer>
      <PageHeader title={business.businessName} subtitle="Business details" />

      <Stack
        direction="row"
        spacing={2}
        sx={{ mb: 3 }}
        justifyContent="space-between"
      >
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() =>
            navigate(`/organizations/${organizationId}/businesses`)
          }
        >
          Back to Businesses
        </Button>

        {business.active && (
          <Button
            variant="contained"
            startIcon={<EditOutlinedIcon />}
            onClick={() =>
              navigate(
                `/organizations/${organizationId}/businesses/${businessId}/edit`,
              )
            }
          >
            Edit Business
          </Button>
        )}
      </Stack>

      <Paper variant="outlined" sx={{ p: 3 }}>
        <Stack spacing={3}>
          <Box>
            <Typography variant="subtitle2" color="text.secondary">
              Status
            </Typography>
            <BusinessStatusChip active={business.active} />
          </Box>

          <Grid container spacing={3}>
            {fields.map(([label, value]) => (
              <Grid item xs={12} md={6} key={label}>
                <Typography variant="subtitle2" color="text.secondary">
                  {label}
                </Typography>
                <Typography variant="body1">{value || "—"}</Typography>
              </Grid>
            ))}
          </Grid>
        </Stack>
      </Paper>
    </PageContainer>
  );
};

export default BusinessDetailsPage;
