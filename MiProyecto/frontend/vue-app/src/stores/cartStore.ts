import { defineStore } from 'pinia';
import api from '@/services/api';
import type { Cart, CartItem } from '@/types/cart';
import type { Product } from '@/types/product';
import { useAuthStore } from './authStore';




export const useCartStore = defineStore('cart', {
  state: () => ({
    currentCart: null as Cart | null,
    loading: false,
    error: null as string | null
  }),
  getters: {
    cartItems():CartItem[]{
      return this.currentCart?.cartItems || [];
    },
    totalItems(): number {
      if (!this.currentCart?.cartItems) return 0;
      const total = this.cartItems.reduce((sum, item) => {
        const qty = Number(item.quantity) || 0;
        return sum + qty;
      }, 0);
      return isNaN(total) ? 0 : total;
    },
    totalPrice(): number {
      if (!this.currentCart?.cartItems) return 0;
      return this.cartItems.reduce((total, item) => {
        return total + (item.product?.price || 0) * (item.quantity || 0);
      }, 0);
    },
  },
  actions: {
    async fetchCart(cartId: number) {
      this.loading = true;
      try {
        const response = await api.get(`/carts/${cartId}/items`);
        this.currentCart = response.data;
        console.log(response.data)
        // No necesitas calcular totalItems, ya viene del backend
      } catch (error) {
        // Manejo de errores
      } finally {
        this.loading = false;
      }
    },
    async addToCart(product: Product, quantity: number = 1) {

      if (!product || !product?.id) {
        throw new Error('Producto no válido');
    }

      if (!this.currentCart) {
          throw new Error('No cart initialized');
      }
      
      this.loading = true;
      try {
          const response = await api.post(
              `/carts/${this.currentCart.id}/items`,
              { productId: product.id, quantity }
          );
                    
        const existingItem = this.currentCart.cartItems?.find(
          item => item.product?.id === product.id
        );
        
        if (existingItem) {
          existingItem.quantity += quantity;
        } else {
          this.currentCart.cartItems = [
            ...(this.currentCart.cartItems || []),
            response.data
          ];
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
        console.log("Hola desde antes de borrar")
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
        const userCart = await this.findUserCart(userId);      

        if (userCart) {
          // 2. Si existe, cargar sus items
          await this.fetchCart(userCart.id);
        } else {
          // 3. Si no existe, crear uno nuevo
          const response = await api.post('/carts', { userId });
          this.currentCart = response.data;
        }
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Unknown error';
        throw error;
      }
    },

    async findUserCart(userId: number) {
      try {
        const response = await api.get(`/carts/user/${userId}`);
        return response.data;
      } catch (error:any) {
        if (error.response?.status === 404) {
          return null; // No existe carrito
        }
        throw error;
      }
    }
  
  },
  persist: true
});