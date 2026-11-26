import {
  IconButton,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Tooltip,
} from "@mui/material";
import VisibilityOutlinedIcon from "@mui/icons-material/VisibilityOutlined";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import BlockOutlinedIcon from "@mui/icons-material/BlockOutlined";
import BusinessStatusChip from "./BusinessStatusChip";

const BusinessTable = ({ businesses, onView, onEdit, onDeactivate }) => {
  return (
    <TableContainer component={Paper} variant="outlined">
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Business Name</TableCell>
            <TableCell>Code</TableCell>
            <TableCell>Type</TableCell>
            <TableCell>Contact</TableCell>
            <TableCell>Status</TableCell>
            <TableCell align="right">Actions</TableCell>
          </TableRow>
        </TableHead>

        <TableBody>
          {businesses.map((business) => (
            <TableRow key={business.id} hover>
              <TableCell>{business.businessName}</TableCell>
              <TableCell>{business.businessCode}</TableCell>
              <TableCell>
                {business.businessType === "FARMING_SERVICES"
                  ? "Farming Services"
                  : "Event Management"}
              </TableCell>
              <TableCell>{business.contactNumber || "—"}</TableCell>
              <TableCell>
                <BusinessStatusChip active={business.active} />
              </TableCell>
              <TableCell align="right">
                <Tooltip title="View">
                  <IconButton size="small" onClick={() => onView(business.id)}>
                    <VisibilityOutlinedIcon fontSize="small" />
                  </IconButton>
                </Tooltip>

                {business.active && (
                  <>
                    <Tooltip title="Edit">
                      <IconButton
                        size="small"
                        onClick={() => onEdit(business.id)}
                      >
                        <EditOutlinedIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>

                    <Tooltip title="Deactivate">
                      <IconButton
                        size="small"
                        color="error"
                        onClick={() => onDeactivate(business)}
                      >
                        <BlockOutlinedIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                  </>
                )}
              </TableCell>
            </TableRow>
          ))}

          {businesses.length === 0 && (
            <TableRow>
              <TableCell colSpan={6} align="center">
                No businesses found.
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default BusinessTable;
