package es.ullr.proyecto.controller;

import es.ullr.proyecto.model.Product;
import es.ullr.proyecto.model.Review;
import es.ullr.proyecto.model.User;
import es.ullr.proyecto.service.OrderService;
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
    
    @Autowired
    private OrderService orderService;

    // Crear una reseña
    @PostMapping
    public ResponseEntity<Review> createOrUpdateReview(
        @RequestParam Long productId,
        @RequestParam int rating,
        @RequestParam String comment_review,

        Authentication authentication
    ) {
        User user = userService.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Product product = productService.findProductById(productId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar si el usuario ha comprado el producto
        boolean hasPurchased = orderService.hasUserPurchasedProduct(user, product);
        if (!hasPurchased) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(null); // O puedes devolver un mensaje de error personalizado
        }

        Optional<Review> existingReview = reviewService.findReviewByProductAndUser(product, user);

        if (existingReview.isPresent()) {
            Review review = existingReview.get();
            review.setRating(rating);
            review.setComment_review(comment_review);
            return new ResponseEntity<>(reviewService.saveReview(review), HttpStatus.OK);
        }

        Review newReview = reviewService.createReview(product, user, rating, comment_review);

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