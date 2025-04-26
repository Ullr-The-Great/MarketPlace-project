<template>
  <nav class="navbar">
    <div class="navbar-container">
      <div class="navbar-links">
        <router-link to="/products" class="nav-link navbar-brand">
          <img src="@/assets/logo.png" class="logo" alt="home">

        </router-link>

      </div>

      <div class="navbar-actions">
        <template v-if="authStore.isAuthenticated">
          <div class="user-section">
            <logout-button />
          </div>
        </template>
        <template v-else>
          <router-link to="/login" class="nav-link">Sign In</router-link>
        </template>
        <CartIndicator />
      </div>
    </div>
  </nav>

</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';
import { useCartStore } from '@/stores/cartStore';
import LogoutButton from '@/components/LogoutButton.vue';
import { useRouter } from 'vue-router';
import CartIndicator from './CartIndicator.vue';

const authStore = useAuthStore();
const cartStore = useCartStore();
const router = useRouter();

const handleCartClick = () => {

  router.push('/cart');

};
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
  width:100%;
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

.nav-link:hover{
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

.logo{
  width: 200px;
  height: 80px;
}
</style>