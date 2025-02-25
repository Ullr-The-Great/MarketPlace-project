package es.ullr.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.ullr.proyecto.model.Order;
import es.ullr.proyecto.model.OrderItem;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> 
{
    List<OrderItem> findByOrder(Order order); // Buscar ítems de pedido por pedido
}
