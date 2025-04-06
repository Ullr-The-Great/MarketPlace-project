<template>
  <Suspense>
    <div class="products">
        <button>  <FontAwesomeIcon :icon="fas.faHouse" /></button>
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
      <div v-else class="products-grid">
        <ProductCard 
          v-for="product in productStore.filteredProducts" 
          :key="product.id" 
          :product="product" 
        />
      </div>
    </div>
  </Suspense>
  </template>
  
  <script setup lang="ts">
  import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { fas, fad, fass, fasds } from '@awesome.me/kit-KIT_CODE/icons'
  import { ref, onMounted } from 'vue';
  import { useProductStore } from '../../stores/productStore';
  import ProductCard from '../../components/products/ProductCard.vue';
  
  const productStore = useProductStore();
  
  const searchQuery = ref('');
  const selectedCategoryId = ref<number | null>(null);
  
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
  };
  
  onMounted(async () => {
    await productStore.fetchProducts();
    await productStore.fetchCategories();
  });
  </script>
  
  <style scoped>
  .product-list {
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
  .price-range {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  </style>