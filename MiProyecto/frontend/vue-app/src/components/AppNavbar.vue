<template>
    <nav class="navbar">
      <router-link to="/" class="navbar-brand">Home</router-link>
      <router-link to="/products">Products</router-link>
      
      <div class="auth-section">
        <template v-if="authStore.isAuthenticated">
          <router-link to="/cart">
            Cart ({{ cartStore.totalItems }})
          </router-link>
          <span>{{ authStore.user?.username }}</span>
        </template>
        <template v-else>
          <router-link to="/login">Login</router-link>
        </template>
        <div class="navbar-actions">
        <CartIndicator />
        <LogoutButton/>
      </div>
      </div>
    </nav>
  </template>
  
  <script setup>
  import { useAuthStore } from '@/stores/authStore';
  import { useCartStore } from '@/stores/cartStore';
  import CartIndicator from '@/components/CartIndicator.vue';
  import LogoutButton from './LogoutButton.vue';
  
  const authStore = useAuthStore();
  const cartStore = useCartStore();
  </script>
  
  <style scoped>
.navbar {
  background-color: #2c3e50;
  padding: 1rem 2rem;
  color: white;
  display: flex;
  justify-content: space-around;
  height: 100px;
  width: 100%;
}

.navbar-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

.navbar-brand {
  font-size: 1.5rem;
  font-weight: bold;
  color: white;
  text-decoration: none;
}

.navbar-links {
  display: flex;
  gap: 1.5rem;
}

.nav-link {
  color: white;
  text-decoration: none;
  transition: color 0.3s;
}

.nav-link:hover {
  color: #42b983;
}

.navbar-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.auth-section{
  display: flex;
  justify-content: space-between;
  width: 2em;
}

.navbar-actions{
  display: flex;
  justify-content: space-evenly;
  width: 300px;
}

  </style>