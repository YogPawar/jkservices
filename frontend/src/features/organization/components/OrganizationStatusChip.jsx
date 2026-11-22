import { Chip } from "@mui/material";

export default function OrganizationStatusChip({ active }) {
  return (
    <Chip
      size="small"
      label={active ? "Active" : "Inactive"}
      color={active ? "success" : "default"}
      variant="outlined"
    />
  );
}
