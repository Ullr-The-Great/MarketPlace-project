package es.ullr.proyecto.service;

import es.ullr.proyecto.model.Cart;
import es.ullr.proyecto.model.CartItem;
import es.ullr.proyecto.model.Product;
import es.ullr.proyecto.model.User;
import es.ullr.proyecto.repository.CartItemRepository;
import es.ullr.proyecto.repository.CartRepository;
import es.ullr.proyecto.repository.ProductRepository;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    public Optional<Cart> findCartByUser(User user) {
        return cartRepository.findByUser(user);
    }
    
    public Optional<Cart> findCartById(long id)
    {
    	return cartRepository.findById(id);
    }
    
    public Cart findOrCreateCart(User user) {
        return cartRepository.findByUser(user)
            .orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setUser(user);
                return cartRepository.save(newCart);
            });
    }
    
    public Cart findOrCreateCartById(Long id) {
        return cartRepository.findById(id)
            .orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setId(id);
                return cartRepository.save(newCart);
            });
    }


    public CartItem addProductToCart(Cart cart, Product product, int quantity) {
        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProduct(cart, product);
        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            return cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            return cartItemRepository.save(cartItem);
        }
    }

    public void removeProductFromCartById(User user, Long productId) {
    	Cart cart = getOrCreateCart(user);
        cartItemRepository.findById(productId)
                .ifPresent(item -> {
                    if (item.getCart().getId().equals(cart.getId())) {
                        cartItemRepository.delete(item);
                    } else {
                        throw new RuntimeException("El ítem no pertenece al carrito del usuario");
                    }
                });
    }

    public void removeProductFromCart(Cart cart,Product product) {
        Optional<CartItem> cartItem = cartItemRepository.findByCartAndProduct(cart, product);
        cartItem.ifPresent(cartItemRepository::delete);
    }
    
    public List<CartItem> getCartItems(Cart cart) {
        return cartItemRepository.findByCart(cart);
    }
    
    @Transactional
    public void clearCartItems(Cart cart) {
        cartItemRepository.deleteByCart(cart); // Elimina todos los ítems asociados al carrito
    }
    
    public CartItem updateCartItemQuantity(Long cartItemId, int newQuantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        
        cartItem.setQuantity(newQuantity);
        return cartItemRepository.save(cartItem);
    }
}