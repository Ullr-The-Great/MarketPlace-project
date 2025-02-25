package es.ullr.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.ullr.proyecto.model.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> 
{
    Optional<Category> findByName(String name); // Buscar categoría por nombre
}
