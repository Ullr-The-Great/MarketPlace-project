<template>
  <div class="products">
    <div class="product-cards">
      <div v-if="productStore.loading">Loading products...</div>
      <div v-else-if="productStore.error" class="error">
        Error: {{ productStore.error }}
      </div>
      <div v-else-if="!productStore.products || productStore.products.length === 0" class="no-products">
        No products found
      </div>
      <div v-else class="products-grid">
        <ProductCard v-for="product in productStore.products" :key="product.id" :product="product" />
      </div>
    </div>

    <!-- Controles de paginación -->
    <div class="pagination-controls" v-if="productStore.totalPages > 1">
      <button
        :disabled="productStore.currentPage === 0"
        @click="fetchPreviousPage"
      >
        Previous
      </button>
      <span>Page {{ productStore.currentPage + 1 }} of {{ productStore.totalPages }}</span>
      <button
        :disabled="productStore.currentPage === productStore.totalPages - 1"
        @click="fetchNextPage"
      >
        Next
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useProductStore } from '@/stores/productStore';
import ProductCard from '@/components/products/ProductCard.vue';

const productStore = useProductStore();

const fetchPreviousPage = () => {
  if (productStore.currentPage > 0) {
    productStore.fetchPaginatedProducts(productStore.currentPage - 1);
  }
};

const fetchNextPage = () => {
  if (productStore.currentPage < productStore.totalPages - 1) {
    productStore.fetchPaginatedProducts(productStore.currentPage + 1);
  }
};

// Cargar la primera página al montar el componente
onMounted(() => {
  productStore.fetchPaginatedProducts();
});
</script>

<style scoped>

  @import "@/styles/product-list.css";

</style>