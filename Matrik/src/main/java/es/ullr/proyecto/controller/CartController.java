package es.ullr.proyecto.controller;

import es.ullr.proyecto.dto.AddItemRequest;
import es.ullr.proyecto.dto.CartItemDto;
import es.ullr.proyecto.dto.CartResponse;
import es.ullr.proyecto.dto.UpdateQuantityRequest;
import es.ullr.proyecto.model.Cart;
import es.ullr.proyecto.model.CartItem;
import es.ullr.proyecto.model.Product;
import es.ullr.proyecto.model.User;
import es.ullr.proyecto.service.CartService;
import es.ullr.proyecto.service.ProductService;
import es.ullr.proyecto.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/carts")
public class CartController 
{

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

   
    // Crear un carrito para un usuario
    @PostMapping("/user/{userId}")
    public ResponseEntity<Cart> createCart(@PathVariable Long userId) 
    {
        User user = userService.findByUsername(userId.toString()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Cart cart = cartService.getOrCreateCart(user);
        return new ResponseEntity<>(cart, HttpStatus.CREATED);
    }
    
    // Añadir un producto al carrito
    @PostMapping("/{cartId}/add")
    public ResponseEntity<CartItem> addProductToCart(@PathVariable Long cartId, @RequestParam Long productId, @RequestParam int quantity) 
    {
        Product product = productService.findProductById(productId).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        Cart cart = cartService.findCartById(cartId).orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        CartItem cartItem = cartService.addProductToCart(cart, product, quantity);
        return new ResponseEntity<>(cartItem, HttpStatus.CREATED);
    }

    // Eliminar un producto del carrito
    @DeleteMapping("/{cartId}/remove")
    public ResponseEntity<Void> removeProductFromCart(@PathVariable Long cartId, @RequestParam Long productId) 
    {
        Product product = productService.findProductById(productId).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        Cart cart = cartService.findCartById(cartId).orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        cartService.removeProductFromCart(cart, product);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } 
    
    // Obtener los ítems del carrito
    @GetMapping("/{cartId}/items")
    public ResponseEntity<CartResponse> getCartItems(@PathVariable Long cartId) {
        Cart cart = cartService.findCartById(cartId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        
        return ResponseEntity.ok(new CartResponse(cart));
    }
    
    // Obtener carrito del usuario actual
    @GetMapping
    public ResponseEntity<Cart> getCart(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Cart cart = cartService.findCartByUser(user)
            .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
            
        return ResponseEntity.ok(cart);
    }

    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCartByUserId(
            @PathVariable Long userId,
            Authentication authentication) {
        
        try {
            // Verificar que el usuario autenticado coincide con el userId solicitado
            User authenticatedUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));
                
            if (authenticatedUser.getId() != (userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
            }
            
            Cart cart = cartService.findCartById(userId)
                .orElseGet(() -> cartService.getOrCreateCart(authenticatedUser));
                
            return ResponseEntity.ok(cart);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    

    // añadir items
    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItem> addItemToCart(
        @PathVariable Long cartId,
        @RequestBody AddItemRequest request,
        Authentication authentication
    ) {
        User user = userService.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Cart cart = cartService.findCartById(cartId)
            .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        
        if (cart.getUser().getId() !=user.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Product product = productService.findProductById(request.getProductId())
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        CartItem item = cartService.addProductToCart(cart, product, request.getQuantity());
        
        return ResponseEntity.ok(item);
    }
    
    // Eliminar producto del carrito
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeItem(
        @PathVariable Long itemId,
        Authentication authentication
    ) {
        User user = userService.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
        cartService.removeProductFromCartById(user, itemId);
        return ResponseEntity.noContent().build();
    }

    // Vaciar carrito
    @DeleteMapping
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        // Obtener el usuario autenticado
        User user = userService.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtener el carrito del usuario
        Cart cart = cartService.getOrCreateCart(user);

        // Eliminar todos los ítems del carrito
        cartService.clearCartItems(cart);

        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartItemDto> updateCartItemQuantity(
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> request,
            Authentication authentication) {
        
        // Obtener el username del principal
        String username = authentication.getName();
        
        // Buscar el usuario real en tu servicio
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        int newQuantity = request.get("quantity");
        
        CartItem updatedItem = cartService.updateCartItemQuantity(itemId, newQuantity);
        
        // Verificar que el item pertenece al carrito del usuario
        if (updatedItem.getCart().getUser().getId() != (user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(new CartItemDto(updatedItem));
    }
}
