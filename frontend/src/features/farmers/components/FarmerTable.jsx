import {
  Box,
  IconButton,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Tooltip,
  Typography,
  CircularProgress,
} from "@mui/material";

import VisibilityOutlinedIcon from "@mui/icons-material/VisibilityOutlined";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import BlockOutlinedIcon from "@mui/icons-material/BlockOutlined";

import { useNavigate } from "react-router-dom";

export default function FarmerTable({
  farmers = [],
  isLoading = false,
  isMutating = false,
  onView,
  onEdit,
  onDeactivate,
}) {
  const navigate = useNavigate();

  if (isLoading) {
    return (
      <Box display="flex" justifyContent="center" py={6}>
        <CircularProgress />
      </Box>
    );
  }

  if (farmers.length === 0) {
    return (
      <Paper variant="outlined" sx={{ p: 5, textAlign: "center" }}>
        <Typography color="text.secondary">
          No farmers found. Use Add Farmer to register the first farmer.
        </Typography>
      </Paper>
    );
  }

  return (
    <TableContainer component={Paper} variant="outlined">
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Farmer Code</TableCell>
            <TableCell>Name</TableCell>
            <TableCell>Phone</TableCell>
            <TableCell>Village</TableCell>
            <TableCell>Status</TableCell>
            <TableCell align="right">Actions</TableCell>
          </TableRow>
        </TableHead>

        <TableBody>
          {farmers.map((farmer) => (
            <TableRow key={farmer.id} hover>
              <TableCell>{farmer.farmerCode}</TableCell>
              <TableCell>{farmer.name}</TableCell>
              <TableCell>{farmer.phone || "—"}</TableCell>
              <TableCell>{farmer.village || "—"}</TableCell>

              <TableCell>
                <Typography
                  component="span"
                  sx={{
                    display: "inline-block",
                    px: 1,
                    py: 0.25,
                    borderRadius: 1,
                    fontSize: "0.75rem",
                    bgcolor: farmer.active ? "success.light" : "action.hover",
                    color: farmer.active ? "success.dark" : "text.secondary",
                  }}
                >
                  {farmer.active ? "Active" : "Inactive"}
                </Typography>
              </TableCell>

              <TableCell align="right">
                <Box display="flex" justifyContent="flex-end" gap={0.5}>
                  <Tooltip title="View">
                    <IconButton
                      aria-label="View farmer"
                      onClick={() =>
                        onView
                          ? onView(farmer)
                          : navigate(`/farmers/${farmer.id}`)
                      }
                    >
                      <VisibilityOutlinedIcon />
                    </IconButton>
                  </Tooltip>

                  <Tooltip title="Edit">
                    <IconButton
                      aria-label="Edit farmer"
                      onClick={() =>
                        onEdit
                          ? onEdit(farmer)
                          : navigate(`/farmers/${farmer.id}/edit`)
                      }
                      disabled={isMutating}
                    >
                      <EditOutlinedIcon />
                    </IconButton>
                  </Tooltip>

                  <Tooltip title="Deactivate">
                    <span>
                      <IconButton
                        aria-label="Deactivate farmer"
                        color="error"
                        onClick={() => onDeactivate?.(farmer)}
                        disabled={!farmer.active || isMutating}
                      >
                        <BlockOutlinedIcon />
                      </IconButton>
                    </span>
                  </Tooltip>
                </Box>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
