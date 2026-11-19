import { useCallback, useEffect, useState } from "react";

import {
  createFarmer,
  getFarmer,
  getFarmers,
  updateFarmer,
  deactivateFarmer,
} from "../api/farmerService";

export default function useFarmers(businessId) {
  const [farmers, setFarmers] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isMutating, setIsMutating] = useState(false);
  const [error, setError] = useState(null);

  /**
   * Retrieves all farmers for the current busines.
   */
  const loadFarmers = useCallback(async () => {
    if (businessId === null || businessId === undefined || businessId === "") {
      setFarmers([]);
      setError(new Error("Business ID is required"));
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      const response = await getFarmers(businessId);

      if (!response.success) {
        throw new Error(response.message || "Unable to retrieve farmers");
      }

      setFarmers(response.data ?? []);
    } catch (err) {
      setError(err);
    } finally {
      setIsLoading(false);
    }
  }, [businessId]);

  /**
   * Retrieves one farmer by ID.
   */
  const fetchFarmer = useCallback(
    async (farmerId) => {
      setError(null);

      try {
        const response = await getFarmer(businessId, farmerId);

        if (!response.success) {
          throw new Error(response.message || "Unable to retrieve farmer");
        }

        return response.data;
      } catch (err) {
        setError(err);
        throw err;
      }
    },
    [businessId],
  );

  /**
   * Creates a farmer and refreshes the list.
   */
  const addFarmer = useCallback(
    async (farmerData) => {
      setIsMutating(true);
      setError(null);

      try {
        const response = await createFarmer(businessId, farmerData);

        if (!response.success) {
          throw new Error(response.message || "Unable to create farmer");
        }

        await loadFarmers();

        return response.data;
      } catch (err) {
        setError(err);
        throw err;
      } finally {
        setIsMutating(false);
      }
    },
    [businessId, loadFarmers],
  );

  /**
   * Updates a farmer and refreshes the list.
   */
  const editFarmer = useCallback(
    async (farmerId, farmerData) => {
      setIsMutating(true);
      setError(null);

      try {
        const response = await updateFarmer(businessId, farmerId, farmerData);

        if (!response.success) {
          throw new Error(response.message || "Unable to update farmer");
        }

        await loadFarmers();

        return response.data;
      } catch (err) {
        setError(err);
        throw err;
      } finally {
        setIsMutating(false);
      }
    },
    [businessId, loadFarmers],
  );

  /**
   * Deactivates a farmer and refreshes the list.
   */
  const removeFarmer = useCallback(
    async (farmerId) => {
      setIsMutating(true);
      setError(null);

      try {
        const response = await deactivateFarmer(businessId, farmerId);

        if (!response.success) {
          throw new Error(response.message || "Unable to deactivate farmer");
        }

        await loadFarmers();

        return response;
      } catch (err) {
        setError(err);
        throw err;
      } finally {
        setIsMutating(false);
      }
    },
    [businessId, loadFarmers],
  );

  /**
   * Clears the current error.
   */
  const clearError = useCallback(() => {
    setError(null);
  }, []);

  useEffect(() => {
    loadFarmers();
  }, [loadFarmers]);

  return {
    farmers,
    isLoading,
    isMutating,
    error,
    loadFarmers,
    fetchFarmer,
    addFarmer,
    editFarmer,
    removeFarmer,
    clearError,
  };
}
