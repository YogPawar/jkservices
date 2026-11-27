import { useState } from "react";
import {
  Alert,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Paper,
  Stack,
  Typography,
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";

import { useNavigate } from "react-router-dom";
import { userApi } from "../api/userApi";
import useUsers from "../hooks/useUsers";
import UserTable from "../components/UserTable";

export default function UsersPage() {
  const navigate = useNavigate();

  const [page, setPage] = useState(0);
  const [size, setSize] = useState(20);
  const [statusUser, setStatusUser] = useState(null);
  const [statusUpdatingId, setStatusUpdatingId] = useState(null);
  const [actionError, setActionError] = useState("");

  const { users, pagination, loading, error, refresh } = useUsers(page, size);

  const handleStatusChange = async () => {
    if (!statusUser) return;

    try {
      setStatusUpdatingId(statusUser.id);
      setActionError("");

      await userApi.updateUserStatus(statusUser.id, !statusUser.active);

      setStatusUser(null);
      await refresh();
    } catch (err) {
      setActionError(
        err.response?.data?.detail ||
          err.response?.data?.message ||
          "Unable to update user status.",
      );
    } finally {
      setStatusUpdatingId(null);
    }
  };

  const handlePageSizeChange = (newSize) => {
    setSize(newSize);
    setPage(0);
  };

  return (
    <Box>
      <Stack
        direction={{ xs: "column", sm: "row" }}
        justifyContent="space-between"
        alignItems={{ xs: "stretch", sm: "center" }}
        spacing={2}
        mb={3}
      >
        <Box>
          <Typography variant="h4" fontWeight={600}>
            User Management
          </Typography>
          <Typography color="text.secondary" mt={0.5}>
            Create and manage platform user accounts.
          </Typography>
        </Box>

        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => navigate("/users/new")}
        >
          Create user
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

      <Paper variant="outlined">
        {loading ? (
          <Box p={4} textAlign="center">
            <Typography color="text.secondary">Loading users...</Typography>
          </Box>
        ) : (
          <UserTable
            users={users}
            pagination={pagination}
            onPageChange={setPage}
            onPageSizeChange={handlePageSizeChange}
            onView={(id) => navigate(`/users/${id}`)}
            onEdit={(id) => navigate(`/users/${id}/edit`)}
            onToggleStatus={setStatusUser}
            statusUpdatingId={statusUpdatingId}
          />
        )}
      </Paper>

      <Dialog open={Boolean(statusUser)} onClose={() => setStatusUser(null)}>
        <DialogTitle>
          {statusUser?.active ? "Deactivate user?" : "Activate user?"}
        </DialogTitle>

        <DialogContent>
          <DialogContentText>
            {statusUser?.active
              ? `Deactivate ${statusUser?.fullName}? They will no longer be able to use their account. Existing organization and business memberships will remain unchanged.`
              : `Activate ${statusUser?.fullName}?`}
          </DialogContentText>
        </DialogContent>

        <DialogActions>
          <Button
            onClick={() => setStatusUser(null)}
            disabled={Boolean(statusUpdatingId)}
          >
            Cancel
          </Button>

          <Button
            variant="contained"
            color={statusUser?.active ? "error" : "success"}
            onClick={handleStatusChange}
            disabled={Boolean(statusUpdatingId)}
          >
            {statusUser?.active ? "Deactivate" : "Activate"}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
