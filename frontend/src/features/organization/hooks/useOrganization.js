import { useCallback, useState } from "react";
import organizationService from "../api/organizationService";

function getErrorMessage(error) {
  return (
    error?.response?.data?.detail ||
    error?.response?.data?.message ||
    error?.message ||
    "Something went wrong. Please try again."
  );
}

export default function useOrganization() {
  const [organizations, setOrganizations] = useState([]);
  const [organization, setOrganization] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const loadOrganizations = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const result = await organizationService.getAll();
      setOrganizations(Array.isArray(result) ? result : []);
      return result;
    } catch (err) {
      setError(getErrorMessage(err));
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const loadOrganization = useCallback(async (id) => {
    setLoading(true);
    setError("");

    try {
      const result = await organizationService.getById(id);
      setOrganization(result);
      return result;
    } catch (err) {
      setError(getErrorMessage(err));
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const createOrganization = async (payload) => {
    setLoading(true);
    setError("");

    try {
      return await organizationService.create(payload);
    } catch (err) {
      setError(getErrorMessage(err));
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const updateOrganization = async (id, name) => {
    setLoading(true);
    setError("");

    try {
      return await organizationService.update(id, name);
    } catch (err) {
      setError(getErrorMessage(err));
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const deactivateOrganization = async (id) => {
    setLoading(true);
    setError("");

    try {
      return await organizationService.deactivate(id);
    } catch (err) {
      setError(getErrorMessage(err));
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return {
    organizations,
    organization,
    loading,
    error,
    loadOrganizations,
    loadOrganization,
    createOrganization,
    updateOrganization,
    deactivateOrganization,
  };
}
