import { Chip } from "@mui/material";

const OrganizationMemberStatusChip = ({ status }) => {
  const color = status === "ACTIVE" ? "success" : "warning";

  return (
    <Chip
      label={status || "UNKNOWN"}
      color={color}
      size="small"
      variant="outlined"
    />
  );
};

export default OrganizationMemberStatusChip;
