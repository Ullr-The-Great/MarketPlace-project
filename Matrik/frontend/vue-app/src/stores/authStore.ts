import { defineStore } from 'pinia';
import api from "@/services/api"
import type { User } from "@/types/user";
import { useCartStore } from './cartStore';
import router from '@/router';
import { useRouter } from 'vue-router';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null as User | null,
    token: null as string | null,
    loading: false,
    error: null as string | null,
  }),
  actions: {
    async login(credentials: { username: string; password: string}) {
      this.loading = true;
      try {
        const response = await api.post('/auth/login', {username:credentials.username, password: credentials.password});
        this.token = response.data.token;
        this.user = response.data.user;

        console.log("Desde antes en authStore", this.token)

        if (this.token) {
          localStorage.setItem('auth_token', this.token);
          console.log("Desde this.token authStore", localStorage.getItem('auth_token'))
        }
        
        if (router.currentRoute.value.path === '/login') {
          router.push('/products');
      }
      
      const cartStore= useCartStore();
      if(this.user){
        cartStore.initializeCart(this.user.id);
      }
      return response;
      } catch (error) {
        this.error = 'Login failed';
        
      } finally {
        this.loading = false;
      }
      
    },
    async logout() {
      try{
        if (this.token) {
          await api.post('/auth/logout');
        }
  
        const cartStore = useCartStore();
        cartStore.$reset();
        cartStore.clearCart();

        router.push('/login');
      } catch (error) {
        console.error("Error during logout:", error);
      }
      finally{

        this.token = null;
        this.user = null;
        localStorage.removeItem('auth_token');
        

        const cartStore = useCartStore();
        cartStore.$reset();
      
        this.clearAuth();
       
      }
      
    },
    async checkAuth() {
      const token = localStorage.getItem('auth_token');
      if (token) {
          try {
              this.loading = true;
              const response = await api.get('/auth/me');
              this.user = response.data;
              this.token = token;
          } catch (error) {
              this.logout();
          } finally {
              this.loading = false;
          }
      }
  },
  clearAuth() {
    this.token = null;
    this.user = null;
    localStorage.removeItem('auth_token');
    
    
    const cartStore = useCartStore();
    cartStore.$reset();
    
  }
  },
  getters: {
    isAuthenticated: (state) => state.user
  }
});