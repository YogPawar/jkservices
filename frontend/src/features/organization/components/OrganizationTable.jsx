import {
  IconButton,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Tooltip,
  Paper,
  Typography,
} from "@mui/material";

import VisibilityOutlinedIcon from "@mui/icons-material/VisibilityOutlined";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import BlockOutlinedIcon from "@mui/icons-material/BlockOutlined";

import { useNavigate } from "react-router-dom";
import OrganizationStatusChip from "./OrganizationStatusChip";

export default function OrganizationTable({ organizations, onDeactivate }) {
  const navigate = useNavigate();

  if (!organizations.length) {
    return (
      <Paper variant="outlined" sx={{ p: 5, textAlign: "center" }}>
        <Typography color="text.secondary">
          No organizations found. Create your first organization.
        </Typography>
      </Paper>
    );
  }

  return (
    <TableContainer component={Paper} variant="outlined">
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Organization</TableCell>
            <TableCell>Invite code</TableCell>
            <TableCell>Status</TableCell>
            <TableCell>Created</TableCell>
            <TableCell align="right">Actions</TableCell>
          </TableRow>
        </TableHead>

        <TableBody>
          {organizations.map((item) => (
            <TableRow key={item.id} hover>
              <TableCell>
                <Typography fontWeight={600}>
                  {item.organizationName}
                </Typography>
              </TableCell>

              <TableCell>{item.inviteCode || "—"}</TableCell>

              <TableCell>
                <OrganizationStatusChip active={item.active} />
              </TableCell>

              <TableCell>
                {item.createdAt
                  ? new Date(item.createdAt).toLocaleDateString()
                  : "—"}
              </TableCell>

              <TableCell align="right">
                <Stack direction="row" spacing={0.5} justifyContent="flex-end">
                  <Tooltip title="View">
                    <IconButton
                      aria-label="View organization"
                      onClick={() => navigate(`/organizations/${item.id}`)}
                    >
                      <VisibilityOutlinedIcon />
                    </IconButton>
                  </Tooltip>

                  <Tooltip title="Edit">
                    <IconButton
                      aria-label="Edit organization"
                      onClick={() => navigate(`/organizations/${item.id}/edit`)}
                    >
                      <EditOutlinedIcon />
                    </IconButton>
                  </Tooltip>

                  <Tooltip title="Deactivate">
                    <span>
                      <IconButton
                        aria-label="Deactivate organization"
                        color="error"
                        onClick={() => onDeactivate(item)}
                        disabled={!item.active}
                      >
                        <BlockOutlinedIcon />
                      </IconButton>
                    </span>
                  </Tooltip>
                </Stack>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
