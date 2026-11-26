import api from "../../../apiClient/apiClient";

const BASE_URL = "/organizations";

const businessService = {
  async getAll(organizationId) {
    const response = await api.get(`${BASE_URL}/${organizationId}/businesses`);
    return response.data.data;
  },

  async getById(organizationId, businessId) {
    const response = await api.get(
      `${BASE_URL}/${organizationId}/businesses/${businessId}`,
    );
    return response.data.data;
  },

  async create(organizationId, payload) {
    const response = await api.post(
      `${BASE_URL}/${organizationId}/businesses`,
      payload,
    );
    return response.data.data;
  },

  async update(organizationId, businessId, payload) {
    const response = await api.put(
      `${BASE_URL}/${organizationId}/businesses/${businessId}`,
      payload,
    );
    return response.data.data;
  },

  async deactivate(organizationId, businessId) {
    const response = await api.delete(
      `${BASE_URL}/${organizationId}/businesses/${businessId}`,
    );
    return response.data.data;
  },
};

export default businessService;
