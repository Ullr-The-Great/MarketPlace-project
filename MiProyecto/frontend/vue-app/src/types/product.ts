import type { Category } from "./category.ts"

export interface Product {
    id: number;
    name: string;
    price: number;
    description: string;
    category:{
        id: number;
        name: string;
    }
    stock: number;
    createdAt: string;
  }
