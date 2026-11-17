import { Grid, MenuItem, TextField } from "@mui/material";

export default function FarmerSearch({
  search,
  onSearchChange,
  status,
  onStatusChange,
}) {
  return (
    <Grid container spacing={2}>
      <Grid size={{ xs: 12, md: 8 }}>
        <TextField
          fullWidth
          label="Search farmers"
          placeholder="Search by name, farmer code, phone or village"
          value={search}
          onChange={(event) => onSearchChange(event.target.value)}
        />
      </Grid>

      <Grid size={{ xs: 12, md: 4 }}>
        <TextField
          select
          fullWidth
          label="Filter by status"
          value={status}
          onChange={(event) => onStatusChange(event.target.value)}
        >
          <MenuItem value="ALL">All farmers</MenuItem>
          <MenuItem value="ACTIVE">Active</MenuItem>
          <MenuItem value="INACTIVE">Inactive</MenuItem>
        </TextField>
      </Grid>
    </Grid>
  );
}
