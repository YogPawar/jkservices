import {
  Box,
  Divider,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Typography,
} from "@mui/material";

import DashboardRoundedIcon from "@mui/icons-material/DashboardRounded";
import BusinessRoundedIcon from "@mui/icons-material/BusinessRounded";
import StorefrontRoundedIcon from "@mui/icons-material/StorefrontRounded";
import AgricultureRoundedIcon from "@mui/icons-material/AgricultureRounded";
import WorkHistoryRoundedIcon from "@mui/icons-material/WorkHistoryRounded";
import ReceiptLongRoundedIcon from "@mui/icons-material/ReceiptLongRounded";
import PaymentsRoundedIcon from "@mui/icons-material/PaymentsRounded";
import AccountBalanceWalletRoundedIcon from "@mui/icons-material/AccountBalanceWalletRounded";
import AssessmentRoundedIcon from "@mui/icons-material/AssessmentRounded";
import PeopleAltOutlinedIcon from "@mui/icons-material/PeopleAltOutlined";

import { NavLink, useLocation } from "react-router-dom";

const navigationGroups = [
  {
    title: "WORKSPACE",
    items: [
      {
        label: "Dashboard",
        path: "/",
        icon: DashboardRoundedIcon,
      },
      {
        label: "Organizations",
        path: "/organizations",
        icon: BusinessRoundedIcon,
      },
      {
        label: "User Management",
        path: "/users",
        icon: PeopleAltOutlinedIcon,
      },
    ],
  },
  {
    title: "OPERATIONS",
    items: [
      {
        label: "Farmers",
        path: "/farmers",
        icon: AgricultureRoundedIcon,
      },
      {
        label: "Daily Work",
        path: "/daily-work",
        icon: WorkHistoryRoundedIcon,
      },
      {
        label: "Billing",
        path: "/billing",
        icon: ReceiptLongRoundedIcon,
      },
      {
        label: "Payments",
        path: "/payments",
        icon: PaymentsRoundedIcon,
      },
      {
        label: "Farmer Ledger",
        path: "/ledger",
        icon: AccountBalanceWalletRoundedIcon,
      },
    ],
  },
  {
    title: "INSIGHTS & ADMIN",
    items: [
      {
        label: "Monthly Reports",
        path: "/reports",
        icon: AssessmentRoundedIcon,
      },
    ],
  },
];

export default function Sidebar({ onNavigate }) {
  const location = useLocation();

  // Extract the active organization ID from the current URL.
  const organizationMatch = location.pathname.match(
    /^\/organizations\/([^/]+)(?:\/|$)/,
  );

  const activeOrganizationId =
    organizationMatch && organizationMatch[1] !== "new"
      ? organizationMatch[1]
      : null;

  // Add organization-scoped Business navigation dynamically.
  const groups = navigationGroups.map((group) => {
    if (group.title !== "OPERATIONS" || !activeOrganizationId) {
      return group;
    }

    return {
      ...group,
      items: [
        {
          label: "Businesses",
          path: `/organizations/${activeOrganizationId}/businesses`,
          icon: StorefrontRoundedIcon,
        },
        ...group.items,
      ],
    };
  });

  // Determine the active navigation item.
  const isSelected = (path) => {
    if (path === "/") {
      return location.pathname === "/";
    }

    return (
      location.pathname === path || location.pathname.startsWith(`${path}/`)
    );
  };

  return (
    <Box
      sx={{
        width: 260,
        height: "100%",
        display: "flex",
        flexDirection: "column",
        bgcolor: "background.paper",
        borderRight: "1px solid",
        borderColor: "divider",
      }}
    >
      {/* Brand */}
      <Box
        sx={{
          px: 2.5,
          py: 3,
          display: "flex",
          alignItems: "center",
          gap: 1.5,
        }}
      >
        <Box
          sx={{
            width: 42,
            height: 42,
            borderRadius: 2.5,
            bgcolor: "primary.main",
            color: "primary.contrastText",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            fontSize: 23,
          }}
        >
          🌾
        </Box>

        <Box>
          <Typography
            variant="h6"
            fontWeight={800}
            color="text.primary"
            lineHeight={1.2}
          >
            JKServices
          </Typography>

          <Typography variant="caption" color="text.secondary">
            Business management
          </Typography>
        </Box>
      </Box>

      <Divider />

      {/* Navigation */}
      <Box
        sx={{
          flex: 1,
          overflowY: "auto",
          px: 1.5,
          py: 2,
        }}
      >
        {groups.map((group, groupIndex) => (
          <Box key={group.title} sx={{ mb: 2.5 }}>
            <Typography
              variant="overline"
              color="text.secondary"
              fontWeight={700}
              sx={{
                px: 1.5,
                mb: 1,
                display: "block",
                letterSpacing: 1,
                fontSize: "0.68rem",
              }}
            >
              {group.title}
            </Typography>

            <List disablePadding>
              {group.items.map((item) => {
                const selected = isSelected(item.path);
                const Icon = item.icon;

                return (
                  <ListItemButton
                    key={item.path}
                    component={NavLink}
                    to={item.path}
                    selected={selected}
                    onClick={onNavigate}
                    sx={{
                      minHeight: 44,
                      px: 1.5,
                      mb: 0.5,
                      borderRadius: 2,
                      color: selected ? "primary.main" : "text.secondary",
                      transition: "all 0.2s ease",

                      "& .MuiListItemIcon-root": {
                        minWidth: 38,
                        color: "inherit",
                      },

                      "& .MuiListItemText-primary": {
                        fontSize: "0.875rem",
                        fontWeight: selected ? 700 : 500,
                      },

                      "&.Mui-selected": {
                        bgcolor: "action.selected",
                        color: "primary.main",
                      },

                      "&.Mui-selected:hover": {
                        bgcolor: "action.selected",
                      },

                      "&:hover": {
                        bgcolor: "action.hover",
                        color: "primary.main",
                      },
                    }}
                  >
                    <ListItemIcon>
                      <Icon fontSize="small" />
                    </ListItemIcon>

                    <ListItemText primary={item.label} />
                  </ListItemButton>
                );
              })}
            </List>

            {groupIndex !== groups.length - 1 && <Divider sx={{ mt: 2 }} />}
          </Box>
        ))}
      </Box>

      {/* Footer */}
      <Divider />

      <Box sx={{ px: 2.5, py: 2 }}>
        <Typography variant="caption" color="text.secondary" fontWeight={600}>
          JKServices
        </Typography>

        <Typography variant="caption" display="block" color="text.disabled">
          Business workspace
        </Typography>
      </Box>
    </Box>
  );
}
