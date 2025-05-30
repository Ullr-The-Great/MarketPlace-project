<template>
  <div class="cart-container">


    <div v-if="!authStore.isAuthenticated" class="auth-prompt">
      <h2>Your Shopping Cart</h2>
      <p>To view your cart and checkout, please:</p>
      <div class="auth-buttons">
        <router-link to="/login" class="auth-button primary">Sign In</router-link>
        <span>or</span>
        <router-link to="/register" class="auth-button secondary">Create an Account</router-link>
      </div>
    </div>

    <div v-else>
      


      <div v-if="cartStore.loading" class="loading-container">
        <!-- Muestra varios skeletons durante la carga -->
        <CartItemSkeleton v-for="n in skeletonCount" :key="'skeleton-' + n" />
      </div>

      <div v-else-if="cartStore.error" class="error-message">
        <p>There was a problem loading your cart. Please try again.</p>
        <button @click="refreshCart" class="refresh-button">Refresh</button>
      </div>

      <div v-else-if="cartStore.cartItems.length === 0" class="empty-cart">
        <div class="empty-cart-content">
          <img src="@/assets/empty-cart.png" alt="Empty cart" class="empty-cart-image">
          <h2>Your Cart is empty</h2>
          <router-link to="/products" class="shop-button">Shop today's deals</router-link>
        </div>
      </div>

      <div v-else class="cart-content">

        <div class="cart-header">
        <h1>Your Shopping Cart</h1>
      </div>
      <div class="cart-subheader">
        <div class="items-section">
          <transition-group name="fade">
            <div v-for="item in cartStore.cartItems" :key="item.id" class="cart-item">
              {{ console.log('Cart item:', item) }}
              <div class="product-image-container">
                <img :src="getMainImage(item.product)" :alt="item.product.name" class="product-image">
              </div>

              <div class="product-details">
                <div class="product-header">
                  <h3 class="product-title">{{ item.product.name }}</h3>
                  <div class="product-price">
                    <span class="price-amount">{{ (item.product.price * item.quantity).toFixed(2) }} €</span>
                    <span class="price-each">{{ item.product.price.toFixed(2) }} € each</span>
                  </div>
                </div>

                <p class="product-stock">In Stock</p>

                <div class="quantity-controls">
                  <div class="quantity-adjuster">
                    <button @click="decreaseQuantity(item)" class="quantity-button"
                      :disabled="item.quantity <= 1">-</button>
                    <span class="quantity-display">{{ item.quantity }}</span>
                    <button @click="increaseQuantity(item)" class="quantity-button">+</button>
                  </div>
                </div>

                <div class="action-links">
                  <button @click="removeItem(item.id)" class="action-link">Delete</button>
                </div>
              </div>
            </div>
          </transition-group>
        </div>

        <div class="summary-section">
          <div class="checkout-card">
            <p class="subtotal">
              <span>Subtotal ({{ cartStore.totalItems }} items):</span>
              <span class="subtotal-amount">{{ cartStore.totalPrice.toFixed(2) }} €</span>
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
    </div>


  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useCartStore } from '@/stores/cartStore';
import { useAuthStore } from '@/stores/authStore';
import { onMounted } from 'vue';
import type { CartItem } from '@/types/cart';
import CartItemSkeleton from '@/components/CartItemSkeleton.vue';
import { Product } from '@/types/product';
import placeholderImage from '@/assets/product-placeholder.png';
import { useRouter } from 'vue-router';




const cartStore = useCartStore();
const authStore = useAuthStore();
const router = useRouter();

const isGift = ref(false);

const globalLoading = ref(false);
const skeletonCount = ref(3)

const getMainImage = (product: Product): string => {
  console.log('product:', product);

  // Verifica si el campo `images` existe y mapea las URLs
  const imageUrls = (product as any).images?.map((image: { imageUrl: string }) => image.imageUrl) || product.imageUrls || [];

  if (imageUrls.length === 0) {
    console.warn('No images found for product:', product);
    return placeholderImage;
  }

  return imageUrls[0]; // Devuelve la primera URL
};

const props = defineProps<{
  product: Product;
}>();


onMounted(async () => {

});

const refreshCart = async () => {
  if (authStore.user) {
    await cartStore.fetchCart();
  }
};

const increaseQuantity = async (item: CartItem) => {
  globalLoading.value = true;
  try {
    const newQuantity = item.quantity + 1;
    
     await cartStore.updateQuantity(item.id, newQuantity);

     item.quantity = newQuantity;
  } catch (error) {
    console.error('Error increasing quantity:', error);
  } finally {
    globalLoading.value = false;
  }
};

const decreaseQuantity = async (item: CartItem) => {
  globalLoading.value = true;
  try {
    const newQuantity = item.quantity - 1;
    if (newQuantity <= 0) {
      await removeItem(item.id);
      return;
    }
    await cartStore.updateQuantity(item.id, newQuantity);
    item.quantity = newQuantity;
  } catch (error) {
    console.error('Error decreasing quantity:', error);
  } finally {
    globalLoading.value = false;
  }
};

const removeItem = async (itemId: number) => {
  globalLoading.value = true;
  try {
    await cartStore.removeFromCart(itemId);
  } catch (error) {
    console.error('Error removing item:', error);
  } finally {
    globalLoading.value = false;
  }
};

const proceedToCheckout = () => {
  router.push('/checkout'); // Navegar a la página de checkout
};

</script>

<style scoped>
@import "../../styles/cart.css"
</style>