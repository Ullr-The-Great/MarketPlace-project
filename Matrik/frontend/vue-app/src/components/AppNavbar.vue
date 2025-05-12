<template>
  <nav class="navbar">
    <div class="navbar-container">
      <!-- Logo -->
      <router-link to="/products" class="nav-link navbar-brand">
        <img src="@/assets/logo.png" class="logo" alt="home">
      </router-link>

      <!-- Barra de búsqueda -->
      <div class="search-bar">
        <select v-model="selectedCategory" class="category-filter" @change="handleSearch">
          <option value="">All Categories</option>
          <option v-for="category in productStore.categories" :key="category.id" :value="category.id">
            {{ category.name }}
          </option>
        </select>
        <input 
        type="text" v-model="searchQuery" 
        placeholder="Search for products..." 
        class="search-input"  
        @input="handleSearch"/>
      </div>

      <!-- Acciones de usuario -->
      <div class="navbar-actions">
        <template v-if="authStore.isAuthenticated">
          <div class="user-section">
            <logout-button />
          </div>
        </template>
        <template v-else>
          <router-link to="/login" class="nav-link nav-login">Sign In</router-link>
        </template>
        <CartIndicator />
      </div>
    </div>
  </nav>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import { useAuthStore } from '@/stores/authStore';
import { useProductStore } from '@/stores/productStore';
import CartIndicator from './CartIndicator.vue';
import LogoutButton from './LogoutButton.vue';

const authStore = useAuthStore();
const productStore = useProductStore();

const searchQuery = ref<string>(''); 
const selectedCategory = ref<string>('');

// Manejar la búsqueda
const handleSearch = () => {
  productStore.updateFilters({
    searchQuery: searchQuery.value,
    categoryId: selectedCategory.value ? Number(selectedCategory.value) : null,
  });
  productStore.searchProducts(searchQuery.value, Number(selectedCategory.value) || null);
};


onMounted(() => {
  productStore.fetchCategories();
});
</script>

<style scoped>
.navbar {
  background-color: var(--navbar-bg);
  padding: 1rem 2rem;
  color: var(--navbar-bg);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
  height: 100px;
  display: flex;
  width: 100%;
}

.navbar-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 0 5vw;
  flex-grow: 1;
}

.navbar-links {
  display: flex;
  gap: 2rem;
  align-items: center;
}

.nav-link {
  color: var(--nav-text);
  text-decoration: none;
  transition: color 0.3s;
  border-radius: 4px;
  font-weight: 700;
  font-family: Arial, Helvetica, sans-serif;
  min-width: 7vw;
  min-height: 6vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-content: center;
  text-align: center;
}

.nav-login:hover {
  background-color: var(--navbar-bg-hover);
}

.cart-link {
  position: relative;
  margin-right: 2rem;
}

.navbar-actions {
  display: flex;
  gap: 1.5rem;
  justify-content: space-evenly;
  width: 15vw;
}

.user-section {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.username {
  font-size: 0.9rem;
  color: var(--nav-text-secondary);
}

.logo {
  width: 200px;
  height: 80px;
}

.search-bar {
  display: flex;
  align-items: center;
  width: 50vw;
  gap: 0.5rem;
}

.category-filter {
  padding: 0.5rem;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  background-color: var(--secondary-background-color);
}

.search-input {
  flex-grow: 1;
  padding: 0.5rem;
  border: 1px solid var(--border-color);
  border-radius: 4px;
}

.search-button {
  padding: 0.5rem 1rem;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.search-button:hover {
  background-color: var(--primary-color-hover);
}
</style>