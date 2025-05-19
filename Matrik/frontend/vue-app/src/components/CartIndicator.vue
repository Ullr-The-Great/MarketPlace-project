<template>
  <div class="cart-indicator">
    <button @click="goToCart" class="cart-button">
      <div class="cart-image">
        <img src="@/assets/black-cart.png" alt="carrito"> <span class="badge" v-if="cartItemsCount >= 0">{{ cartItemsCount }}</span>
      </div>
    </button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useCartStore } from '@/stores/cartStore';
import { useAuthStore } from '@/stores/authStore';

const cartStore = useCartStore();
const authStore = useAuthStore();
const router = useRouter();

const cartItemsCount = computed(() => {
  return cartStore.totalItems || 0;
});

const goToCart = async () => {
  try {
    router.push({ name: 'cart' });

  } catch (error: any) {
    console.error('Error al ir al carrito:', error);
    if (error.response?.status === 401) {
      router.push('/login');
    }
  }
};
</script>

<style scoped>
.cart-indicator {
  position: relative;
}

.cart-button {
  position: relative;
  padding: 8px 16px;
  background: var(--navbar-bg);
  border-radius: 3px;
  cursor: pointer;
  min-width: 7vw;
  min-height: 6vh;
  border: none;
}

.cart-button:hover {
  background-color: var(--navbar-bg-hover);
}

.badge {
  background-color: #bb4545;
  color: var(--navbar-bg);
  border-radius: 50%;
  padding: 2px 6px;
  font-size: 0.8rem;
  position: absolute;
  transform: scale(1.4);
  top: 1.9vh;
}

.cart-image img{
  position: relative;
  justify-self: start;
  right: 1vw;
}

</style>