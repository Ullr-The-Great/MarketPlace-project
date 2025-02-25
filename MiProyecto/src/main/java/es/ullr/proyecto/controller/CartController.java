package es.ullr.proyecto.controller;

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
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        Cart cart = cartService.createCart(user);
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
    public ResponseEntity<List<CartItem>> getCartItems(@PathVariable Long cartId) 
    {
        Cart cart = cartService.findCartById(cartId).orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        List<CartItem> cartItems = cartService.getCartItems(cart);
        return new ResponseEntity<>(cartItems, HttpStatus.OK);
    }
}
