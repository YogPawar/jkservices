import { useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  MenuItem,
  Snackbar,
  Stack,
  TextField,
  Typography,
} from "@mui/material";

import { Add, Search, Agriculture } from "@mui/icons-material";

import FarmerTable from "../components/FarmerTable";
import useFarmers from "../hooks/useFarmers";
import appConfig from "../../../app/appConfig";

export default function FarmersPage() {
  const navigate = useNavigate();
  const location = useLocation();

  const { farmers, isLoading, isMutating, error, loadFarmers, removeFarmer } =
    useFarmers(appConfig.familyId);

  const [search, setSearch] = useState("");
  const [status, setStatus] = useState("ALL");
  const [selectedFarmer, setSelectedFarmer] = useState(null);
  const [isConfirmOpen, setIsConfirmOpen] = useState(false);

  const [notification, setNotification] = useState(
    location.state?.successMessage || "",
  );

  const [actionError, setActionError] = useState("");

  const totalFarmers = farmers.length;
  const activeFarmers = farmers.filter((farmer) => farmer.active).length;

  const filteredFarmers = useMemo(() => {
    const searchValue = search.trim().toLowerCase();

    return farmers.filter((farmer) => {
      const matchesSearch =
        !searchValue ||
        [farmer.name, farmer.farmerCode, farmer.phone, farmer.village].some(
          (value) =>
            String(value ?? "")
              .toLowerCase()
              .includes(searchValue),
        );

      const matchesStatus =
        status === "ALL" ||
        (status === "ACTIVE" && farmer.active) ||
        (status === "INACTIVE" && !farmer.active);

      return matchesSearch && matchesStatus;
    });
  }, [farmers, search, status]);

  function handleDeactivateClick(farmer) {
    setSelectedFarmer(farmer);
    setActionError("");
    setIsConfirmOpen(true);
  }

  async function handleConfirmDeactivate() {
    if (!selectedFarmer) return;

    try {
      await removeFarmer(selectedFarmer.id);

      setNotification(
        `${selectedFarmer.name} has been deactivated successfully`,
      );

      setIsConfirmOpen(false);
      setSelectedFarmer(null);
    } catch (err) {
      setActionError(
        err.response?.data?.detail ||
          err.response?.data?.message ||
          err.message ||
          "Unable to deactivate farmer",
      );
    }
  }

  return (
    <Stack spacing={3}>
      {/* Page heading */}
      <Stack
        direction={{ xs: "column", sm: "row" }}
        justifyContent="space-between"
        alignItems={{ sm: "center" }}
        spacing={2}
      >
        <Box>
          <Typography variant="h4" fontWeight={800}>
            Farmers
          </Typography>

          <Typography color="text.secondary" sx={{ mt: 0.5 }}>
            Manage your registered farmers and their details.
          </Typography>
        </Box>

        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={() => navigate("/farmers/new")}
        >
          Add Farmer
        </Button>
      </Stack>

      {/* Error state */}
      {error && (
        <Alert
          severity="error"
          action={
            <Button color="inherit" size="small" onClick={loadFarmers}>
              Retry
            </Button>
          }
        >
          {error.message || "Unable to load farmers"}
        </Alert>
      )}

      {/* Summary cards */}
      <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
        <Card sx={{ flex: 1 }}>
          <CardContent>
            <Typography color="text.secondary" variant="body2">
              Total Farmers
            </Typography>

            <Typography variant="h4" fontWeight={800} sx={{ mt: 1 }}>
              {isLoading ? "—" : totalFarmers}
            </Typography>
          </CardContent>
        </Card>

        <Card sx={{ flex: 1 }}>
          <CardContent>
            <Typography color="text.secondary" variant="body2">
              Active Farmers
            </Typography>

            <Typography
              variant="h4"
              fontWeight={800}
              color="success.main"
              sx={{ mt: 1 }}
            >
              {isLoading ? "—" : activeFarmers}
            </Typography>
          </CardContent>
        </Card>
      </Stack>

      {/* Farmer directory */}
      <Card>
        <CardContent>
          <Stack spacing={2}>
            <Stack
              direction={{ xs: "column", sm: "row" }}
              justifyContent="space-between"
              alignItems={{ sm: "center" }}
              spacing={2}
            >
              <Box>
                <Typography variant="h6" fontWeight={700}>
                  Farmer Directory
                </Typography>

                <Typography variant="body2" color="text.secondary">
                  Search and manage your farmers.
                </Typography>
              </Box>

              <Chip
                label={`${filteredFarmers.length} Farmers`}
                color="success"
                variant="outlined"
              />
            </Stack>

            <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
              <TextField
                fullWidth
                size="small"
                placeholder="Search by name, mobile number, village or farmer code"
                value={search}
                onChange={(event) => setSearch(event.target.value)}
                slotProps={{
                  input: {
                    startAdornment: (
                      <Search sx={{ mr: 1, color: "text.secondary" }} />
                    ),
                  },
                }}
              />

              <TextField
                select
                size="small"
                label="Status"
                value={status}
                onChange={(event) => setStatus(event.target.value)}
                sx={{ minWidth: { sm: 170 } }}
              >
                <MenuItem value="ALL">All Farmers</MenuItem>
                <MenuItem value="ACTIVE">Active</MenuItem>
                <MenuItem value="INACTIVE">Inactive</MenuItem>
              </TextField>
            </Stack>

            {!isLoading && !error && farmers.length === 0 ? (
              <Box
                sx={{
                  py: 6,
                  textAlign: "center",
                  border: "1px dashed",
                  borderColor: "divider",
                  borderRadius: 2,
                }}
              >
                <Agriculture
                  sx={{ fontSize: 48, color: "text.secondary", mb: 1 }}
                />

                <Typography variant="h6" fontWeight={700}>
                  No farmers to display
                </Typography>

                <Typography color="text.secondary" sx={{ mt: 1 }}>
                  Farmers registered in your family workspace will appear here.
                </Typography>

                <Button
                  variant="contained"
                  startIcon={<Add />}
                  sx={{ mt: 2 }}
                  onClick={() => navigate("/farmers/new")}
                >
                  Register First Farmer
                </Button>
              </Box>
            ) : (
              <FarmerTable
                farmers={filteredFarmers}
                isLoading={isLoading}
                isMutating={isMutating}
                onView={(farmer) => navigate(`/farmers/${farmer.id}`)}
                onEdit={(farmer) => navigate(`/farmers/${farmer.id}/edit`)}
                onDeactivate={handleDeactivateClick}
              />
            )}

            {!isLoading &&
              farmers.length > 0 &&
              filteredFarmers.length === 0 && (
                <Alert severity="info">
                  No farmers match your search or selected status.
                </Alert>
              )}
          </Stack>
        </CardContent>
      </Card>

      {/* Deactivation confirmation */}
      <Dialog
        open={isConfirmOpen}
        onClose={() => {
          if (!isMutating) {
            setIsConfirmOpen(false);
            setSelectedFarmer(null);
          }
        }}
      >
        <DialogTitle>Deactivate Farmer?</DialogTitle>

        <DialogContent>
          <DialogContentText>
            Are you sure you want to deactivate{" "}
            <strong>{selectedFarmer?.name}</strong>? The farmer will no longer
            be active, but their existing records will be retained.
          </DialogContentText>

          {actionError && (
            <Alert severity="error" sx={{ mt: 2 }}>
              {actionError}
            </Alert>
          )}
        </DialogContent>

        <DialogActions>
          <Button
            onClick={() => {
              setIsConfirmOpen(false);
              setSelectedFarmer(null);
            }}
            disabled={isMutating}
          >
            Cancel
          </Button>

          <Button
            color="error"
            variant="contained"
            onClick={handleConfirmDeactivate}
            disabled={isMutating}
          >
            Deactivate
          </Button>
        </DialogActions>
      </Dialog>

      {/* Success notification */}
      <Snackbar
        open={Boolean(notification)}
        autoHideDuration={4000}
        onClose={() => setNotification("")}
        message={notification}
        anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
      />
    </Stack>
  );
}
