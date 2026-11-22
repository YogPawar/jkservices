import { Paper, Stack, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";

import OrganizationForm from "../components/OrganizationForm";
import useOrganization from "../hooks/useOrganization";

export default function AddOrganizationPage() {
  const navigate = useNavigate();
  const { createOrganization, loading } = useOrganization();

  const handleSubmit = async (payload) => {
    await createOrganization(payload);
    navigate("/organizations");
  };

  return (
    <Stack spacing={3} maxWidth="md">
      <Typography variant="h5" fontWeight={700}>
        Create organization
      </Typography>

      <Typography color="text.secondary">
        Create an organization and register its initial owner.
      </Typography>

      <Paper variant="outlined" sx={{ p: { xs: 2, sm: 3 } }}>
        <OrganizationForm
          mode="create"
          onSubmit={handleSubmit}
          loading={loading}
        />
      </Paper>
    </Stack>
  );
}
