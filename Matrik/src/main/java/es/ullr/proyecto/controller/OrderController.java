package es.ullr.proyecto.controller;

import es.ullr.proyecto.model.Order;
import es.ullr.proyecto.model.Product;
import es.ullr.proyecto.model.User;
import es.ullr.proyecto.service.OrderService;
import es.ullr.proyecto.service.ProductService;
import es.ullr.proyecto.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductService productService;

    // Crear un pedido para un usuario
    @PostMapping("/user/{userId}")
    public ResponseEntity<Order> createOrder(@PathVariable Long userId) 
    {
        User user = userService.findByUsername(userId.toString()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Order order = orderService.createOrder(user);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    // Obtener los pedidos de un usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable Long userId) 
    {
        User user = userService.findByUsername(userId.toString()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<Order> orders = orderService.findOrdersByUser(user);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
    
    @GetMapping("/has-purchased/{productId}")
    public ResponseEntity<Map<String, Boolean>> hasPurchased(
        @PathVariable Long productId,
        Authentication authentication
    ) {
        User user = userService.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Product product = productService.findProductById(productId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        boolean hasPurchased = orderService.hasUserPurchasedProduct(user, product);
        return ResponseEntity.ok(Collections.singletonMap("hasPurchased", hasPurchased));
    }
}
