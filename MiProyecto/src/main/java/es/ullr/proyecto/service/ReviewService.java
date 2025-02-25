package es.ullr.proyecto.service;

import es.ullr.proyecto.model.Product;
import es.ullr.proyecto.model.Review;
import es.ullr.proyecto.model.User;
import es.ullr.proyecto.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService
{

    @Autowired
    private ReviewRepository reviewRepository;

    public Review createReview(Product product, User user, int rating, String commentario) 
    {
        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(rating);
        review.setCommentario(commentario);
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    public List<Review> findReviewsByProduct(Product product) 
    {
        return reviewRepository.findByProduct(product);
    }

    public List<Review> findReviewsByProductId(Long productId) 
    {
        return reviewRepository.findByProductId(productId);
    }
}