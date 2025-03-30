import { defineStore } from 'pinia';
import api from '@/services/api';
import type { Product } from '@/types/product.ts';
import type { Category } from '@/types/category.ts';


export const useProductStore = defineStore('product', {
    state: () => ({
        product: [] as Product[],
        categories: [] as Category[],
        loading: false,
        error: null as string | null,
        filters: {
            categoryId: null as number | null,
            searchQuery: ""
        }
    }),
    getters: {
        filteredProducts(state){
            let filtered = [...state.product];
      
            if (state.filters.categoryId) {
              filtered = filtered.filter(
                product => product.category.id === state.filters.categoryId
              );
            }
            
            if (state.filters.searchQuery) {
              filtered = filtered.filter(product =>
                product.name.toLowerCase().includes(state.filters.searchQuery.toLowerCase())
              );
            }
            
            return filtered;
          
        }
    },
    actions: {
        async fetchProducts(){
            this.loading = true;
            try{
                const response = await api.get('/products');
                this.product = response.data;
            }catch (error) {
                this.error = error instanceof Error ? error.message : 'unknown error';
            }finally {
                this.loading = false;
            }
        },
        async fetchProductsByCategory(categoryId: number) {
            this.loading = true;
            try {
              const response = await api.get(`/products/category/${categoryId}`);
              this.product = response.data;
            } catch (error) {
              this.error = error instanceof Error ? error.message : 'Unknown error';
            } finally {
              this.loading = false;
            }
          },
          async fetchCategories() {
            try {
              // Asumiendo que tienes un endpoint /categories
              const response = await api.get('/categories');
              this.categories = response.data;
            } catch (error) {
              console.error('Error fetching categories:', error);
            }
          },
          async searchProducts(query: string) {
            this.loading = true;
            try {
              const response = await api.get(`/products/search?name=${query}`);
              this.product = response.data;
            } catch (error) {
              this.error = error instanceof Error ? error.message : 'Unknown error';
            } finally {
              this.loading = false;
            }
          },
          updateFilters(newFilters: Partial<typeof this.filters>) {
            this.filters = { ...this.filters, ...newFilters };
          }
        }

})

