<template>
  <div class="cart-container">
    <div class="cart-header">
      <h1>Your Shopping Cart</h1>
      <p v-if="cartStore.cartItems.length > 0">
        Price
      </p>
    </div>

    <div v-if="cartStore.loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>Loading your cart...</p>
    </div>

    <div v-else-if="cartStore.error" class="error-message">
      <p>There was a problem loading your cart. Please try again.</p>
      <button @click="refreshCart" class="refresh-button">Refresh</button>
    </div>

    <div v-else-if="cartStore.cartItems.length === 0" class="empty-cart">
      <div class="empty-cart-content">
        <img src="@/assets/empty-cart.png" alt="Empty cart" class="empty-cart-image">
        <h2>Your Amazon Cart is empty</h2>
        <router-link to="/products" class="shop-button">Shop today's deals</router-link>
      </div>
    </div>

    <div v-else class="cart-content">
      <div class="items-section">
        <div v-for="item in cartStore.cartItems" :key="item.id" class="cart-item">
          <div class="product-image-container">
            <img :src="item.product.imageUrl || '@/assets/product-placeholder.png'" 
            :alt="item.product.name" 
            class="product-image">
          </div>
          
          <div class="product-details">
            <div class="product-info">
              <h3 class="product-title">{{ item.product.name }}</h3>
              <p class="product-stock">In Stock</p>
              <p class="product-shipping">Eligible for FREE Shipping</p>
              
              <div class="quantity-controls">
                <select v-model="item.quantity" @change="updateQuantity(item)" class="quantity-select">
                  <option v-for="n in 10" :value="n" :key="n">Qty: {{ n }}</option>
                  <option value="10+">10+</option>
                </select>
                
                <div class="action-links">
                  <button @click="removeItem(item.product.id)" class="action-link">Delete</button>
                  <button class="action-link">Save for later</button>
                  <button class="action-link">Compare with similar items</button>
                </div>
              </div>
            </div>
            
            <div class="product-price">
              <span class="price-amount">${{ (item.product.price * item.quantity).toFixed(2) }}</span>
              <span class="price-each">${{ item.product.price.toFixed(2) }} each</span>
            </div>
          </div>
        </div>
      </div>

      <div class="summary-section">
        <div class="checkout-card">
          <p class="subtotal">
            <span>Subtotal ({{ cartStore.totalItems }} items):</span>
            <span class="subtotal-amount">${{ cartStore.totalPrice.toFixed(2) }}</span>
          </p>
          <div class="gift-option">
            <input type="checkbox" id="gift" v-model="isGift">
            <label for="gift">This order contains a gift</label>
          </div>
          <button @click="proceedToCheckout" class="checkout-button">
            Proceed to Checkout
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useCartStore } from '@/stores/cartStore';
import { useAuthStore } from '@/stores/authStore';
import { onMounted } from 'vue';
import type { CartItem } from '@/types/cart';

const cartStore = useCartStore();
const authStore = useAuthStore();
const isGift = ref(false);

onMounted(async () => {
  if (authStore.user) {
    await cartStore.fetchCart(authStore.user.id);
  }
});

const refreshCart = async () => {
  if (authStore.user) {
    await cartStore.fetchCart(authStore.user.id);
  }
};

const updateQuantity = async (item: CartItem) => {
  try {
    await cartStore.updateQuantity(item.id, item.quantity);
  } catch (error) {
    console.error('Error updating quantity:', error);
  }
};

const removeItem = async (productId: number) => {
  try {
    await cartStore.removeFromCart(productId);
  } catch (error) {
    console.error('Error removing item:', error);
  }
};

const proceedToCheckout = () => {
  // Implement checkout navigation
};
</script>

<style scoped>
:root {
  --bg-color: #ffffff;
  --text-color: #111111;
  --border-color: #dddddd;
  --primary-color: #0066c0;
  --secondary-color: #FF9900;
  --success-color: #008a00;
  --error-color: #D32F2F;
  --card-bg: #ffffff;
  --input-bg: #F0F2F2;
}

