import { createTheme } from "@mui/material/styles";

const theme = createTheme({
  palette: {
    primary: {
      main: "#286044",
      dark: "#1d4933",
      light: "#e8f0eb",
    },
    secondary: {
      main: "#d99a28",
    },
    background: {
      default: "#f5f7f3",
      paper: "#ffffff",
    },
    success: {
      main: "#2e7d32",
    },
  },
  shape: {
    borderRadius: 12,
  },
  typography: {
    fontFamily: '"Roboto", "Arial", sans-serif',
    h4: {
      fontWeight: 750,
    },
    h6: {
      fontWeight: 700,
    },
    button: {
      textTransform: "none",
      fontWeight: 600,
    },
  },
  components: {
    MuiCard: {
      styleOverrides: {
        root: {
          border: "1px solid #e6ebe4",
          boxShadow: "none",
        },
      },
    },
  },
});

export default theme;
