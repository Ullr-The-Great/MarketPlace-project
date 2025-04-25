<template>
   <router-link 
    :to="`/products/${product.id}`" 
    class="product-card"
  >
    <div class="product-card">
      <div class="product-image-placeholder">
        {{ product.name.charAt(0) }}
        
      </div>
      <div class="product-info">
        <h3>{{ product.name }}</h3>
        <span class="price">${{ product.price.toFixed(2) }}</span>
        <div class="category">{{ product.category.name }}</div>
        <div class="stock" :class="{ 'low-stock': product.stock < 5 }">
          Stock: {{ product.stock }}
        </div>
        <button 
          @click="handleAddToCart"
          :disabled= "addingToCart"
        >
          {{ product.stock > 0 ? 'Add to cart' : 'Out of stock' }}
        </button>
      </div>
    </div>
  </router-link>
  </template>

  <script setup lang="ts">
  import type { Product } from '@/types/product';
  import { useAuthStore } from '@/stores/authStore';
  import { useCartStore } from '@/stores/cartStore';
  import { useRouter } from 'vue-router';
  import { ref } from 'vue';
import router from '@/router';

  const props = defineProps<{
    product: Product;
  }>();

  const cartStore = useCartStore();
  const addingToCart = ref(false);

  const handleAddToCart = async () => {
  if (addingToCart.value) return;
  
  addingToCart.value = true;
  
  try {

    if (!props.product?.id) {
      throw new Error('Producto inválido');
    }

    await cartStore.addToCart(props.product);
    console.log("Producto añadido correctamente");
  } catch (error) {
    console.log("error en product card", error)
    router.push('/login');
  } finally {
    addingToCart.value = false;
  }
}
  </script>

  <style scoped>
    @import "@/styles/product-cards.css";
  </style>