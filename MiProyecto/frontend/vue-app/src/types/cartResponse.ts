import { CartItem } from "./cart";

export interface CartResponse {
    id: number;
    userId: number;
    createdAt: string;
    items: CartItem[]; // Cambiado de cartItems a items para coincidir con el DTO
  }