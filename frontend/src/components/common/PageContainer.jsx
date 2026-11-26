import { Container } from "@mui/material";

export default function PageContainer({
  children,
  maxWidth = "xl",
  disableGutters = false,
  sx = {},
}) {
  return (
    <Container
      maxWidth={maxWidth}
      disableGutters={disableGutters}
      sx={{
        py: { xs: 2, sm: 3 },
        ...sx,
      }}
    >
      {children}
    </Container>
  );
}
