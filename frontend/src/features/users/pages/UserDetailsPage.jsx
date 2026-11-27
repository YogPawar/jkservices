import { useEffect, useState } from "react";
import {
  Alert,
  Box,
  Button,
  Divider,
  Paper,
  Stack,
  Typography,
} from "@mui/material";

import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";

import { useLocation, useNavigate, useParams } from "react-router-dom";
import { userApi } from "../api/userApi";
import UserStatusChip from "../components/UserStatusChip";

function DetailItem({ label, value }) {
  return (
    <Box>
      <Typography variant="body2" color="text.secondary" mb={0.5}>
        {label}
      </Typography>

      <Typography variant="body1" fontWeight={500}>
        {value ?? "—"}
      </Typography>
    </Box>
  );
}

export default function UserDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();

  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
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

  if (loading) {
    return <Typography>Loading user...</Typography>;
  }

  if (error || !user) {
    return <Alert severity="error">{error || "User not found."}</Alert>;
  }

  return (
    <Box maxWidth="900px">
      <Stack
        direction={{ xs: "column", sm: "row" }}
        justifyContent="space-between"
        alignItems={{ xs: "stretch", sm: "center" }}
        spacing={2}
        mb={3}
      >
        <Box>
          <Typography variant="h4" fontWeight={600}>
            User details
          </Typography>

          <Typography color="text.secondary" mt={0.5}>
            View platform user account information.
          </Typography>
        </Box>

        <Stack direction="row" spacing={1}>
          <Button
            startIcon={<ArrowBackIcon />}
            color="inherit"
            onClick={() => navigate("/users")}
          >
            Back
          </Button>

          <Button
            variant="contained"
            startIcon={<EditOutlinedIcon />}
            onClick={() => navigate(`/users/${id}/edit`)}
          >
            Edit user
          </Button>
        </Stack>
      </Stack>

      {location.state?.message && (
        <Alert severity="success" sx={{ mb: 2 }}>
          {location.state.message}
        </Alert>
      )}

      <Paper variant="outlined" sx={{ p: { xs: 2, sm: 3 } }}>
        <Stack
          direction={{ xs: "column", sm: "row" }}
          justifyContent="space-between"
          spacing={2}
        >
          <Box>
            <Typography variant="h5" fontWeight={600}>
              {user.fullName}
            </Typography>

            <Typography color="text.secondary" mt={0.5}>
              User ID: {user.id}
            </Typography>
          </Box>

          <UserStatusChip active={user.active} />
        </Stack>

        <Divider sx={{ my: 3 }} />

        <Box
          display="grid"
          gridTemplateColumns={{
            xs: "1fr",
            sm: "1fr 1fr",
          }}
          gap={3}
        >
          <DetailItem label="Full name" value={user.fullName} />
          <DetailItem label="Email address" value={user.email} />
          <DetailItem
            label="Created at"
            value={
              user.createdAt ? new Date(user.createdAt).toLocaleString() : "—"
            }
          />
          <DetailItem
            label="Last updated"
            value={
              user.updatedAt ? new Date(user.updatedAt).toLocaleString() : "—"
            }
          />
        </Box>
      </Paper>
    </Box>
  );
}
