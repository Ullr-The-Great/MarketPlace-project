package es.ullr.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.ullr.proyecto.model.Cart;
import es.ullr.proyecto.model.CartItem;
import es.ullr.proyecto.model.Product;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> 
{
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product); // Buscar ítem del carrito por carrito y producto
    
    List<CartItem> findByCart(Cart cart); //Buscar item por carrito
    
}