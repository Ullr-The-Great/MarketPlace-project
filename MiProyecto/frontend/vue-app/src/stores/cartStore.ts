import { defineStore } from 'pinia';
import api from '@/services/api';
import type { Cart, CartItem } from '@/types/cart';
import type { Product } from '@/types/product';
import { useAuthStore } from './authStore';

export const useCartStore = defineStore('cart', {
  state: () => ({
    cartItems: [] as CartItem[],
    currentCart: null as Cart | null,
    loading: false,
    error: null as string | null
  }),
  getters: {
    totalItems: (state) => state.cartItems.reduce(
      (sum, item) => sum + item.quantity, 0),
    totalPrice: (state) => state.cartItems.reduce(
      (sum, item) => sum + (item.product.price * item.quantity), 0),
  },
  actions: {
    async fetchCart(userId: number) {
      this.loading = true;
      try {
        const response = await api.get(`/carts/user/${userId}`);
        this.currentCart = response.data;
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Unknown error';
      } finally {
        this.loading = false;
      }
    },
    async addToCart(product: Product, quantity: number = 1) {
      if (!this.currentCart) {
          throw new Error('No cart initialized');
      }
      
      this.loading = true;
      try {
          const response = await api.post(
              `/carts/${this.currentCart.id}/items`,
              { productId: product.id, quantity }
          );
          
          // Actualizar estado local
          const existingItem = this.cartItems.find(
              item => item.product.id === product.id
          );
          
          if (existingItem) {
              existingItem.quantity += quantity;
          } else {
              this.cartItems.push(response.data);
          }
          return response.data;
      } catch (error) {
          console.error('Error adding to cart:', error);
          throw error;
      } finally {
          this.loading = false;
      }
  },
    async removeFromCart(productId: number) {
      if (!this.currentCart) {
        throw new Error('No cart initialized');
      }
      
      this.loading = true;
      try {
        await api.delete(
          `/carts/${this.currentCart.id}/items/${productId}`
        );
        
        // Actualizar el carrito local
        this.currentCart.cartItems = this.currentCart.cartItems.filter(
          item => item.product.id !== productId
        );
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Unknown error';
        throw error;
      } finally {
        this.loading = false;
      }
    },
    async updateQuantity(itemId: number, newQuantity: number) {
      this.loading = true;
      try {
        await api.put(
          `/carts/items/${itemId}`,
          { quantity: newQuantity }
        );
        
        // Actualizar el carrito local
        const item = this.currentCart?.cartItems.find(i => i.id === itemId);
        if (item) {
          item.quantity = newQuantity;
        }
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Unknown error';
        throw error;
      } finally {
        this.loading = false;
      }
    },
    async clearCart() {
      if (!this.currentCart) return;
      
      this.loading = true;
      try {
        await api.delete(`/carts/${this.currentCart.id}/items`);
        this.currentCart.cartItems = [];
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Unknown error';
        throw error;
      } finally {
        this.loading = false;
      }
    },
    async initializeCart(userId: number) {
      try {
        // Primero intentamos obtener el carrito existente
        await this.fetchCart(userId);
        
        // Si no existe, creamos uno nuevo
        if (!this.currentCart) {
          const response = await api.post('/carts', { userId });
          this.currentCart = response.data;
        }
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Unknown error';
        throw error;
      }
    }
  },
  persist: true
});