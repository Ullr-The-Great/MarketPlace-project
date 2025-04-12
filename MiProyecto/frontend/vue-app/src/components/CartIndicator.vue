<template>
    <div class="cart-indicator">
      <button @click="goToCart" class="cart-button">
        <span class="cart">🛒</span> <span class="badge" v-if="cartItemsCount > 0">{{ cartItemsCount }}</span>
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

  } catch (error:any) {
    console.error('Error al ir al carrito:', error);
    // Opcional: redirigir a login si hay error de autenticación
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
  background: rgb(165, 221, 165);
  border-radius: 10px;
  cursor: pointer;
}

.cart-button:hover{
  border: solid 3px white;
}
  
  .badge {
    background-color: #42b983;
    color: white;
    border-radius: 50%;
    padding: 2px 6px;
    font-size: 0.8rem;
    position: absolute;
    top: -8px;
    right: -8px;
  }

  </style>