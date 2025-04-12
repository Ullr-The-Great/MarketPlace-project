<template>
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
  .product-card {
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 16px;
    transition: transform 0.2s;
  }
  .product-card:hover {
    transform: translateY(-5px);
  }
  .product-image-placeholder {
    width: 100%;
    height: 200px;
    background-color: #f0f0f0;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 3rem;
    color: #666;
    margin-bottom: 1rem;
  }
  .price {
    font-weight: bold;
    color: #2c3e50;
    display: block;
    margin: 8px 0;
  }
  .category {
    color: #666;
    font-size: 0.9rem;
  }
  .stock {
    margin: 8px 0;
    font-size: 0.9rem;
  }
  .low-stock {
    color: #e74c3c;
  }
  button:disabled {
    background-color: #ccc;
    cursor: not-allowed;
  }
  </style>