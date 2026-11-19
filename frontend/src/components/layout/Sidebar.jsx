import {
  Box,
  Divider,
  List,
  ListItemButton,
  ListItemText,
  Typography,
} from "@mui/material";

import { NavLink, useLocation } from "react-router-dom";

const menuItems = [
  { label: "Dashboard", path: "/" },
  { label: "Farmers", path: "/farmers" },
  { label: "Daily Work", path: "/daily-work" },
  { label: "Billing", path: "/billing" },
  { label: "Payments", path: "/payments" },
  { label: "Farmer Ledger", path: "/ledger" },
  { label: "Monthly Reports", path: "/reports" },
  { label: "User Management", path: "/users" },
];

export default function Sidebar({ onNavigate }) {
  const location = useLocation();

  return (
    <Box sx={{ width: 248, p: 2 }}>
      <Typography
        variant="h6"
        fontWeight={800}
        color="primary.main"
        sx={{ px: 1, py: 2 }}
      >
        🌾 JKServices
      </Typography>

      <List>
        {menuItems.map((item) => {
          const selected = location.pathname === item.path;

          return (
            <ListItemButton
              key={item.path}
              component={NavLink}
              to={item.path}
              selected={selected}
              onClick={onNavigate}
              sx={{
                borderRadius: 2,
                mb: 0.5,
                color: selected ? "primary.main" : "text.primary",
                "&.Mui-selected": {
                  backgroundColor: "primary.light",
                  fontWeight: 700,
                },
                "&.Mui-selected:hover": {
                  backgroundColor: "primary.light",
                },
              }}
            >
              <ListItemText primary={item.label} />
            </ListItemButton>
          );
        })}
      </List>

      <Divider sx={{ my: 2 }} />

      <Typography variant="caption" color="text.secondary">
        Business workspace
      </Typography>
    </Box>
  );
}
