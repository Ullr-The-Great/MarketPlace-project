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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<Review> createOrUpdateReview(
        @RequestParam Long productId,
        @RequestParam int rating,
        @RequestParam String commentario,
        Authentication authentication
    ) {
        // Obtener el usuario autenticado
        User user = userService.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtener el producto
        Product product = productService.findProductById(productId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar si ya existe una reseña del usuario para este producto
        Optional<Review> existingReview = reviewService.findReviewByProductAndUser(product, user);

        if (existingReview.isPresent()) {
            // Actualizar la reseña existente
            Review review = existingReview.get();
            review.setRating(rating);
            review.setCommentario(commentario);
            return new ResponseEntity<>(reviewService.saveReview(review), HttpStatus.OK);
        }

        // Crear una nueva reseña
        Review newReview = reviewService.createReview(product, user, rating, commentario);
        return new ResponseEntity<>(newReview, HttpStatus.CREATED);
    }

    // Obtener reseñas de un producto
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getReviewsByProduct(@PathVariable Long productId) 
    {
        Product product = productService.findProductById(productId).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        List<Review> reviews = reviewService.findReviewsByProduct(product);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }
    
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Review review = reviewService.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Reseña no encontrada"));

        // Verificar que la reseña pertenece al usuario autenticado
        if (review.getUser().getId() !=(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
    
}