import {
  AppBar,
  Box,
  Chip,
  IconButton,
  Toolbar,
  Typography,
} from "@mui/material";

import MenuIcon from "@mui/icons-material/Menu";

export default function Topbar({ title, onMenuClick, showMenu }) {
  return (
    <AppBar
      position="sticky"
      color="inherit"
      elevation={0}
      sx={{ borderBottom: "1px solid #e6ebe4" }}
    >
      <Toolbar>
        {showMenu && (
          <IconButton
            edge="start"
            onClick={onMenuClick}
            sx={{ mr: 2 }}
            aria-label="Open navigation"
          >
            <MenuIcon />
          </IconButton>
        )}

        <Typography fontWeight={700} sx={{ flexGrow: 1 }}>
          {title}
        </Typography>

        <Chip
          label="Family Admin"
          color="success"
          variant="outlined"
          size="small"
        />
      </Toolbar>
    </AppBar>
  );
}
