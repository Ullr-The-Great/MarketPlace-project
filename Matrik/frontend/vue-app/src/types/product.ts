import type { Category } from "./category.ts"

export interface Product {
    id: number;
    name: string;
    price: number;
    originalPrice?: number;
    description: string;
    imageUrls?: string[]; // Cambiar de `imageUrl` a `imageUrls` para manejar múltiples imágenes
    category: Category;
    stock: number;
    createdAt?: string;
  }
