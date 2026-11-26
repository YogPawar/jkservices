import { Chip } from "@mui/material";

const BusinessStatusChip = ({ active }) => (
  <Chip
    label={active ? "Active" : "Inactive"}
    color={active ? "success" : "default"}
    size="small"
    variant="outlined"
  />
);

export default BusinessStatusChip;
