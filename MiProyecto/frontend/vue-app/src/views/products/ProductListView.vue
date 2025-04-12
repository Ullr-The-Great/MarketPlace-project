<template>
  <div class="products">
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

    <div v-if="productStore.loading">Loading products...</div>
    <div v-else-if="productStore.error" class="error">
      Error: {{ productStore.error }}
    </div>
    <div v-else-if="productStore.filteredProducts.length === 0" class="no-products">
      No products found
    </div>
    <div v-else class="products-grid">
      <ProductCard 
        v-for="product in productStore.filteredProducts" 
        :key="product.id" 
        :product="product" 
      />
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
  if (searchQuery.value.length > 2) {
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
.products {
  padding: 20px;
}
.filters {
  margin-bottom: 20px;
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}
.error {
  color: red;
}
.cart-button {
  padding: 8px 16px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.no-products {
  padding: 20px;
  text-align: center;
  font-style: italic;
}
</style>