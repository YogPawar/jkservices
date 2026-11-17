import { createBrowserRouter } from "react-router-dom";
import { Typography } from "@mui/material";

import AppLayout from "../components/layout/AppLayout.jsx";
import DashboardPage from "../features/dashboard/pages/DashboardPage.jsx";

import FarmersPage from "../features/farmers/pages/FarmersPage.jsx";
import AddFarmerPage from "../features/farmers/pages/AddFarmerPage.jsx";
import EditFarmerPage from "../features/farmers/pages/EditFarmerPage.jsx";
import FarmerDetailsPage from "../features/farmers/pages/FarmerDetailsPage.jsx";

function ModulePlaceholder({ title }) {
  return <Typography variant="h5">{title} — implementation pending</Typography>;
}
import { useLocation } from "react-router-dom";
function RouteDiagnostic() {
  const location = useLocation();

  return (
    <Typography color="error" sx={{ p: 2 }}>
      Current route: {location.pathname}
    </Typography>
  );
}

const router = createBrowserRouter([
  {
    path: "/",
    element: <AppLayout />,
    children: [
      { index: true, element: <DashboardPage /> },

      // Farmer module
      { path: "farmers", element: <FarmersPage /> },
      { path: "farmers/new", element: <AddFarmerPage /> },
      { path: "farmers/:farmerId", element: <FarmerDetailsPage /> },
      { path: "farmers/:farmerId/edit", element: <EditFarmerPage /> },

      // Other modules
      {
        path: "daily-work",
        element: <ModulePlaceholder title="Daily Work" />,
      },
      {
        path: "billing",
        element: <ModulePlaceholder title="Billing" />,
      },
      {
        path: "payments",
        element: <ModulePlaceholder title="Payments" />,
      },
      {
        path: "ledger",
        element: <ModulePlaceholder title="Farmer Ledger" />,
      },
      {
        path: "reports",
        element: <ModulePlaceholder title="Monthly Reports" />,
      },
      {
        path: "users",
        element: <ModulePlaceholder title="User Management" />,
      },
      {
        path: "farmers",
        element: (
          <>
            <RouteDiagnostic />
            <FarmersPage />
          </>
        ),
      },
    ],
  },
]);

export default router;
