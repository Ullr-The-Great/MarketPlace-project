package es.ullr.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.ullr.proyecto.model.Order;
import es.ullr.proyecto.model.OrderItem;
import es.ullr.proyecto.model.Product;
import es.ullr.proyecto.model.User;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> 
{
    List<OrderItem> findByOrder(Order order); // Buscar ítems de pedido por pedido

	boolean existsByOrderUserAndProduct(User user, Product product);
}
