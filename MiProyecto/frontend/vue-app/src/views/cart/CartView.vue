<template>
    <div class="cart-view">
      <h2>Your Cart</h2>
      
      <div v-if="cartStore.loading">Loading cart...</div>
      <div v-else-if="cartStore.error" class="error">
        Error: {{ cartStore.error }}
      </div>
      <div v-else-if="cartStore.cartItems.length === 0">
        Your cart is empty
      </div>
      <div v-else>
        <div class="cart-items">
          <div v-for="item in cartStore.cartItems" :key="item.id" class="cart-item">
            <div class="product-info">
              <h3>{{ item.product.name }}</h3>
              <p>${{ item.product.price.toFixed(2) }} x {{ item.quantity }}</p>
            </div>
            <div class="item-controls">
              <input 
                type="number" 
                v-model.number="item.quantity" 
                min="1" 
                :max="item.product.stock"
                @change="updateItemQuantity(item)"
              >
              <button @click="removeItem(item.product.id)">
                Remove
              </button>
            </div>
          </div>
        </div>
        
        <div class="cart-summary">
          <p>Total Items: {{ cartStore.totalItems }}</p>
          <p>Total Price: ${{ cartStore.totalPrice.toFixed(2) }}</p>
          <button @click="proceedToCheckout">
            Proceed to Checkout
          </button>
          <button @click="clearCart" class="secondary">
            Clear Cart
          </button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { useCartStore } from '@/stores/cartStore';
  import { useAuthStore } from '@/stores/authStore';
  import { onMounted } from 'vue';
import type { CartItem } from '@/types/cart';
  
  const cartStore = useCartStore();
  const authStore = useAuthStore();
  
  onMounted(async () => {
    if (authStore.user) {
      await cartStore.fetchCart(authStore.user.id);
    }
  });
  
  const updateItemQuantity = async (item: CartItem) => {
    try {
      await cartStore.updateQuantity(item.id, item.quantity);
    } catch (error) {
      // Revertir el cambio si falla
      item.quantity = item.quantity;
      // Mostrar error
    }
  };
  
  const removeItem = async (productId: number) => {
    try {
      await cartStore.removeFromCart(productId);
    } catch (error) {
      // Mostrar error
    }
  };
  
  const clearCart = async () => {
    try {
      await cartStore.clearCart();
    } catch (error) {
      // Mostrar error
    }
  };
  
  const proceedToCheckout = () => {
    // Navegar a checkout
  };
  </script>
  
  <style scoped>
  .cart-view {
    padding: 20px;
    max-width: 800px;
    margin: 0 auto;
  }
  .cart-items {
    margin-bottom: 20px;
  }
  .cart-item {
    display: flex;
    justify-content: space-between;
    padding: 15px;
    border-bottom: 1px solid #eee;
  }
  .item-controls {
    display: flex;
    gap: 10px;
  }
  input[type="number"] {
    width: 60px;
  }
  .cart-summary {
    padding: 20px;
    border-top: 2px solid #333;
    text-align: right;
  }
  button.secondary {
    background-color: #f0f0f0;
    color: #333;
  }
  </style>