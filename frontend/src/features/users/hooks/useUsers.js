import { useCallback, useEffect, useState } from "react";
import { userApi } from "../api/userApi";

export default function useUsers(page, size) {
  const [users, setUsers] = useState([]);
  const [pagination, setPagination] = useState({
    page: 0,
    size,
    totalElements: 0,
    totalPages: 0,
    first: true,
    last: true,
  });

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchUsers = useCallback(async () => {
    try {
      setLoading(true);
      setError("");

      const result = await userApi.getUsers({ page, size });

      setUsers(result.content ?? []);
      setPagination({
        page: result.number ?? page,
        size: result.size ?? size,
        totalElements: result.totalElements ?? 0,
        totalPages: result.totalPages ?? 0,
        first: result.first ?? true,
        last: result.last ?? true,
      });
    } catch (err) {
      setError(
        err.response?.data?.detail ||
          err.response?.data?.message ||
          "Unable to load users. Please try again.",
      );
    } finally {
      setLoading(false);
    }
  }, [page, size]);

  useEffect(() => {
    fetchUsers();
  }, [fetchUsers]);

  return {
    users,
    pagination,
    loading,
    error,
    refresh: fetchUsers,
  };
}
