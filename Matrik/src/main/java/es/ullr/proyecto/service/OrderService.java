package es.ullr.proyecto.service;

import es.ullr.proyecto.model.*;
import es.ullr.proyecto.repository.OrderItemRepository;
import es.ullr.proyecto.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService 
{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartService cartService;

    public Order createOrder(User user) 
    {
        Cart cart = cartService.findCartByUser(user).orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        List<CartItem> cartItems = cartService.getCartItems(cart);

        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(calculateTotalAmount(cartItems));
        order.setStatus("PENDIENTE");
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItemRepository.save(orderItem);
        }

        cartService.getCartItems(cart).forEach(cartItem -> cartService.removeProductFromCart(cart, cartItem.getProduct()));

        return savedOrder;
    }

    private Double calculateTotalAmount(List<CartItem> cartItems) 
    {
        return cartItems.stream()
                .mapToDouble(cartItem -> cartItem.getProduct().getPrice() * cartItem.getQuantity())
                .sum();
    }

    public List<Order> findOrdersByUser(User user) 
    {
        return orderRepository.findByUser(user);
    }
    
    public boolean hasUserPurchasedProduct(User user, Product product) {
        // Verificar si existe un OrderItem asociado al producto y al usuario
        return orderItemRepository.existsByOrderUserAndProduct(user, product);
    }
    
}