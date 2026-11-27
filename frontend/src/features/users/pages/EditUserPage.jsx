import { useEffect, useState } from "react";
import { Alert, Box, Paper, Typography } from "@mui/material";

import { useNavigate, useParams } from "react-router-dom";
import { userApi } from "../api/userApi";
import UserForm from "../components/UserForm";

export default function EditUserPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;

    const loadUser = async () => {
      try {
        setLoading(true);
        setError("");

        const result = await userApi.getUserById(id);

        if (!cancelled) {
          setUser(result);
        }
      } catch (err) {
        if (!cancelled) {
          setError(
            err.response?.data?.detail ||
              err.response?.data?.message ||
              "Unable to load user.",
          );
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    };

    loadUser();

    return () => {
      cancelled = true;
    };
  }, [id]);

  const handleSubmit = async (payload) => {
    try {
      setSubmitting(true);
      setError("");

      await userApi.updateUser(id, payload);

      navigate(`/users/${id}`, {
        replace: true,
        state: { message: "User updated successfully." },
      });
    } catch (err) {
      setError(
        err.response?.data?.detail ||
          err.response?.data?.message ||
          "Unable to update user.",
      );
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <Typography>Loading user...</Typography>;
  }

  if (!user) {
    return <Alert severity="error">{error || "User not found."}</Alert>;
  }

  return (
    <Box maxWidth="720px">
      <Typography variant="h4" fontWeight={600} mb={1}>
        Edit user
      </Typography>

      <Typography color="text.secondary" mb={3}>
        Update user profile information.
      </Typography>

      <Paper variant="outlined" sx={{ p: { xs: 2, sm: 3 } }}>
        <UserForm
          mode="edit"
          initialValues={user}
          onSubmit={handleSubmit}
          submitting={submitting}
          error={error}
        />
      </Paper>
    </Box>
  );
}
