import { useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Alert, CircularProgress, Box } from "@mui/material";
import PageContainer from "../../../components/common/PageContainer";
import PageHeader from "../../../components/common/PageHeader";
import BusinessForm from "../components/BusinessForm";
import useBusinesses from "../hooks/useBusinesses";

const EditBusinessPage = () => {
  const { organizationId, businessId } = useParams();
  const navigate = useNavigate();

  const { business, loading, error, loadBusiness, updateBusiness } =
    useBusinesses();

  useEffect(() => {
    loadBusiness(organizationId, businessId).catch(() => {});
  }, [loadBusiness, organizationId, businessId]);

  const handleSubmit = async (payload) => {
    await updateBusiness(organizationId, businessId, payload);
    navigate(`/organizations/${organizationId}/businesses/${businessId}`);
  };

  if (loading && !business) {
    return (
      <PageContainer>
        <Box display="flex" justifyContent="center" p={4}>
          <CircularProgress />
        </Box>
      </PageContainer>
    );
  }

  return (
    <PageContainer>
      <PageHeader title="Edit Business" subtitle="Update business details" />

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {business && (
        <BusinessForm
          initialData={business}
          onSubmit={handleSubmit}
          onCancel={() =>
            navigate(
              `/organizations/${organizationId}/businesses/${businessId}`,
            )
          }
          loading={loading}
          submitLabel="Update Business"
        />
      )}
    </PageContainer>
  );
};

export default EditBusinessPage;
