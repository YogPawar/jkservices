import Chip from "@mui/material/Chip";

export default function UserStatusChip({ active }) {
  return (
    <Chip
      label={active ? "Active" : "Inactive"}
      color={active ? "success" : "default"}
      size="small"
      variant="outlined"
    />
  );
}
