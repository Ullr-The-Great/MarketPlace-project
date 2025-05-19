package es.ullr.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.ullr.proyecto.model.Order;
import es.ullr.proyecto.model.Product;
import es.ullr.proyecto.model.User;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> 
{
    List<Order> findByUser(User user); // Buscar pedidos por usuario
    
    boolean existsByUserAndOrderItemsProduct(User user, Product product);
}