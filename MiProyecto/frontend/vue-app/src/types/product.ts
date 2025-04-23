import type { Category } from "./category.ts"

export interface Product {
    id: number;
    name: string;
    price: number;
    originalPrice?: number; 
    description: string;
    imageUrl?: string;
    category: Category;
    stock: number;
    createdAt?: string;
  }

  export interface ProductWithGallery extends Product {
    images?: string[]; // URLs de imágenes adicionales
  }