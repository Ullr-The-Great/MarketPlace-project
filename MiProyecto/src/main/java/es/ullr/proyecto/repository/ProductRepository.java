package es.ullr.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.ullr.proyecto.model.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> 
{
    List<Product> findByCategoryId(Long categoryId); // Buscar productos por categoría
    List<Product> findByNameContainingIgnoreCase(String name); // Buscar productos por nombre (ignorando mayúsculas/minúsculas)
}
