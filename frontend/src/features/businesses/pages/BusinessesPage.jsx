import { useCallback, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Stack,
  Typography,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import PageContainer from "../../../components/common/PageContainer";
import PageHeader from "../../../components/common/PageHeader";
import BusinessTable from "../components/BusinessTable";
import useBusinesses from "../hooks/useBusinesses";

const BusinessesPage = () => {
  const { organizationId } = useParams();
  const navigate = useNavigate();

  const { businesses, loading, error, loadBusinesses, deactivateBusiness } =
    useBusinesses();

  const [selectedBusiness, setSelectedBusiness] = useState(null);
  const [actionError, setActionError] = useState("");

  const basePath = `/organizations/${organizationId}/businesses`;

  const refresh = useCallback(() => {
    loadBusinesses(organizationId).catch(() => {});
  }, [loadBusinesses, organizationId]);

  useEffect(() => {
    refresh();
  }, [refresh]);

  const handleDeactivate = async () => {
    if (!selectedBusiness) return;

    try {
      setActionError("");
      await deactivateBusiness(organizationId, selectedBusiness.id);
      setSelectedBusiness(null);
      refresh();
    } catch (err) {
      setActionError(
        err.response?.data?.detail ||
          err.response?.data?.message ||
          "Unable to deactivate business.",
      );
    }
  };

  return (
    <PageContainer>
      <PageHeader
        title="Businesses"
        subtitle="Manage businesses belonging to this organization"
      />

      <Stack direction="row" justifyContent="flex-end" sx={{ mb: 2 }}>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => navigate(`${basePath}/new`)}
        >
          Add Business
        </Button>
      </Stack>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}
      {actionError && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {actionError}
        </Alert>
      )}

      {loading && businesses.length === 0 ? (
        <Box display="flex" justifyContent="center" p={4}>
          <CircularProgress />
        </Box>
      ) : (
        <BusinessTable
          businesses={businesses}
          onView={(id) => navigate(`${basePath}/${id}`)}
          onEdit={(id) => navigate(`${basePath}/${id}/edit`)}
          onDeactivate={setSelectedBusiness}
        />
      )}

      <Dialog
        open={!!selectedBusiness}
        onClose={() => setSelectedBusiness(null)}
      >
        <DialogTitle>Deactivate Business?</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to deactivate{" "}
            <strong>{selectedBusiness?.businessName}</strong>? This business
            will no longer be active.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setSelectedBusiness(null)}>Cancel</Button>
          <Button color="error" onClick={handleDeactivate} disabled={loading}>
            Deactivate
          </Button>
        </DialogActions>
      </Dialog>
    </PageContainer>
  );
};

export default BusinessesPage;
