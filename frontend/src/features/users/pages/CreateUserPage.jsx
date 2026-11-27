import { useState } from "react";
import { Alert, Box, Paper, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";

import UserForm from "../components/UserForm";
import { userApi } from "../api/userApi";

export default function CreateUserPage() {
  const navigate = useNavigate();

  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (payload) => {
    try {
      setSubmitting(true);
      setError("");

      const createdUser = await userApi.createUser(payload);

      navigate(`/users/${createdUser.id}`, {
        replace: true,
        state: { message: "User created successfully." },
      });
    } catch (err) {
      setError(
        err.response?.data?.detail ||
          err.response?.data?.message ||
          "Unable to create user.",
      );
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Box maxWidth="720px">
      <Typography variant="h4" fontWeight={600} mb={1}>
        Create user
      </Typography>

      <Typography color="text.secondary" mb={3}>
        Create a new platform user account.
      </Typography>

      <Paper variant="outlined" sx={{ p: { xs: 2, sm: 3 } }}>
        <UserForm
          mode="create"
          onSubmit={handleSubmit}
          submitting={submitting}
          error={error}
        />
      </Paper>
    </Box>
  );
}
