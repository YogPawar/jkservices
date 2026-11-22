import { useEffect } from "react";
import {
  Alert,
  CircularProgress,
  Paper,
  Stack,
  Typography,
} from "@mui/material";

import { useNavigate, useParams } from "react-router-dom";

import OrganizationForm from "../components/OrganizationForm";
import useOrganization from "../hooks/useOrganization";

export default function EditOrganizationPage() {
  const { organizationId } = useParams();
  const navigate = useNavigate();

  const { organization, loading, error, loadOrganization, updateOrganization } =
    useOrganization();

  useEffect(() => {
    loadOrganization(organizationId).catch(() => {});
  }, [organizationId, loadOrganization]);

  const handleSubmit = async (payload) => {
    await updateOrganization(organizationId, payload);
    navigate(`/organizations/${organizationId}`);
  };

  if (loading && !organization) return <CircularProgress />;
  if (error) return <Alert severity="error">{error}</Alert>;
  if (!organization) return null;

  return (
    <Stack spacing={3} maxWidth="md">
      <Typography variant="h5" fontWeight={700}>
        Edit organization
      </Typography>

      <Paper variant="outlined" sx={{ p: { xs: 2, sm: 3 } }}>
        <OrganizationForm
          mode="edit"
          initialData={organization}
          onSubmit={handleSubmit}
          loading={loading}
        />
      </Paper>
    </Stack>
  );
}
