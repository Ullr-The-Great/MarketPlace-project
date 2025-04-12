<template>
    <button 
      @click="handleLogout"
      class="logout-button"
      :disabled="loading"
    >
      <span v-if="!loading">Cerrar sesión</span>
      <span v-else class="loading">Cerrando sesión...</span>
    </button>
  </template>
  
  <script setup lang="ts">
  import { ref } from 'vue';
  import { useAuthStore } from '@/stores/authStore';
  import { useRouter } from 'vue-router';
  
  const authStore = useAuthStore();
  const router = useRouter();
  const loading = ref(false);
  
  const handleLogout = async () => {
    if (!authStore.isAuthenticated) {
    // Si no está autenticado, simplemente limpiar y redirigir
    authStore.clearAuth();
    router.push('/login');
    return;
  }
    loading.value = true;
    try {
      await authStore.logout();
      // Redirigir con mensaje opcional
      router.push({ path: '/login', query: { logout: 'success' } });
    } catch (error) {
      console.error('Logout failed:', error);
    } finally {
      loading.value = false;
    }
  };
  </script>
  
  <style scoped>
  .logout-button {
    padding: 0.5rem 1rem;
    background-color: #ef4444;
    color: white;
    border: none;
    border-radius: 0.375rem;
    cursor: pointer;
    transition: background-color 0.2s;
  }
  
  .logout-button:hover {
    background-color: #dc2626;
  }
  
  .logout-button:disabled {
    opacity: 0.7;
    cursor: not-allowed;
  }
  
  .loading {
    display: inline-flex;
    align-items: center;
  }
  </style>