import { Box, Stack, Typography } from "@mui/material";

export default function PageHeader({
  title,
  subtitle,
  description,
  action,
  actions,
  children,
}) {
  const supportingText = subtitle || description;
  const headerActions = actions || action;

  return (
    <Stack
      direction={{ xs: "column", sm: "row" }}
      justifyContent="space-between"
      alignItems={{ xs: "stretch", sm: "center" }}
      spacing={2}
      sx={{ mb: 3 }}
    >
      <Box>
        <Typography variant="h4" component="h1" fontWeight={600}>
          {title}
        </Typography>

        {supportingText && (
          <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
            {supportingText}
          </Typography>
        )}
      </Box>

      {headerActions && <Box sx={{ flexShrink: 0 }}>{headerActions}</Box>}

      {children}
    </Stack>
  );
}
