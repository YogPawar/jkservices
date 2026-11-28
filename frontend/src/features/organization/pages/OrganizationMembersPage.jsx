import { useState } from "react";
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
import AddIcon from "@mui/icons-material/Add";
import useOrganizationMembers from "../hooks/useOrganizationMembers";
import OrganizationMemberForm from "../components/OrganizationMemberForm";
import OrganizationMemberTable from "../components/OrganizationMemberTable";

const OrganizationMembersPage = () => {
  const { organizationId } = useParams();
  const navigate = useNavigate();

  const { members, loading, error, addMember, updateMember, removeMember } =
    useOrganizationMembers(organizationId);

  const [dialogOpen, setDialogOpen] = useState(false);
  const [selectedMember, setSelectedMember] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [actionError, setActionError] = useState("");

  const openAddDialog = () => {
    setSelectedMember(null);
    setActionError("");
    setDialogOpen(true);
  };

  const openEditDialog = (member) => {
    setSelectedMember(member);
    setActionError("");
    setDialogOpen(true);
  };

  const closeDialog = () => {
    if (submitting) return;
    setDialogOpen(false);
    setSelectedMember(null);
  };

  const handleSubmit = async (payload) => {
    setSubmitting(true);
    setActionError("");

    try {
      if (selectedMember) {
        await updateMember(selectedMember.id, payload);
      } else {
        await addMember(payload);
      }
    } catch (err) {
      setActionError(
        err.response?.data?.detail ||
          err.response?.data?.message ||
          "Unable to save organization membership.",
      );
      throw err;
    } finally {
      setSubmitting(false);
    }
  };

  const handleRemove = async (member) => {
    const confirmed = window.confirm(
      `Remove user ${member.userId} from this organization? This will not delete the platform user.`,
    );

    if (!confirmed) return;

    setActionError("");

    try {
      await removeMember(member.id);
    } catch (err) {
      setActionError(
        err.response?.data?.detail ||
          err.response?.data?.message ||
          "Unable to remove organization membership.",
      );
    }
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
          <Button
            startIcon={<ArrowBackIcon />}
            onClick={() => navigate(`/organizations/${organizationId}`)}
            sx={{ mb: 1 }}
          >
            Back to Organization
          </Button>

          <Typography variant="h4" fontWeight={600}>
            Organization Members
          </Typography>

          <Typography color="text.secondary">
            Manage users who have access to this organization.
          </Typography>
        </Box>

        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={openAddDialog}
        >
          Add Member
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

      {loading && members.length === 0 ? (
        <Box display="flex" justifyContent="center" p={4}>
          <CircularProgress />
        </Box>
      ) : (
        <OrganizationMemberTable
          members={members}
          loading={loading}
          onEdit={openEditDialog}
          onRemove={handleRemove}
        />
      )}

      <OrganizationMemberForm
        open={dialogOpen}
        onClose={closeDialog}
        onSubmit={handleSubmit}
        member={selectedMember}
        submitting={submitting}
      />
    </Box>
  );
};

export default OrganizationMembersPage;
