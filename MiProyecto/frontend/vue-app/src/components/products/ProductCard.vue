<template>
  <div class="product-card">
    <div class="product-image-container">
      <router-link :to="`/products/${product.id}`">
        <img :src="mainImage" :alt="product.name" class="product-image">
      </router-link>
    </div>
    <div class="product-info">
      <h3 class="product-name">{{ product.name }}</h3>
      <div class="product-category">{{ product.category.name }}</div>
      <div class="product-pricing">
        <span class="product-price">${{ product.price.toFixed(2) }}</span>
        <span v-if="product.originalPrice" class="product-original-price">${{ product.originalPrice.toFixed(2) }}</span>
      </div>
      <div class="product-stock" :class="{ 'low-stock': product.stock < 5 }">
        Stock: {{ product.stock }}
      </div>
      <button 
        class="add-to-cart-button"
        @click="handleAddToCart"
        :disabled="addingToCart || product.stock <= 0"
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
import { ref, computed } from 'vue';
import router from '@/router';
import placeholderImage from '@/assets/product-placeholder.png';

const props = defineProps<{
  product: Product;
}>();

// Usa la primera imagen de imageUrls como principal
const mainImage = computed(() => {
  return props.product.imageUrls?.[0] || placeholderImage;
});
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
    console.log("error en product card", error);
    router.push('/login');
  } finally {
    addingToCart.value = false;
  }
};
</script>

  <style scoped>
    @import "@/styles/product-cards.css";
  </style>