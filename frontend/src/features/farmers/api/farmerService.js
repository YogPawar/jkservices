import api from "../../../api/client";

/**
 * Validates the business ID required for business-scoped operations.
 */
function validateBusinessId(businessId) {
  if (businessId === null || businessId === undefined || businessId === "") {
    throw new Error("Business ID is required");
  }
}

/**
 * Validates the farmer ID required for farmer-specific operations.
 */
function validateFarmerId(farmerId) {
  if (farmerId === null || farmerId === undefined || farmerId === "") {
    throw new Error("Farmer ID is required");
  }
}

/**
 * Creates a farmer under the specified business.
 */
export async function createFarmer(businessId, farmerData) {
  validateBusinessId(businessId);

  const response = await api.post(
    `/businesses/${encodeURIComponent(businessId)}/farmers`,
    farmerData,
  );

  return response.data;
}

/**
 * Retrieves all farmers belonging to a business.
 */
export async function getFarmers(businessId) {
  validateBusinessId(businessId);

  const response = await api.get(
    `/businesses/${encodeURIComponent(businessId)}/farmers`,
  );

  return response.data;
}

/**
 * Retrieves a specific farmer belonging to a busines.
 */
export async function getFarmer(businessId, farmerId) {
  validateBusinessId(businessId);
  validateFarmerId(farmerId);

  const response = await api.get(
    `/businesses/${encodeURIComponent(businessId)}/farmers/${encodeURIComponent(farmerId)}`,
  );

  return response.data;
}

/**
 * Updates a farmer belonging to a business.
 */
export async function updateFarmer(businessId, farmerId, farmerData) {
  validateBusinessId(businessId);
  validateFarmerId(farmerId);

  const response = await api.put(
    `/businesses/${encodeURIComponent(businessId)}/farmers/${encodeURIComponent(farmerId)}`,
    farmerData,
  );

  return response.data;
}

/**
 * Soft-deactivates a farmer.
 * The backend retains the farmer record.
 */
export async function deactivateFarmer(businessId, farmerId) {
  validateBusinessId(businessId);
  validateFarmerId(farmerId);

  const response = await api.delete(
    `/businesses/${encodeURIComponent(businessId)}/farmers/${encodeURIComponent(farmerId)}`,
  );

  return response.data;
}

