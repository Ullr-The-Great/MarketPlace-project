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
    background-color: var(--navbar-bg);
    color: rgb(0, 0, 0);
    border: none;
    border-radius: 3px;
    cursor: pointer;
    transition: background-color 0.2s;
    min-width: 7vw;
    min-height: 6vh;
    font-weight: 550;
    font-size: 15px;
    font-family: Arial, Helvetica, sans-serif;
    align-self: flex-start;
    padding: 8px 16px;
  }
  
  .logout-button:hover {
    background-color: var(--navbar-bg-hover);
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