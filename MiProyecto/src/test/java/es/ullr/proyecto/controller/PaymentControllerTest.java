package es.ullr.proyecto.controller;

import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import es.ullr.proyecto.model.CartItem;
import es.ullr.proyecto.model.Order;
import es.ullr.proyecto.model.Product;
import es.ullr.proyecto.model.User;
import es.ullr.proyecto.repository.CartItemRepository;
import es.ullr.proyecto.repository.CartRepository;
import es.ullr.proyecto.repository.OrderRepository;
import es.ullr.proyecto.repository.UserRepository;
import es.ullr.proyecto.service.CartService;
import es.ullr.proyecto.service.ProductService;
import es.ullr.proyecto.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class PaymentControllerTest {

	@InjectMocks
    private PaymentController paymentController;

	@Mock
    private UserService userService;
	
	@Mock
	private UserRepository userRepository;

    @Mock
    private ProductService productService;

    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private CartItemRepository cartItemRepository;
    
    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Simular un usuario autenticado en el contexto de seguridad
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(mockUser.getUsername());
        when(authentication.isAuthenticated()).thenReturn(true);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        
        when(userService.createUser(any(User.class))).thenCallRealMethod();

        SecurityContextHolder.setContext(securityContext);        
    }

   

    @Test
    void testCreatePaymentIntent_Success() throws Exception {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("amount", 10000L); // 100 euros en centavos
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("productId", 1L);
        item.put("quantity", 2);
        items.add(item);
        requestData.put("items", items);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");

        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("Test Product");
        mockProduct.setPrice(50.0);
        mockProduct.setStock(10);

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(productService.findProductById(1L)).thenReturn(Optional.of(mockProduct));
        when(orderRepository.save(any(Order.class))).thenReturn(new Order());

        PaymentIntent mockPaymentIntent = mock(PaymentIntent.class);
        when(mockPaymentIntent.getClientSecret()).thenReturn("test_client_secret");
        mockStatic(PaymentIntent.class);
        when(PaymentIntent.create(any(PaymentIntentCreateParams.class))).thenReturn(mockPaymentIntent);

        ResponseEntity<Map<String, String>> response = paymentController.createPaymentIntent(requestData);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("clientSecret"));
        assertEquals("test_client_secret", response.getBody().get("clientSecret"));

        verify(productService, times(1)).findProductById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }
    
 
    
    @Test
    void testCreateUser_EmptyUsername() {
        User user = new User();
        user.setUsername(""); // Nombre de usuario vacío
        user.setPassword("password123");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Username is required", exception.getMessage());
    }

    @Test
    void testCreateUser_EmptyPassword() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(""); // Contraseña vacía

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        System.err.println(exception.getMessage());
        assertEquals("Password is required", exception.getMessage());
    }
    
    @Test
    void testUpdateCartItemQuantity_InvalidQuantity() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setQuantity(2);

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartService.updateCartItemQuantity(1L, 0); // Cantidad inválida
        });
        
        

        assertEquals("Quantity must be greater than zero", exception.getMessage());
    }
    
    
    @Test
    void testUpdateCartItemQuantity_Success() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setQuantity(2);

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        CartItem updatedCartItem = cartService.updateCartItemQuantity(1L, 5);

        assertNotNull(updatedCartItem);
        assertEquals(5, updatedCartItem.getQuantity());
        verify(cartItemRepository, times(1)).save(cartItem);
    }
}
