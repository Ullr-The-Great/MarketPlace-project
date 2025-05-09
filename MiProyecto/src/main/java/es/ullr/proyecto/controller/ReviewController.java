package es.ullr.proyecto.controller;

import es.ullr.proyecto.model.Product;
import es.ullr.proyecto.model.Review;
import es.ullr.proyecto.model.User;
import es.ullr.proyecto.service.ProductService;
import es.ullr.proyecto.service.ReviewService;
import es.ullr.proyecto.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController 
{

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    // Crear una reseña
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestParam Long productId, @RequestParam Long userId, @RequestParam int rating, @RequestParam String commentario) 
    {
        Product product = productService.findProductById(productId).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        User user = userService.findByUsername(userId.toString()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Review review = reviewService.createReview(product, user, rating, commentario);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    // Obtener reseñas de un producto
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getReviewsByProduct(@PathVariable Long productId) 
    {
        Product product = productService.findProductById(productId).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        List<Review> reviews = reviewService.findReviewsByProduct(product);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }
}