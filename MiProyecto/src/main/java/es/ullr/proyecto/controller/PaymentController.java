package es.ullr.proyecto.controller;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import es.ullr.proyecto.model.Order;
import es.ullr.proyecto.repository.OrderRepository;
import es.ullr.proyecto.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
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

    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestBody Map<String, Object> data) {
        Stripe.apiKey = stripeSecretKey;

        try {
            long amount = ((Number) data.get("amount")).longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amount)
                    .setCurrency("eur")
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Guarda los detalles del pedido en la base de datos
            Order order = new Order();
            order.setUser(userService.getCurrentUser()); // Obtén el usuario autenticado
            order.setTotalAmount(amount / 100.0); // Convierte de centavos a euros
            order.setStatus("PAGADO");
            order.setCreatedAt(LocalDateTime.now());
            order.setPaymentIntentId(paymentIntent.getId()); // Guarda el ID del PaymentIntent
            orderRepository.save(order);

            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}