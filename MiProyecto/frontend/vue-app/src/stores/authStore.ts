import { defineStore } from 'pinia';
import api from "@/services/api.ts"
import type { User } from "@/types/user.ts";
import { useCartStore } from './cartStore';
import router from '@/router';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null as User | null,
    token: null as string | null,
    loading: false,
    error: null as string | null,
  }),
  actions: {
    async login(credentials: { email: string; password: string}) {
      this.loading = true;
      try {
        const response = await api.post('/auth/login', credentials);
        this.token = response.data.token;
        this.user = response.data.user;
        if (this.token) {
          localStorage.setItem('auth_token', this.token);
        }
        const cartStore = useCartStore();
        if (this.user?.id !== undefined) {
          await cartStore.initializeCart(this.user.id);
        }
        router.push("/");
      } catch (error) {
        this.error = error instanceof Error? error.message : 'Login failed';
        throw error;
      } finally {
        this.loading = false;
      }
      
    },
    async logout() {
      this.token = null;
      this.user = null;
      localStorage.removeItem('auth_token');

      // Limpiar carrito
      const cartStore = useCartStore();
      cartStore.clearCart();
      
      // Redirigir a login
      router.push('/login');
    },
    async checkAuth() {
      const token = localStorage.getItem("auth_token");
      if(token){
        try{
          this.loading = true;
          const response = await api.get('/auth/me');
          this.user = response.data;
          this.token = token;

          // Inicializar carrito del usuario
          const cartStore = useCartStore();
          if(this.user?.id !== undefined){
            await cartStore.initializeCart(this.user.id);
          }
          
        } catch (error) {
          this.logout();
        } finally {
          this.loading = false;
        }
      }
    }
  },
  getters: {
    isAuthenticated: (state) => !state.user
  }
});