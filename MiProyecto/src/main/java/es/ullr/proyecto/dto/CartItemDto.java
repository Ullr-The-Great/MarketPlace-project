package es.ullr.proyecto.dto;

import es.ullr.proyecto.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Long id;
    private ProductDto product;
    private int quantity;
    
    public CartItemDto(CartItem item) {
        this.id = item.getId();
        this.product = new ProductDto(item.getProduct());
        this.quantity = item.getQuantity();
    }
    
    // Getters
}