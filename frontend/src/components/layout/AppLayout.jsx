import { useState } from "react";
import { Box, Drawer, useMediaQuery } from "@mui/material";
import { useTheme } from "@mui/material/styles";
import { Outlet, useLocation } from "react-router-dom";

import Sidebar from "./Sidebar.jsx";
import Topbar from "./Topbar.jsx";

const pageTitles = [
  { path: "/organizations/new", title: "Create Organization" },
  { path: "/organizations", title: "Organizations" },
  { path: "/farmers/new", title: "Add Farmer" },
  { path: "/farmers", title: "Farmers" },
  { path: "/daily-work", title: "Daily Work" },
  { path: "/billing", title: "Billing" },
  { path: "/payments", title: "Payments" },
  { path: "/ledger", title: "Farmer Ledger" },
  { path: "/reports", title: "Monthly Reports" },
  { path: "/users", title: "User Management" },
];

function getPageTitle(pathname) {
  if (pathname === "/") return "Dashboard";

  if (/^\/organizations\/[^/]+\/edit$/.test(pathname)) {
    return "Edit Organization";
  }

  if (/^\/organizations\/[^/]+$/.test(pathname)) {
    return "Organization Details";
  }

  if (/^\/farmers\/[^/]+\/edit$/.test(pathname)) {
    return "Edit Farmer";
  }

  if (/^\/farmers\/[^/]+$/.test(pathname)) {
    return "Farmer Details";
  }

  return (
    pageTitles.find((item) => item.path === pathname)?.title ?? "JKServices"
  );
}

export default function AppLayout() {
  const [mobileOpen, setMobileOpen] = useState(false);

  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("md"));

  const location = useLocation();
  const title = getPageTitle(location.pathname);

  const closeMobileMenu = () => setMobileOpen(false);

  return (
    <Box sx={{ display: "flex", minHeight: "100vh" }}>
      {!isMobile && (
        <Drawer
          variant="permanent"
          sx={{
            width: 248,
            flexShrink: 0,
            "& .MuiDrawer-paper": {
              width: 248,
              boxSizing: "border-box",
              borderRight: "1px solid #e6ebe4",
            },
          }}
        >
          <Sidebar />
        </Drawer>
      )}

      {isMobile && (
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={closeMobileMenu}
          ModalProps={{ keepMounted: true }}
          sx={{
            "& .MuiDrawer-paper": {
              width: 248,
              boxSizing: "border-box",
            },
          }}
        >
          <Sidebar onNavigate={closeMobileMenu} />
        </Drawer>
      )}

      <Box component="main" sx={{ flexGrow: 1, minWidth: 0 }}>
        <Topbar
          title={title}
          showMenu={isMobile}
          onMenuClick={() => setMobileOpen(true)}
        />

        <Box sx={{ p: { xs: 2, sm: 3, md: 4 } }}>
          <Outlet />
        </Box>
      </Box>
    </Box>
  );
}
