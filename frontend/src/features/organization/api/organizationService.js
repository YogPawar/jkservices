import api from "../../../apiClient/apiClient";

const BASE_URL = "/organizations";

const organizationService = {
  async getAll() {
    const response = await api.get(BASE_URL);
    return response.data.data;
  },

  async getById(organizationId) {
    const response = await api.get(`${BASE_URL}/${organizationId}`);
    return response.data.data;
  },

  async create(payload) {
    const response = await api.post(BASE_URL, payload);
    return response.data.data;
  },

  async update(organizationId, payload) {
    const response = await api.put(`${BASE_URL}/${organizationId}`, payload);

    return response.data.data;
  },

  async deactivate(organizationId) {
    const response = await api.delete(`${BASE_URL}/${organizationId}`);
    return response.data.data;
  },
};

export default organizationService;
