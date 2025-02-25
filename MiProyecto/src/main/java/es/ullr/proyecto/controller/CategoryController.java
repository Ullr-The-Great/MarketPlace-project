package es.ullr.proyecto.controller;

import es.ullr.proyecto.model.Category;
import es.ullr.proyecto.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryController 
{

    @Autowired
    private CategoryService categoryService;

    // Crear una categoría
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) 
    {
        Category savedCategory = categoryService.createCategory(category);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    // Obtener todas las categorías
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() 
    {
        List<Category> categories = categoryService.findAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Obtener una categoría por ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) 
    {
        Optional<Category> category = categoryService.findCategoryById(id);
        return category.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Obtener una categoría por nombre
    @GetMapping("/name/{name}")
    public ResponseEntity<Category> getCategoryByName(@PathVariable String name) 
    {
        Optional<Category> category = categoryService.findCategoryByName(name);
        return category.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}