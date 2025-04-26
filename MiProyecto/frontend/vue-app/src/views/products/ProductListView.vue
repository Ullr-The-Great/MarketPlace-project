<template>
   <div class="filters">
      <input
        v-model="searchQuery"
        placeholder="Search products..."
        @input="handleSearch"
      >
      <select v-model="selectedCategoryId" @change="handleCategoryChange">
        <option :value="null">All Categories</option>
        <option 
          v-for="category in productStore.categories" 
          :key="category.id" 
          :value="category.id"
        >
          {{ category.name }}
        </option>
      </select>
    </div>
  <div class="products">
    <div class="product-cards">
      <div v-if="productStore.loading">Loading products...</div>
        <div v-else-if="productStore.error" class="error">
          Error: {{ productStore.error }}
        </div>
        <div v-else-if="productStore.filteredProducts.length === 0" class="no-products">
          No products found
        </div>
        <div v-else class="products-grid">
          <div v-for="product in productStore.filteredProducts" :key="product.id">
      {{ console.log('Product in parent:', product) }}
      <ProductCard :product="product" />
    </div>
        </div>
      </div>
    </div>
   
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useProductStore } from '@/stores/productStore';
import { useCartStore } from '@/stores/cartStore';
import { useAuthStore } from '@/stores/authStore';
import ProductCard from '@/components/products/ProductCard.vue';

const productStore = useProductStore();
const cartStore = useCartStore();
const authStore = useAuthStore();
const router = useRouter();

const searchQuery = ref('');
const selectedCategoryId = ref<number | null>(null);

  const cartItemCount = computed(() => {
  const count = cartStore.totalItems;
  return isNaN(count) ? 0 : count;
});

// Cargar datos iniciales
onMounted(async () => {
  await productStore.fetchProducts();
  await productStore.fetchCategories();
  console.log("hola desde mounted productList", productStore.product)
});

const handleSearch = () => {
  if (searchQuery.value.length >= 1) {
    productStore.searchProducts(searchQuery.value);
  } else if (searchQuery.value.length === 0) {
    productStore.fetchProducts();
  }
};

const handleCategoryChange = () => {
  if (selectedCategoryId.value) {
    productStore.fetchProductsByCategory(selectedCategoryId.value);
  } else {
    productStore.fetchProducts();
  }
}
</script>

<style scoped>

  @import "@/styles/product-list.css";

</style>