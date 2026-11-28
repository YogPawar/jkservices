import api from "../../../apiClient/apiClient";

const getBaseUrl = (organizationId) =>
  `/organizations/${organizationId}/members`;

const organizationMemberService = {
  async getAll(organizationId) {
    const response = await api.get(getBaseUrl(organizationId));
    return response.data.data;
  },

  async getById(organizationId, memberId) {
    const response = await api.get(`${getBaseUrl(organizationId)}/${memberId}`);
    return response.data.data;
  },

  async create(organizationId, payload) {
    const response = await api.post(getBaseUrl(organizationId), payload);
    return response.data.data;
  },

  async update(organizationId, memberId, payload) {
    const response = await api.put(
      `${getBaseUrl(organizationId)}/${memberId}`,
      payload,
    );
    return response.data.data;
  },

  async remove(organizationId, memberId) {
    const response = await api.delete(
      `${getBaseUrl(organizationId)}/${memberId}`,
    );
    return response.data.data;
  },
};

export default organizationMemberService;
