<template>
    <div class="product-view">
        <div v-if="loading" class="loading-container">
            <div class="loader"></div>
        </div>

        <div v-else-if="error" class="error-container">
            <h2>Product not found</h2>
            <router-link to="/products" class="back-button">Back to Products</router-link>
        </div>

        <div v-else class="product-container">
            <!-- Galería de imágenes -->
            <div class="product-gallery">
                <div class="main-image">
                    <img :src="mainImage || '@/assets/product-placeholder.png'" :alt="product.name">
                </div>
                <div class="thumbnail-grid">
                    <div v-for="(image, index) in product.imageUrls" :key="index" class="thumbnail"
                        :class="{ active: mainImage === image }" @click="mainImage = image">
                        <img :src="image" :alt="`Thumbnail ${index + 1}`">
                    </div>
                </div>
            </div>

            <!-- Información del producto -->
            <div class="product-info">
                <h1 class="product-title">{{ product.name }}</h1>

                <div class="price-section">
                    <span class="current-price">{{ formattedPrice }}</span>
                    <span v-if="product.originalPrice" class="original-price">{{ formattedOriginalPrice }}</span>
                </div>

                <div class="stock-status"
                    :class="{ 'in-stock': product.stock > 0, 'out-of-stock': product.stock <= 0 }">
                    {{ stockStatus }}
                </div>

                <div class="quantity-selector">
                    <label>Quantity:</label>
                    <input type="number" v-model.number="quantity" :max="product.stock" min="1" class="quantity-input">
                </div>

                <button class="add-to-cart-button" @click="addToCart" :disabled="product.stock <= 0">
                    Add to Cart
                </button>

                <div class="product-description">
                    <h3>About this product</h3>
                    <p>{{ product.description }}</p>
                </div>

                <div class="specifications">
                    <h3>Specifications</h3>
                    <div class="spec-grid">
                        <div class="spec-item">
                            <span class="spec-label">Category:</span>
                            <span class="spec-value">{{ product.category?.name }}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        {{ console.log(product.id) }}
        <ProductReviews v-if="product.id > 0" :productId="product.id" />
    </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useCartStore } from '@/stores/cartStore';
import api from '@/services/api';
import { Product } from '@/types/product';
import ProductReviews from '@/components/reviews/ProductReviews.vue';


const route = useRoute();
const cartStore = useCartStore();

const product = ref<Product>({
  id: 0,
  name: '',
  description: '',
  price: 0,
  category: { id: 0, name: '' },
  stock: 0,
  originalPrice: undefined,
  imageUrls: [], // Cambiar a `imageUrls` para manejar múltiples imágenes
  createdAt: undefined
});

const mainImage = ref<string>('');
const quantity = ref<number>(1);
const loading = ref<boolean>(true);
const error = ref<string>('');


const formattedPrice = computed(() => {
    return new Intl.NumberFormat('es-ES', {
        style: 'currency',
        currency: 'EUR'
    }).format(product.value.price);
});

const formattedOriginalPrice = computed(() => {
    return product.value.originalPrice
        ? new Intl.NumberFormat('es-ES', {
            style: 'currency',
            currency: 'EUR'
        }).format(product.value.originalPrice)
        : '';
});

const stockStatus = computed(() => {
    return product.value.stock > 0
        ? `In Stock (${product.value.stock} available)`
        : 'Currently Unavailable';
});

onMounted(async () => {
    try {
        const response = await api.get(`/products/${route.params.id}`);
        product.value = response.data as Product;
        mainImage.value = product.value.imageUrls?.[0] || ''; // Usar la primera imagen como principal
        loading.value = false;
    } catch (err) {
        error.value = 'Failed to load product details';
        loading.value = false;
    }
});

const addToCart = () => {
    cartStore.addToCart(
        product.value,
        quantity.value
    );
};
</script>   

<style scoped>
.product-view {
    max-width: 1400px;
    margin: 2rem auto;
    padding: 0 1rem;
}

.product-container {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 2rem;
}

.product-gallery {
    position: sticky;
    top: 1rem;
}

.main-image img {
    width: 100%;
    max-height: 600px;
    object-fit: contain;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.thumbnail-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 1rem;
    margin-top: 1rem;
}

.thumbnail {
    cursor: pointer;
    border: 2px solid transparent;
    border-radius: 4px;
    transition: all 0.2s;
}

.thumbnail.active {
    border-color: var(--primary-color);
}

.thumbnail img {
    width: 100%;
    height: 100px;
    object-fit: cover;
    border-radius: 4px;
}

.product-info {
    padding: 0 2rem;
}

.product-title {
    font-size: 2rem;
    margin-bottom: 1.5rem;
}

.price-section {
    margin-bottom: 1.5rem;
}

.current-price {
    font-size: 1.8rem;
    color: var(--primary-color);
    font-weight: bold;
    margin-right: 1rem;
}

.original-price {
    font-size: 1.2rem;
    color: #666;
    text-decoration: line-through;
}

.stock-status {
    font-size: 1.1rem;
    margin-bottom: 1.5rem;
    padding: 0.5rem;
    border-radius: 4px;
}

.stock-status.in-stock {
    color: #008a00;
    background-color: #e6f4ea;
}

.stock-status.out-of-stock {
    color: #cc0000;
    background-color: #ffe6e6;
}

.quantity-selector {
    margin-bottom: 2rem;
}

.quantity-input {
    width: 100px;
    padding: 0.5rem;
    border: 1px solid var(--border-color);
    border-radius: 4px;
    margin-left: 1rem;
}

.add-to-cart-button {
    width: 100%;
    padding: 1rem;
    background-color: var(--primary-color);
    color: white;
    border: none;
    border-radius: 4px;
    font-size: 1.1rem;
    cursor: pointer;
    transition: background-color 0.2s;
    margin-bottom: 2rem;
}

.add-to-cart-button:hover:not(:disabled) {
    background-color: #0056b3;
}

.add-to-cart-button:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
}

.product-description {
    margin-bottom: 2rem;
    line-height: 1.6;
}

.specifications {
    border-top: 1px solid var(--border-color);
    padding-top: 2rem;
}

.spec-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 1rem;
}

.spec-item {
    display: flex;
    justify-content: space-between;
    padding: 0.5rem 0;
    border-bottom: 1px solid #eee;
}

.spec-label {
    color: #666;
}

.spec-value {
    font-weight: 500;
}

.loading-container {
    text-align: center;
    padding: 4rem;
}

.loader {
    display: inline-block;
    width: 50px;
    height: 50px;
    border: 4px solid #f3f3f3;
    border-top: 4px solid var(--primary-color);
    border-radius: 50%;
    animation: spin 1s linear infinite;
}

.error-container {
    text-align: center;
    padding: 4rem;
}

.back-button {
    display: inline-block;
    margin-top: 1rem;
    padding: 0.8rem 1.5rem;
    background-color: var(--primary-color);
    color: white;
    text-decoration: none;
    border-radius: 4px;
}

@keyframes spin {
    0% {
        transform: rotate(0deg);
    }

    100% {
        transform: rotate(360deg);
    }
}

@media (max-width: 768px) {
    .product-container {
        grid-template-columns: 1fr;
    }

    .product-info {
        padding: 0;
    }

    .thumbnail-grid {
        grid-template-columns: repeat(3, 1fr);
    }
}
</style>