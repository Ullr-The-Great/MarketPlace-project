<template>
    <div class="cart-indicator">
      <button @click="goToCart">
        🛒 <span class="badge" v-if="cartItemsCount > 0">{{ cartItemsCount }}</span>
      </button>
    </div>
  </template>
  
  <script setup lang="ts">
  import { computed } from 'vue';
  import { useRouter } from 'vue-router';
  import { useCartStore } from '@/stores/cartStore';
  
  const cartStore = useCartStore();
  const router = useRouter();
  
  const cartItemsCount = computed(() => {
    return cartStore.totalItems || 0;
  });
  
  const goToCart = () => {
    router.push('/cart');
  };
  </script>
  
  <style scoped>
  .cart-indicator {
    position: relative;
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