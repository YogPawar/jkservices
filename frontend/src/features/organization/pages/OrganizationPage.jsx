import { useEffect, useState } from "react";
import {
  Alert,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Stack,
  Typography,
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";

import { useNavigate } from "react-router-dom";
import useOrganization from "../hooks/useOrganization";
import OrganizationTable from "../components/OrganizationTable";

export default function OrganizationPage() {
  const navigate = useNavigate();

  const {
    organizations,
    loading,
    error,
    loadOrganizations,
    deactivateOrganization,
  } = useOrganization();

  const [selectedOrganization, setSelectedOrganization] = useState(null);
  const [actionError, setActionError] = useState("");

  useEffect(() => {
    loadOrganizations().catch(() => {});
  }, [loadOrganizations]);

  const handleDeactivate = async () => {
    if (!selectedOrganization) return;

    setActionError("");

    try {
      await deactivateOrganization(selectedOrganization.id);
      setSelectedOrganization(null);
      await loadOrganizations();
    } catch (err) {
      setActionError(
        err?.response?.data?.detail ||
          err?.response?.data?.message ||
          "Unable to deactivate organization.",
      );
    }
  };

  return (
    <Stack spacing={3}>
      <Stack
        direction={{ xs: "column", sm: "row" }}
        justifyContent="space-between"
        alignItems={{ xs: "stretch", sm: "center" }}
        spacing={2}
      >
        <div>
          <Typography variant="h5" fontWeight={700}>
            Organizations
          </Typography>
          <Typography color="text.secondary">
            Manage your organizations and their details.
          </Typography>
        </div>

        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => navigate("/organizations/new")}
        >
          Create organization
        </Button>
      </Stack>

      {error && <Alert severity="error">{error}</Alert>}
      {actionError && <Alert severity="error">{actionError}</Alert>}

      {loading && !organizations.length ? (
        <Typography color="text.secondary">Loading organizations...</Typography>
      ) : (
        <OrganizationTable
          organizations={organizations}
          onDeactivate={setSelectedOrganization}
        />
      )}

      <Dialog
        open={Boolean(selectedOrganization)}
        onClose={() => setSelectedOrganization(null)}
      >
        <DialogTitle>Deactivate organization?</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to deactivate{" "}
            <strong>{selectedOrganization?.organizationName}</strong>? This
            operation will mark the organization inactive.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setSelectedOrganization(null)}>Cancel</Button>
          <Button color="error" variant="contained" onClick={handleDeactivate}>
            Deactivate
          </Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
}