@media (prefers-color-scheme: dark) {
  :root {
    --bg-color: #1a1a1a;
    --text-color: #f0f0f0;
    --border-color: #444444;
    --primary-color: #4dabf7;
    --secondary-color: #ffc078;
    --success-color: #69db7c;
    --error-color: #ff8787;
    --card-bg: #2d2d2d;
    --input-bg: #3d3d3d;
  }
}

.cart-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  background-color: var(--bg-color);
  color: var(--text-color);
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--border-color);
}

.cart-header h1 {
  font-size: 28px;
  font-weight: 400;
  margin: 0;
  color: var(--text-color);
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
}

.loading-spinner {
  border: 4px solid var(--border-color);
  border-top: 4px solid var(--secondary-color);
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  text-align: center;
  padding: 40px;
  color: var(--error-color);
}

.refresh-button {
  margin-top: 15px;
  padding: 8px 16px;
  background-color: var(--secondary-color);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.empty-cart {
  display: flex;
  justify-content: center;
  padding: 40px 0;
}

.empty-cart-content {
  text-align: center;
}

.empty-cart-image {
  width: 200px;
  height: auto;
  margin-bottom: 20px;
  filter: invert(0); /* Se invertirá automáticamente en modo oscuro */
}

@media (prefers-color-scheme: dark) {
  .empty-cart-image {
    filter: invert(0.9);
  }
}

.shop-button {
  display: inline-block;
  margin-top: 15px;
  padding: 8px 16px;
  background-color: var(--secondary-color);
  color: white;
  text-decoration: none;
  border-radius: 4px;
}

.cart-content {
  display: flex;
  gap: 20px;
}

.items-section {
  flex: 3;
  background: var(--card-bg);
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.summary-section {
  flex: 1;
}

.cart-item {
  display: flex;
  gap: 20px;
  padding: 20px 0;
  border-bottom: 1px solid var(--border-color);
}

.product-image-container {
  width: 180px;
  height: 180px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.product-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  background-color: white; /* Fondo blanco para imágenes */
  border-radius: 4px;
}

.product-details {
  flex: 1;
  display: flex;
  justify-content: space-between;
}

.product-info {
  flex: 1;
}

.product-title {
  font-size: 18px;
  margin: 0 0 10px 0;
  color: var(--primary-color);
}

.product-stock {
  color: var(--success-color);
  font-size: 14px;
  margin: 5px 0;
}

.product-shipping {
  font-size: 14px;
  margin: 5px 0;
  color: var(--text-color);
}

.quantity-controls {
  margin-top: 15px;
}

.quantity-select {
  padding: 5px;
  border-radius: 4px;
  border: 1px solid var(--border-color);
  background-color: var(--input-bg);
  color: var(--text-color);
  cursor: pointer;
}

.action-links {
  margin-top: 10px;
  display: flex;
  gap: 15px;
}

.action-link {
  background: none;
  border: none;
  color: var(--primary-color);
  cursor: pointer;
  padding: 0;
  font-size: 12px;
}

.action-link:hover {
  text-decoration: underline;
  color: var(--secondary-color);
}

.product-price {
  width: 120px;
  text-align: right;
}

.price-amount {
  font-size: 18px;
  font-weight: bold;
  display: block;
  color: var(--text-color);
}

.price-each {
  font-size: 12px;
  color: var(--text-color);
  opacity: 0.8;
}

.checkout-card {
  background: var(--card-bg);
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.subtotal {
  display: flex;
  justify-content: space-between;
  font-size: 18px;
  margin-bottom: 15px;
  color: var(--text-color);
}

.subtotal-amount {
  font-weight: bold;
}

.gift-option {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-bottom: 15px;
  font-size: 14px;
  color: var(--text-color);
}

.gift-option input[type="checkbox"] {
  accent-color: var(--secondary-color);
}

.checkout-button {
  width: 100%;
  padding: 10px;
  background-color: var(--secondary-color);
  color: #111;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  transition: background-color 0.2s;
}

.checkout-button:hover {
  background-color: #e6b800;
}
</style>