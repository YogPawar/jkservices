import {
  Avatar,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Grid,
  Stack,
  Typography,
} from "@mui/material";

import {
  Agriculture,
  AddTask,
  Assessment,
  Payments,
  ReceiptLong,
  TrendingUp,
  WarningAmber,
} from "@mui/icons-material";

import { useNavigate } from "react-router-dom";

const metrics = [
  {
    label: "Unbilled work",
    value: "₹12,500",
    hint: "8 work entries pending",
    icon: Agriculture,
    color: "#286044",
    background: "#E8F3EC",
  },
  {
    label: "Outstanding bills",
    value: "₹35,000",
    hint: "15 invoices not fully paid",
    icon: ReceiptLong,
    color: "#315DB5",
    background: "#E9F0FF",
  },
  {
    label: "Overdue bills",
    value: "₹18,000",
    hint: "7 invoices past due date",
    icon: WarningAmber,
    color: "#C62828",
    background: "#FCECEC",
  },
  {
    label: "Collected this month",
    value: "₹42,000",
    hint: "November collections",
    icon: TrendingUp,
    color: "#A36A0B",
    background: "#FFF4DF",
  },
];

function MetricCard({ metric }) {
  const Icon = metric.icon;

  return (
    <Card
      sx={{
        height: "100%",
        backgroundColor: metric.background,
        border: "none",
        borderRadius: 3,
        transition: "transform 0.2s, box-shadow 0.2s",
        "&:hover": {
          transform: "translateY(-4px)",
          boxShadow: "0 8px 24px rgba(0,0,0,0.08)",
        },
      }}
    >
      <CardContent sx={{ p: 2.5 }}>
        <Stack
          direction="row"
          justifyContent="space-between"
          alignItems="center"
        >
          <Typography color="text.secondary" fontWeight={600}>
            {metric.label}
          </Typography>

          <Avatar
            sx={{
              backgroundColor: "#ffffff",
              color: metric.color,
              width: 42,
              height: 42,
            }}
          >
            <Icon />
          </Avatar>
        </Stack>

        <Typography
          variant="h4"
          fontWeight={800}
          sx={{ color: metric.color, mt: 2 }}
        >
          {metric.value}
        </Typography>

        <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
          {metric.hint}
        </Typography>
      </CardContent>
    </Card>
  );
}

export default function DashboardPage() {
  const navigate = useNavigate();

  const overdueItems = [
    ["Bharat Shinde", "₹2,000", "35 days overdue"],
    ["Mahesh Shinde", "₹1,500", "18 days overdue"],
    ["Vilas Shinde", "₹3,000", "8 days overdue"],
  ];

  const quickActions = [
    {
      label: "Add farmer",
      icon: Agriculture,
      path: "/farmers/new",
      color: "#286044",
    },
    {
      label: "Record daily work",
      icon: AddTask,
      path: "/daily-work",
      color: "#315DB5",
    },
    {
      label: "Generate bill",
      icon: ReceiptLong,
      path: "/billing",
      color: "#A36A0B",
    },
    {
      label: "Record payment",
      icon: Payments,
      path: "/payments",
      color: "#16836B",
    },
    {
      label: "Monthly report",
      icon: Assessment,
      path: "/reports",
      color: "#7651A8",
    },
  ];

  return (
    <Stack spacing={3}>
      {/* Welcome section */}
      <Box
        sx={{
          p: { xs: 2.5, md: 3.5 },
          borderRadius: 3,
          background: "linear-gradient(120deg, #1D4933, #387653)",
          color: "#ffffff",
        }}
      >
        <Typography variant="h4" fontWeight={800}>
          Namaskar! 👋
        </Typography>

        <Typography sx={{ mt: 1, opacity: 0.9 }}>
          Welcome to JKServices — your business overview.
        </Typography>

        <Chip
          label="Business workspace"
          sx={{
            mt: 2,
            color: "#ffffff",
            borderColor: "rgba(255,255,255,0.5)",
          }}
          variant="outlined"
        />
      </Box>

      {/* Metrics */}
      <Box>
        <Typography variant="h6" sx={{ mb: 2 }}>
          Business overview
        </Typography>

        <Grid container spacing={2}>
          {metrics.map((metric) => (
            <Grid key={metric.label} size={{ xs: 12, sm: 6, xl: 3 }}>
              <MetricCard metric={metric} />
            </Grid>
          ))}
        </Grid>
      </Box>

      {/* Overdue and quick actions */}
      <Grid container spacing={2}>
        <Grid size={{ xs: 12, lg: 7 }}>
          <Card sx={{ height: "100%", borderRadius: 3 }}>
            <CardContent sx={{ p: 2.5 }}>
              <Stack
                direction="row"
                justifyContent="space-between"
                alignItems="center"
                sx={{ mb: 1 }}
              >
                <Typography variant="h6">
                  Delayed billing & overdue bills
                </Typography>

                <Chip label="Attention needed" color="error" size="small" />
              </Stack>

              <Typography variant="body2" color="text.secondary">
                Follow up on these outstanding accounts.
              </Typography>

              {overdueItems.map(([name, amount, delay]) => (
                <Stack
                  key={name}
                  direction="row"
                  justifyContent="space-between"
                  alignItems="center"
                  sx={{
                    py: 2,
                    borderBottom: "1px solid #edf0eb",
                  }}
                >
                  <Box>
                    <Typography fontWeight={700}>{name}</Typography>

                    <Typography variant="caption" color="error.main">
                      {delay}
                    </Typography>
                  </Box>

                  <Typography fontWeight={800}>{amount}</Typography>
                </Stack>
              ))}
            </CardContent>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, lg: 5 }}>
          <Card sx={{ height: "100%", borderRadius: 3 }}>
            <CardContent sx={{ p: 2.5 }}>
              <Typography variant="h6" sx={{ mb: 2 }}>
                Quick actions
              </Typography>

              <Stack spacing={1.5}>
                {quickActions.map((action) => {
                  const Icon = action.icon;

                  return (
                    <Button
                      key={action.label}
                      fullWidth
                      variant="outlined"
                      startIcon={<Icon />}
                      onClick={() => navigate(action.path)}
                      sx={{
                        justifyContent: "flex-start",
                        py: 1.2,
                        px: 2,
                        color: action.color,
                        borderColor: action.color,
                        "&:hover": {
                          backgroundColor: `${action.color}10`,
                          borderColor: action.color,
                        },
                      }}
                    >
                      {action.label}
                    </Button>
                  );
                })}
              </Stack>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Stack>
  );
}
