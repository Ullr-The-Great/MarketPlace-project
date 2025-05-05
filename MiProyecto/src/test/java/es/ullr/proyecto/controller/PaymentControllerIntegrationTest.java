package es.ullr.proyecto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.ullr.proyecto.model.Category;
import es.ullr.proyecto.model.Product;
import es.ullr.proyecto.model.User;
import es.ullr.proyecto.repository.CategoryRepository;
import es.ullr.proyecto.repository.ProductRepository;
import es.ullr.proyecto.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
public class PaymentControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    private Long testProductId;

    @BeforeEach
    void setUp() {
        // Configurar MockMvc con el contexto de la aplicación
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Verificar si la categoría ya existe
        Category category = categoryRepository.findByName("Test Category")
                .orElseGet(() -> {
                    Category newCategory = new Category();
                    newCategory.setName("Test Category");
                    return categoryRepository.save(newCategory); // Guardar la categoría en la base de datos
                });

        // Crear un usuario de prueba
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("testuser@example.com");
        user.setRole("User");
        userRepository.save(user);

        // Configurar el contexto de seguridad con el usuario autenticado
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Crear un producto de prueba
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(50.0);
        product.setStock(10);
        product.setCategory(category); // Asignar la categoría al producto
        Product savedProduct = productRepository.save(product);

        // Almacenar el ID del producto creado
        testProductId = savedProduct.getId();
    }

    @Test
    void testCreatePaymentIntent_Success() throws Exception {
        Map<String, Object> requestData = Map.of(
            "amount", 10000L,
            "items", List.of(
                Map.of("productId", testProductId, "quantity", 2) 
            )
        );

        mockMvc.perform(post("/api/payments/create-payment-intent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientSecret").exists());
    }

    @Test
    void testCreatePaymentIntent_InsufficientStock() throws Exception {
        // Datos de entrada para la solicitud
        Map<String, Object> requestData = Map.of(
            "amount", 10000L, // 100 euros en centavos
            "items", List.of(
                Map.of("productId", testProductId, "quantity", 20) // Usar el ID del producto creado
            )
        );

        // Realizar la solicitud POST
        mockMvc.perform(post("/api/payments/create-payment-intent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Stock insuficiente para el producto: Test Product"));
    }
}