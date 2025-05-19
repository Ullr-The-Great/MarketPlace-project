package es.ullr.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.ullr.proyecto.model.Cart;
import es.ullr.proyecto.model.User;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long>
{
    Optional<Cart> findByUser(User user); // Buscar carrito por usuario
}
