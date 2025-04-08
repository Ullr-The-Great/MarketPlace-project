<template>
  <div class="cart-view">
    <h2>Your Cart</h2>

    <div v-if="cartStore.loading">Loading cart...</div>
    <div v-else-if="cartStore.error" class="error">
      <p>Error: {{ cartStore.error }}</p>
    </div>
    <div v-else-if="cartStore.cartItems.length === 0">
      <p>Your cart is empty</p>
    </div>
    <div v-else>
      <div class="cart-items">
        <div v-for="item in cartStore.cartItems" :key="item.id" class="cart-item">
          <div class="product-info">
            <h3>{{ item.product.name }}</h3>
            <p>Cantidad: {{ item.quantity }}</p>
            <p>${{ item.product.price.toFixed(2) }} x {{ item.quantity }}</p>
          </div>
          <div class="item-actions">
            <button @click="decreaseQuantity(item)" :disabled="item.quantity <= 1">
              -
            </button>
            <input type="number" v-model.number="item.quantity" min="1" @change="updateItemQuantity(item)">
            <button @click="increaseQuantity(item)">
              +
            </button>
            <button @click="removeItem(item.product.id)" class="remove-button">
              Eliminar
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

const updateQuantity = async (item: CartItem) => {
  try {
    await cartStore.updateQuantity(item.id, item.quantity);
  } catch (error) {
    console.error('Error updating quantity:', error);
  }
};

const increaseQuantity = (item: CartItem) => {
  item.quantity++;
  updateQuantity(item);
};

const decreaseQuantity = (item: CartItem) => {
  if (item.quantity > 1) {
    item.quantity--;
    updateQuantity(item);
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
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.cart-items {
  margin-bottom: 30px;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
  margin-bottom: 10px;
}

.product-info {
  flex: 1;
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

input[type="number"] {
  width: 50px;
  text-align: center;
}

button {
  padding: 5px 10px;
  cursor: pointer;
}

.remove-button {
  background-color: #ff4444;
  color: white;
  border: none;
  border-radius: 4px;
}

.remove-button:hover {
  background-color: #cc0000;
}

.cart-summary {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.checkout-button {
  padding: 12px 24px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1.1rem;
  margin-top: 15px;
  cursor: pointer;
}

.checkout-button:hover {
  background-color: #3aa876;
}

.back-button {
  display: inline-block;
  margin-top: 15px;
  padding: 8px 16px;
  background-color: #f0f0f0;
  color: #333;
  text-decoration: none;
  border-radius: 4px;
}

.error {
  color: red;
}
</style>