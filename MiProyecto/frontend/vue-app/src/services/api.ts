import axios from 'axios';
import { useAuthStore } from '@/stores/authStore';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  timeout: 10000,
});

api.interceptors.request.use(config => {
  const authStore = useAuthStore();
  const token = authStore.token || localStorage.getItem('auth_token');
  
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  
  return config;
});

api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      const authStore = useAuthStore();
      authStore.logout();
    }
    return Promise.reject(error);
  }
);

export default api; 