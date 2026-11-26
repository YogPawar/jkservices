import { useCallback, useState } from "react";
import businessService from "../api/businessService";

const getErrorMessage = (error) =>
  error.response?.data?.detail ||
  error.response?.data?.message ||
  error.message ||
  "Something went wrong. Please try again.";

const useBusinesses = () => {
  const [businesses, setBusinesses] = useState([]);
  const [business, setBusiness] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const loadBusinesses = useCallback(async (organizationId) => {
    setLoading(true);
    setError(null);

    try {
      const data = await businessService.getAll(organizationId);
      setBusinesses(data || []);
      return data;
    } catch (err) {
      setError(getErrorMessage(err));
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const loadBusiness = useCallback(async (organizationId, businessId) => {
    setLoading(true);
    setError(null);

    try {
      const data = await businessService.getById(organizationId, businessId);
      setBusiness(data);
      return data;
    } catch (err) {
      setError(getErrorMessage(err));
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const createBusiness = async (organizationId, payload) => {
    setLoading(true);
    setError(null);

    try {
      return await businessService.create(organizationId, payload);
    } catch (err) {
      setError(getErrorMessage(err));
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const updateBusiness = async (organizationId, businessId, payload) => {
    setLoading(true);
    setError(null);

    try {
      const data = await businessService.update(
        organizationId,
        businessId,
        payload,
      );
      setBusiness(data);
      return data;
    } catch (err) {
      setError(getErrorMessage(err));
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const deactivateBusiness = async (organizationId, businessId) => {
    setLoading(true);
    setError(null);

    try {
      await businessService.deactivate(organizationId, businessId);
    } catch (err) {
      setError(getErrorMessage(err));
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return {
    businesses,
    business,
    loading,
    error,
    loadBusinesses,
    loadBusiness,
    createBusiness,
    updateBusiness,
    deactivateBusiness,
  };
};

export default useBusinesses;
