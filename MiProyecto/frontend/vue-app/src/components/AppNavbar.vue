<template>
    <nav>
      <router-link to="/">Home</router-link>
      <router-link to="/products">Products</router-link>
      
      <div class="auth-section">
        <template v-if="authStore.isAuthenticated">
          <router-link to="/cart">
            Cart ({{ cartStore.totalItems }})
          </router-link>
          <button @click="authStore.logout">Logout</button>
          <span>Hi, {{ authStore.user?.username }}</span>
        </template>
        <template v-else>
          <router-link to="/login">Login</router-link>
        </template>
      </div>
    </nav>
  </template>
  
  <script setup lang="ts">
  import { useAuthStore } from '@/stores/authStore';
  import { useCartStore } from '@/stores/cartStore';
  
  const authStore = useAuthStore();
  const cartStore = useCartStore();
  </script>
  
  <style scoped>
  nav {
    display: flex;
    justify-content: space-between;
    padding: 1rem;
    background-color: #f0f0f0;
  }
  .auth-section {
    display: flex;
    gap: 1rem;
    align-items: center;
  }
  </style>