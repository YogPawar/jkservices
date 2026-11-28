import {
  Box,
  Button,
  CircularProgress,
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
} from "@mui/material";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import DeleteOutlineIcon from "@mui/icons-material/DeleteOutline";
import OrganizationMemberStatusChip from "./OrganizationMemberStatusChip";

const OrganizationMemberTable = ({ members, loading, onEdit, onRemove }) => {
  if (loading) {
    return (
      <Box display="flex" justifyContent="center" p={4}>
        <CircularProgress />
      </Box>
    );
  }

  if (!members.length) {
    return (
      <Paper sx={{ p: 4, textAlign: "center" }}>
        <Typography color="text.secondary">
          No members have been added to this organization yet.
        </Typography>
      </Paper>
    );
  }

  return (
    <TableContainer component={Paper}>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Member ID</TableCell>
            <TableCell>User ID</TableCell>
            <TableCell>Role</TableCell>
            <TableCell>Status</TableCell>
            <TableCell>Joined At</TableCell>
            <TableCell align="right">Actions</TableCell>
          </TableRow>
        </TableHead>

        <TableBody>
          {members.map((member) => (
            <TableRow key={member.id} hover>
              <TableCell>{member.id}</TableCell>
              <TableCell>{member.userId}</TableCell>
              <TableCell>{member.role}</TableCell>
              <TableCell>
                <OrganizationMemberStatusChip
                  status={member.membershipStatus}
                />
              </TableCell>
              <TableCell>
                {member.joinedAt
                  ? new Date(member.joinedAt).toLocaleDateString()
                  : "—"}
              </TableCell>
              <TableCell align="right">
                <Tooltip title="Edit membership">
                  <IconButton
                    color="primary"
                    onClick={() => onEdit(member)}
                    aria-label={`Edit member ${member.id}`}
                  >
                    <EditOutlinedIcon />
                  </IconButton>
                </Tooltip>

                <Tooltip title="Remove membership">
                  <IconButton
                    color="error"
                    onClick={() => onRemove(member)}
                    aria-label={`Remove member ${member.id}`}
                  >
                    <DeleteOutlineIcon />
                  </IconButton>
                </Tooltip>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default OrganizationMemberTable;
