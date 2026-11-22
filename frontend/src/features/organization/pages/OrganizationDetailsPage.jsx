import { useEffect } from "react";
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Divider,
  Paper,
  Stack,
  Typography,
} from "@mui/material";

import { useNavigate, useParams } from "react-router-dom";

import useOrganization from "../hooks/useOrganization";
import OrganizationStatusChip from "../components/OrganizationStatusChip";

export default function OrganizationDetailsPage() {
  const { organizationId } = useParams();
  const navigate = useNavigate();

  const { organization, loading, error, loadOrganization } = useOrganization();

  useEffect(() => {
    loadOrganization(organizationId).catch(() => {});
  }, [organizationId, loadOrganization]);

  if (loading && !organization) return <CircularProgress />;
  if (error) return <Alert severity="error">{error}</Alert>;
  if (!organization) return null;

  return (
    <Stack spacing={3} maxWidth="md">
      <Stack
        direction={{ xs: "column", sm: "row" }}
        justifyContent="space-between"
        spacing={2}
      >
        <Box>
          <Typography variant="h5" fontWeight={700}>
            {organization.organizationName}
          </Typography>
          <Typography color="text.secondary">Organization details</Typography>
        </Box>

        {organization.active && (
          <Button
            variant="contained"
            onClick={() => navigate(`/organizations/${organizationId}/edit`)}
          >
            Edit organization
          </Button>
        )}
      </Stack>

      <Paper variant="outlined" sx={{ p: 3 }}>
        <Stack spacing={2}>
          <Box>
            <Typography variant="body2" color="text.secondary">
              Organization name
            </Typography>
            <Typography fontWeight={600}>
              {organization.organizationName}
            </Typography>
          </Box>

          <Divider />

          <Box>
            <Typography variant="body2" color="text.secondary">
              Organization ID
            </Typography>
            <Typography>{organization.id}</Typography>
          </Box>

          <Divider />

          <Box>
            <Typography variant="body2" color="text.secondary">
              Invite code
            </Typography>
            <Typography>{organization.inviteCode || "—"}</Typography>
          </Box>

          <Divider />

          <Box>
            <Typography variant="body2" color="text.secondary">
              Status
            </Typography>
            <OrganizationStatusChip active={organization.active} />
          </Box>

          <Divider />

          <Box>
            <Typography variant="body2" color="text.secondary">
              Created at
            </Typography>
            <Typography>
              {organization.createdAt
                ? new Date(organization.createdAt).toLocaleString()
                : "—"}
            </Typography>
          </Box>

          <Box>
            <Typography variant="body2" color="text.secondary">
              Last updated
            </Typography>
            <Typography>
              {organization.updatedAt
                ? new Date(organization.updatedAt).toLocaleString()
                : "—"}
            </Typography>
          </Box>
        </Stack>
      </Paper>
    </Stack>
  );
}
