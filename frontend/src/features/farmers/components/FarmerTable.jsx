import {
  Alert,
  Box,
  Button,
  Chip,
  CircularProgress,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";

export default function FarmerTable({
  farmers = [],
  isLoading = false,
  isMutating = false,
  onView,
  onEdit,
  onDeactivate,
}) {
  if (isLoading) {
    return (
      <Box display="flex" justifyContent="center" py={6}>
        <CircularProgress />
      </Box>
    );
  }

  if (farmers.length === 0) {
    return (
      <Alert severity="info">
        No farmers found. Use Add Farmer to register the first farmer.
      </Alert>
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
                <Chip
                  label={farmer.active ? "Active" : "Inactive"}
                  color={farmer.active ? "success" : "default"}
                  size="small"
                />
              </TableCell>

              <TableCell align="right">
                <Box
                  display="flex"
                  justifyContent="flex-end"
                  gap={1}
                  flexWrap="wrap"
                >
                  <Button size="small" onClick={() => onView?.(farmer)}>
                    View
                  </Button>

                  <Button
                    size="small"
                    onClick={() => onEdit?.(farmer)}
                    disabled={!farmer.active || isMutating}
                  >
                    Edit
                  </Button>

                  <Button
                    size="small"
                    color="error"
                    onClick={() => onDeactivate?.(farmer)}
                    disabled={!farmer.active || isMutating}
                  >
                    Deactivate
                  </Button>
                </Box>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
