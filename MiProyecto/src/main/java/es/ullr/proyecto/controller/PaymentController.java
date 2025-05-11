package es.ullr.proyecto.controller;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import es.ullr.proyecto.model.Order;
import es.ullr.proyecto.model.OrderItem;
import es.ullr.proyecto.model.Product;
import es.ullr.proyecto.model.User;
import es.ullr.proyecto.repository.OrderRepository;
import es.ullr.proyecto.service.ProductService;
import es.ullr.proyecto.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;
    
    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestBody Map<String, Object> data) {
        Stripe.apiKey = stripeSecretKey;

        try {
            // Validar y extraer los datos del cuerpo de la solicitud
            if (!data.containsKey("amount") || !data.containsKey("items")) {
                throw new IllegalArgumentException("Faltan datos requeridos: 'amount' o 'items'");
            }

            long amount = ((Number) data.get("amount")).longValue();
            List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");

            if (amount <= 0 || items.isEmpty()) {
                throw new IllegalArgumentException("El monto debe ser mayor a 0 y los ítems no pueden estar vacíos");
            }

            // Crear el PaymentIntent con Stripe
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amount)
                    .setCurrency("eur")
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Guardar los detalles del pedido en la base de datos
            User user = userService.getCurrentUser(); // Obtén el usuario autenticado
            Order order = new Order();
            order.setUser(user);
            order.setTotalAmount(amount / 100.0); // Convierte de centavos a euros
            order.setStatus("PAGADO");
            order.setCreatedAt(LocalDateTime.now());
            order.setPaymentIntentId(paymentIntent.getId());

            // Crear los OrderItems y reducir el stock de los productos
            List<OrderItem> orderItems = new ArrayList<>();
            for (Map<String, Object> item : items) {
                Long productId = ((Number) item.get("productId")).longValue();
                Integer quantity = ((Number) item.get("quantity")).intValue();

                Product product = productService.findProductById(productId)
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

                if (product.getStock() < quantity) {
                        System.out.println("Stock insuficiente para el producto: " + product.getName());

                    throw new RuntimeException("Stock insuficiente para el producto: " + product.getName());
                }

                // Reducir el stock
                product.setStock(product.getStock() - quantity);
                productService.saveProduct(product);

                // Crear el OrderItem
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(quantity);
                orderItem.setPrice(product.getPrice() * quantity);
                orderItems.add(orderItem);
            }

            order.setOrderItems(orderItems);
            orderRepository.save(order);

            // Responder con el clientSecret
            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno del servidor"));
        }
    }
}