import axios from "axios";
import { useContext, useMemo } from "react";
import { AuthContext } from "./context/AuthContext";

export const useApi = () => {
  const { token } = useContext(AuthContext);

  const API = useMemo(() => {
    const instance = axios.create({
      baseURL: "http://localhost:8085", // API Gateway
      headers: {
        'Content-Type': 'application/json'
      }
    });

    instance.interceptors.request.use(
      (config) => {
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        console.error("Request interceptor error:", error);
        return Promise.reject(error);
      }
    );

    instance.interceptors.response.use(
      (response) => response,
      (error) => {
        console.error("Response error:", error.response?.data || error.message);
        return Promise.reject(error);
      }
    );

    return instance;
  }, [token]);

  return API;
};