package es.ullr.proyecto.service;

import es.ullr.proyecto.model.Cart;
import es.ullr.proyecto.model.CartItem;
import es.ullr.proyecto.model.Product;
import es.ullr.proyecto.model.User;
import es.ullr.proyecto.repository.CartItemRepository;
import es.ullr.proyecto.repository.CartRepository;
import es.ullr.proyecto.repository.ProductRepository;
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

    public Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    public Optional<Cart> findCartByUser(User user) {
        return cartRepository.findByUser(user);
    }
    
    public Optional<Cart> findCartById(long id)
    {
    	return cartRepository.findById(id);
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

    public void removeProductFromCart(Cart cart, Product product) {
        Optional<CartItem> cartItem = cartItemRepository.findByCartAndProduct(cart, product);
        cartItem.ifPresent(cartItemRepository::delete);
    }

    public List<CartItem> getCartItems(Cart cart) {
        return cartItemRepository.findByCart(cart);
    }
}