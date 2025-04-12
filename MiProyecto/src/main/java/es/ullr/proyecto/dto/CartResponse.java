package es.ullr.proyecto.dto;

import java.util.List;
import java.util.stream.Collectors;

import es.ullr.proyecto.model.Cart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Long id;
    private Long userId;
    private String createdAt;
    private List<CartItemDto> items;
    
    // Constructor que acepta un Cart
    public CartResponse(Cart cart) {
        this.id = cart.getId();
        this.userId = cart.getUser().getId();
        this.createdAt = cart.getCreatedAt().toString();
        this.items = cart.getCartItems().stream()
                .map(CartItemDto::new)
                .collect(Collectors.toList());
    }
    
    // Getters
}
