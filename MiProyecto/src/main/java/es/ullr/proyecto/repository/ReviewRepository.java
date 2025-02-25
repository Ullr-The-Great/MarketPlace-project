package es.ullr.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.ullr.proyecto.model.Product;
import es.ullr.proyecto.model.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> 
{
    List<Review> findByProduct(Product product); // Buscar reseñas por producto
    List<Review> findByProductId(Long productId); // Buscar reseñas por ID de producto
}