package es.ullr.proyecto.service;

import es.ullr.proyecto.model.Product;
import es.ullr.proyecto.model.ProductImage;
import es.ullr.proyecto.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService 
{

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> findProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> findProductsByNameContaining(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
    
    public Product createProductWithImages(Product product, List<String> imageUrls) {
        Product savedProduct = productRepository.save(product);
        
        List<ProductImage> images = imageUrls.stream()
            .map(url -> new ProductImage(url, savedProduct))
            .collect(Collectors.toList());
            
        savedProduct.setImages(images);
        return productRepository.save(savedProduct);
    }

    public Product addImageToProduct(Long productId, String imageUrl) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        ProductImage newImage = new ProductImage();
        newImage.setImageUrl(imageUrl);
        newImage.setProduct(product);
        
        product.getImages().add(newImage);
        return productRepository.save(product);
    }
}