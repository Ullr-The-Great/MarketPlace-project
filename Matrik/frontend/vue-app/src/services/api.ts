import axios from 'axios';
import { useAuthStore } from '@/stores/authStore';
import { useRouter } from 'vue-router';
import { useCartStore } from '@/stores/cartStore';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  timeout: 10000,
  withCredentials:true
});

api.interceptors.request.use(config => {
  const token = localStorage.getItem('auth_token');

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

api.interceptors.response.use(
  response => response,
 async error => {
  const authStore = useAuthStore();
  const router = useRouter();
    if (error.response?.status === 401 || error.response?.status === 403) {
      router.push({ path: '/login', query: { sessionExpired: 'true' } });
      await authStore.logout();
      console.log("Error desde la api.ts",error)
    }
    return Promise.reject(error);
  }
);

export default api; 