import apiClient from "../../../apiClient/apiClient";

const BASE_URL = "/users";

export const userApi = {
  async getUsers({ page = 0, size = 20 } = {}) {
    const response = await apiClient.get(BASE_URL, {
      params: {
        page,
        size,
        sort: "id,desc",
      },
    });

    return response.data.data;
  },

  async getUserById(id) {
    const response = await apiClient.get(`${BASE_URL}/${id}`);
    return response.data.data;
  },

  async createUser(payload) {
    const response = await apiClient.post(BASE_URL, payload);
    return response.data.data;
  },

  async updateUser(id, payload) {
    const response = await apiClient.put(`${BASE_URL}/${id}`, payload);
    return response.data.data;
  },

  async updateUserStatus(id, active) {
    const response = await apiClient.patch(`${BASE_URL}/${id}/status`, {
      active,
    });

    return response.data.data;
  },
};
