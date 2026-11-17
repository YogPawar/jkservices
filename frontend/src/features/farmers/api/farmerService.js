import api from "../../../api/client";

/**
 * Validates the family ID required for family-scoped operations.
 */
function validateFamilyId(familyId) {
  if (familyId === null || familyId === undefined || familyId === "") {
    throw new Error("Family ID is required");
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
 * Creates a farmer under the specified family.
 */
export async function createFarmer(familyId, farmerData) {
  validateFamilyId(familyId);

  const response = await api.post(
    `/families/${encodeURIComponent(familyId)}/farmers`,
    farmerData,
  );

  return response.data;
}

/**
 * Retrieves all farmers belonging to a family.
 */
export async function getFarmers(familyId) {
  validateFamilyId(familyId);

  const response = await api.get(
    `/families/${encodeURIComponent(familyId)}/farmers`,
  );

  return response.data;
}

/**
 * Retrieves a specific farmer belonging to a family.
 */
export async function getFarmer(familyId, farmerId) {
  validateFamilyId(familyId);
  validateFarmerId(farmerId);

  const response = await api.get(
    `/families/${encodeURIComponent(familyId)}/farmers/${encodeURIComponent(farmerId)}`,
  );

  return response.data;
}

/**
 * Updates a farmer belonging to a family.
 */
export async function updateFarmer(familyId, farmerId, farmerData) {
  validateFamilyId(familyId);
  validateFarmerId(farmerId);

  const response = await api.put(
    `/families/${encodeURIComponent(familyId)}/farmers/${encodeURIComponent(farmerId)}`,
    farmerData,
  );

  return response.data;
}

/**
 * Soft-deactivates a farmer.
 * The backend retains the farmer record.
 */
export async function deactivateFarmer(familyId, farmerId) {
  validateFamilyId(familyId);
  validateFarmerId(farmerId);

  const response = await api.delete(
    `/families/${encodeURIComponent(familyId)}/farmers/${encodeURIComponent(farmerId)}`,
  );

  return response.data;
}
