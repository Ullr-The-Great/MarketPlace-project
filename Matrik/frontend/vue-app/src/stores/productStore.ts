import { defineStore } from 'pinia'
import api from '@/services/api'
import type { Product } from '@/types/product.ts'
import type { Category } from '@/types/category.ts'
import { useAuthStore } from './authStore'

export const useProductStore = defineStore('product', {
  state: () => ({
    products: [] as Product[], // Asegúrate de que sea un array vacío
    categories: [] as Category[],
    currentProduct: null as Product | null,
    loading: false,
    error: null as string | null,
    filters: {
      categoryId: null as number | null,
      searchQuery: '',
    },
    totalPages: 0, // Added totalPages to the state
    currentPage: 0, // Added currentPage to the state
  }),
  getters: {
    filteredProducts(state) {
      let filtered = [...state.products]; // Cambiado de state.product a state.products
    
      if (state.filters.categoryId) {
        filtered = filtered.filter((product) => product.category.id === state.filters.categoryId);
      }
    
      if (state.filters.searchQuery) {
        filtered = filtered.filter((product) =>
          product.name.toLowerCase().includes(state.filters.searchQuery.toLowerCase()),
        );
      }
    
      console.log('Filtered products:', filtered);
      return filtered;
    }
  },
  actions: {
    async fetchProducts() {
      this.loading = true
      try {
        const response = await api.get('/products')
        console.log('Fetched products:', response.data)

        this.products = response.data
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'unknown error'
      } finally {
        this.loading = false
      }
    },
    async fetchProductsByCategory(categoryId: number | null, page = 0, size = 5) {
      this.loading = true;
      try {
        if (categoryId === null) {
          // Si el categoryId es null, obtener todos los productos paginados
          await this.fetchPaginatedProducts(page, size);
        } else {
          // Si el categoryId no es null, obtener productos por categoría paginados
          const response = await api.get(`/products/category/${categoryId}`, {
            params: { page, size },
          });
          const data = response.data;
    
          this.products = data.content || []; // Asegúrate de que sea un array
          this.totalPages = data.totalPages || 0; // Total de páginas
          this.currentPage = data.number || 0; // Página actual
        }
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Unknown error';
      } finally {
        this.loading = false;
      }
    },
    async fetchProductById(id: number) {
      this.loading = true
      try {
        const response = await api.get<Product>(`/products/${id}`)
        this.currentProduct = response.data
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Failed to fetch product'
      } finally {
        this.loading = false
      }
    },
    async fetchCategories() {
      try {
        // Asumiendo que tienes un endpoint /categories
        const response = await api.get('/categories')
        console.log("categorias")
        this.categories = response.data
      } catch (error) {
        console.error('Error fetching categories:', error)
      }
    },
    async searchProducts(query: string, categoryId: number | null = null, page = 0, size = 5) {
      this.loading = true;
      try {
        const response = await api.get('/products/search', {
          params: {
            name: query,
            categoryId: categoryId || undefined,
            page,
            size,
          },
        });
        const data = response.data;
    
        this.products = data.content || [];
        this.totalPages = data.totalPages || 0;
        this.currentPage = data.number || 0;
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Failed to search products';
      } finally {
        this.loading = false;
      }
    },
    updateFilters(newFilters: Partial<typeof this.filters>) {
      this.filters = { ...this.filters, ...newFilters }
    },
    async fetchPaginatedProducts(page = 0, size = 5) {
      this.loading = true;
      try {
        const response = await api.get(`/products/paginated`, {
          params: { page, size },
        });
        const data = response.data;
    
        this.products = data.content || []; // Asegúrate de que sea un array
        this.totalPages = data.totalPages || 0;
        this.currentPage = data.number || 0;
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Failed to fetch products';
      } finally {
        this.loading = false;
      }
    }
  },
})
