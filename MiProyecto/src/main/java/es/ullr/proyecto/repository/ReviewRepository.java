package es.ullr.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.ullr.proyecto.model.Product;
import es.ullr.proyecto.model.Review;
import es.ullr.proyecto.model.User;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> 
{
    List<Review> findByProduct(Product product); // Buscar reseñas por producto
    List<Review> findByProductId(Long productId); // Buscar reseñas por ID de producto
	Optional<Review> findByProductAndUser(Product product, User user);
}