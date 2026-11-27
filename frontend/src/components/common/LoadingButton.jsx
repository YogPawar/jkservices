import { Button, CircularProgress } from "@mui/material";

export default function LoadingButton({
  loading = false,
  disabled = false,
  children,
  startIcon,
  ...props
}) {
  return (
    <Button
      {...props}
      disabled={disabled || loading}
      startIcon={
        loading ? <CircularProgress size={18} color="inherit" /> : startIcon
      }
    >
      {children}
    </Button>
  );
}
