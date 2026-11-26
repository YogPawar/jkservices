import { useNavigate, useParams } from "react-router-dom";
import { Alert } from "@mui/material";
import PageContainer from "../../../components/common/PageContainer";
import PageHeader from "../../../components/common/PageHeader";
import BusinessForm from "../components/BusinessForm";
import useBusinesses from "../hooks/useBusinesses";

const AddBusinessPage = () => {
  const { organizationId } = useParams();
  const navigate = useNavigate();

  const { createBusiness, loading, error } = useBusinesses();

  const handleSubmit = async (payload) => {
    await createBusiness(organizationId, payload);
    navigate(`/organizations/${organizationId}/businesses`);
  };

  return (
    <PageContainer>
      <PageHeader
        title="Add Business"
        subtitle="Create a new business under this organization"
      />

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <BusinessForm
        onSubmit={handleSubmit}
        onCancel={() => navigate(`/organizations/${organizationId}/businesses`)}
        loading={loading}
        submitLabel="Create Business"
      />
    </PageContainer>
  );
};

export default AddBusinessPage;
