import type { Product } from "@/types/product"

export interface CartItem {
    id: number;
    product: Product;
    quantity: number;
  }
  
export interface Cart {
    id: number;
    userId: number;
    createdAt: string;
    cartItems: CartItem[];
}