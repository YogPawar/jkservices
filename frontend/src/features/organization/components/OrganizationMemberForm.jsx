import { useEffect, useState } from "react";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  Stack,
  TextField,
} from "@mui/material";

const ROLES = ["OWNER", "ADMIN", "MEMBER"];
const STATUSES = ["ACTIVE", "SUSPENDED"];

const OrganizationMemberForm = ({
  open,
  onClose,
  onSubmit,
  member = null,
  submitting = false,
}) => {
  const isEdit = Boolean(member);

  const [userId, setUserId] = useState("");
  const [role, setRole] = useState("MEMBER");
  const [membershipStatus, setMembershipStatus] = useState("ACTIVE");
  const [validationError, setValidationError] = useState("");

  useEffect(() => {
    if (open) {
      setUserId(member?.userId?.toString() || "");
      setRole(member?.role || "MEMBER");
      setMembershipStatus(member?.membershipStatus || "ACTIVE");
      setValidationError("");
    }
  }, [open, member]);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setValidationError("");

    if (!isEdit && (!userId || Number(userId) <= 0)) {
      setValidationError("Enter a valid platform user ID.");
      return;
    }

    const payload = isEdit
      ? { role, membershipStatus }
      : { userId: Number(userId), role };

    try {
      await onSubmit(payload);
      onClose();
    } catch (error) {
      setValidationError(
        error.response?.data?.detail ||
          error.response?.data?.message ||
          "Unable to save organization member.",
      );
    }
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <form onSubmit={handleSubmit}>
        <DialogTitle>
          {isEdit ? "Update Organization Member" : "Add Organization Member"}
        </DialogTitle>

        <DialogContent>
          <Stack spacing={2} sx={{ pt: 1 }}>
            {!isEdit && (
              <TextField
                label="Platform User ID"
                type="number"
                value={userId}
                onChange={(event) => setUserId(event.target.value)}
                required
                fullWidth
                inputProps={{ min: 1 }}
                helperText="Enter the ID of an existing platform user."
              />
            )}

            <FormControl fullWidth>
              <InputLabel id="organization-member-role-label">Role</InputLabel>
              <Select
                labelId="organization-member-role-label"
                value={role}
                label="Role"
                onChange={(event) => setRole(event.target.value)}
              >
                {ROLES.map((value) => (
                  <MenuItem key={value} value={value}>
                    {value}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            {isEdit && (
              <FormControl fullWidth>
                <InputLabel id="organization-member-status-label">
                  Membership Status
                </InputLabel>
                <Select
                  labelId="organization-member-status-label"
                  value={membershipStatus}
                  label="Membership Status"
                  onChange={(event) => setMembershipStatus(event.target.value)}
                >
                  {STATUSES.map((value) => (
                    <MenuItem key={value} value={value}>
                      {value}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            )}

            {validationError && (
              <div role="alert" style={{ color: "red" }}>
                {validationError}
              </div>
            )}
          </Stack>
        </DialogContent>

        <DialogActions>
          <Button onClick={onClose} disabled={submitting}>
            Cancel
          </Button>
          <Button type="submit" variant="contained" disabled={submitting}>
            {submitting ? "Saving..." : isEdit ? "Save Changes" : "Add Member"}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

export default OrganizationMemberForm;
