import { createBrowserRouter } from "react-router-dom";
import { Typography } from "@mui/material";

import AppLayout from "../components/layout/AppLayout.jsx";
import DashboardPage from "../features/dashboard/pages/DashboardPage.jsx";

// Organization module
import OrganizationPage from "../features/organization/pages/OrganizationPage.jsx";
import AddOrganizationPage from "../features/organization/pages/AddOrganizationPage.jsx";
import EditOrganizationPage from "../features/organization/pages/EditOrganizationPage.jsx";
import OrganizationDetailsPage from "../features/organization/pages/OrganizationDetailsPage.jsx";

// Business module
import BusinessesPage from "../features/businesses/pages/BusinessesPage.jsx";
import AddBusinessPage from "../features/businesses/pages/AddBusinessPage.jsx";
import EditBusinessPage from "../features/businesses/pages/EditBusinessPage.jsx";
import BusinessDetailsPage from "../features/businesses/pages/BusinessDetailsPage.jsx";

// Farmer module
import FarmersPage from "../features/farmers/pages/FarmersPage.jsx";
import AddFarmerPage from "../features/farmers/pages/AddFarmerPage.jsx";
import EditFarmerPage from "../features/farmers/pages/EditFarmerPage.jsx";
import FarmerDetailsPage from "../features/farmers/pages/FarmerDetailsPage.jsx";

function ModulePlaceholder({ title }) {
  return <Typography variant="h5">{title} — implementation pending</Typography>;
}

const router = createBrowserRouter([
  {
    path: "/",
    element: <AppLayout />,
    children: [
      { index: true, element: <DashboardPage /> },

      // Organization module
      { path: "organizations", element: <OrganizationPage /> },
      { path: "organizations/new", element: <AddOrganizationPage /> },
      {
        path: "organizations/:organizationId",
        element: <OrganizationDetailsPage />,
      },
      {
        path: "organizations/:organizationId/edit",
        element: <EditOrganizationPage />,
      },

      // Business module (organization-scoped)
      {
        path: "organizations/:organizationId/businesses",
        element: <BusinessesPage />,
      },
      {
        path: "organizations/:organizationId/businesses/new",
        element: <AddBusinessPage />,
      },
      {
        path: "organizations/:organizationId/businesses/:businessId",
        element: <BusinessDetailsPage />,
      },
      {
        path: "organizations/:organizationId/businesses/:businessId/edit",
        element: <EditBusinessPage />,
      },

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
      { path: "billing", element: <ModulePlaceholder title="Billing" /> },
      { path: "payments", element: <ModulePlaceholder title="Payments" /> },
      { path: "ledger", element: <ModulePlaceholder title="Farmer Ledger" /> },
      {
        path: "reports",
        element: <ModulePlaceholder title="Monthly Reports" />,
      },
      { path: "users", element: <ModulePlaceholder title="User Management" /> },
    ],
  },
]);

export default router;
