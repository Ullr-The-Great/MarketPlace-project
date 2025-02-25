package es.ullr.proyecto.controller;

import es.ullr.proyecto.model.Product;
import es.ullr.proyecto.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController 
{

    @Autowired
    private ProductService productService;

    // Crear un producto
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) 
    {
        Product savedProduct = productService.createProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    // Obtener todos los productos
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() 
    {
        List<Product> products = productService.findAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Obtener un producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) 
    {
        Optional<Product> product = productService.findProductById(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Obtener productos por categoría
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) 
    {
        List<Product> products = productService.findProductsByCategoryId(categoryId);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Buscar productos por nombre
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProductsByName(@RequestParam String name) 
    {
        List<Product> products = productService.findProductsByNameContaining(name);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}
