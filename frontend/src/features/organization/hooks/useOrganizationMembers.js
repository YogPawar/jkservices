import { useCallback, useEffect, useState } from "react";
import organizationMemberService from "../api/organizationMemberService";

const useOrganizationMembers = (organizationId) => {
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const loadMembers = useCallback(async () => {
    if (!organizationId) return;

    setLoading(true);
    setError("");

    try {
      const result = await organizationMemberService.getAll(organizationId);

      setMembers(Array.isArray(result) ? result : []);
    } catch (err) {
      setError(
        err.response?.data?.detail ||
          err.response?.data?.message ||
          "Unable to load organization members.",
      );
    } finally {
      setLoading(false);
    }
  }, [organizationId]);

  const addMember = async (payload) => {
    const result = await organizationMemberService.create(
      organizationId,
      payload,
    );
    await loadMembers();
    return result;
  };

  const updateMember = async (memberId, payload) => {
    const result = await organizationMemberService.update(
      organizationId,
      memberId,
      payload,
    );
    await loadMembers();
    return result;
  };

  const removeMember = async (memberId) => {
    const result = await organizationMemberService.remove(
      organizationId,
      memberId,
    );
    await loadMembers();
    return result;
  };

  useEffect(() => {
    loadMembers();
  }, [loadMembers]);

  return {
    members,
    loading,
    error,
    loadMembers,
    addMember,
    updateMember,
    removeMember,
  };
};

export default useOrganizationMembers;
