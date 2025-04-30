package es.ullr.proyecto.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.ullr.proyecto.model.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> 
{
    List<Product> findByCategoryId(Long categoryId); // Buscar productos por categoría
    List<Product> findByNameContainingIgnoreCase(String name); // Buscar productos por nombre (ignorando mayúsculas/minúsculas)
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    List<Product> findByNameContainingIgnoreCaseAndCategoryId(String name, Long categoryId);
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseAndCategoryId(String name, Long categoryId, Pageable pageable);
}
