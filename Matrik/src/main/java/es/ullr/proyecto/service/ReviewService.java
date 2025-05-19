package es.ullr.proyecto.service;

import es.ullr.proyecto.model.Product;
import es.ullr.proyecto.model.Review;
import es.ullr.proyecto.model.User;
import es.ullr.proyecto.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService
{

    @Autowired
    private ReviewRepository reviewRepository;

    public Review createReview(Product product, User user, int rating, String comment_review) 
    {
        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(rating);
        review.setComment_review(comment_review);
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

    public Optional<Review> findReviewByProductAndUser(Product product, User user) {
        return reviewRepository.findByProductAndUser(product, user);
    }

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }
    
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

	public Optional<Review> findById(Long reviewId) {
		// TODO Auto-generated method stub
		return reviewRepository.findById(reviewId);
	}
}