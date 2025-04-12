import { defineStore } from 'pinia';
import api from '@/services/api';
import type { Cart, CartItem } from '@/types/cart';
import type { Product } from '@/types/product';
import { useAuthStore } from './authStore';
import { CartResponse } from '@/types/cartResponse';




export const useCartStore = defineStore('cart', {
  state: () => ({
    currentCart: null as CartResponse | null,
    loading: false,
    error: null as string | null
  }),
  getters: {
    cartItems():CartItem[]{
      return this.currentCart?.items || [];
    },
    totalItems(): number {
      if (!this.currentCart?.items) return 0;
      return this.currentCart.items.reduce((total, item) => {
        return total + (item.quantity || 0);
      }, 0);
    },
    totalPrice(): number {
      if (!this.currentCart?.items) return 0;
      return this.currentCart.items.reduce((total, item) => {
        const price = item.product?.price || 0;
        const quantity = item.quantity || 0;
        return total + (price * quantity);
      }, 0);
    },
  },
  actions: {
    async fetchCart(cartId: number) {
      this.loading = true;
      try {
        const response = await api.get<CartResponse>(`/carts/${cartId}/items`);
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
                    
        const existingItem = this.currentCart.items?.find(
          item => item.product?.id === product.id
        );
        
        if (existingItem) {
          existingItem.quantity += quantity;
        } else {
          this.currentCart.items = [
            ...(this.currentCart.items || []),
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
        this.currentCart.items = this.currentCart.items.filter(
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
        const item = this.currentCart?.items.find(i => i.id === itemId);
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
        this.currentCart.items = [];
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