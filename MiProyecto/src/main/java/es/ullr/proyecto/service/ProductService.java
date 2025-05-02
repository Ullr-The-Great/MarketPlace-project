package es.ullr.proyecto.service;

import es.ullr.proyecto.model.Product;
import es.ullr.proyecto.model.ProductImage;
import es.ullr.proyecto.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        // Guardar el producto
        Product savedProduct = productRepository.save(product);

        // Asegúrate de modificar la colección existente
        if (savedProduct.getImages() == null) {
            savedProduct.setImages(new ArrayList<>());
        }

        // Asociar imágenes al producto
        for (String url : imageUrls) {
            ProductImage image = new ProductImage(url, savedProduct);
            savedProduct.getImages().add(image);
        }

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

    public Page<Product> findPaginatedProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }

	public Page<Product> findProductsByCategoryIdPaginated(Long categoryId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return productRepository.findByCategoryId(categoryId, pageable);
}

	public List<Product> findProductsByNameAndCategory(String name, Long categoryId) {
	    return productRepository.findByNameContainingIgnoreCaseAndCategoryId(name, categoryId);
	}

	public Page<Product> findProductsByNamePaginated(String name, int page, int size) {
	    Pageable pageable = PageRequest.of(page, size);
	    return productRepository.findByNameContainingIgnoreCase(name, pageable);
	}

	public Page<Product> findProductsByNameAndCategoryPaginated(String name, Long categoryId, int page, int size) {
	    Pageable pageable = PageRequest.of(page, size);
	    return productRepository.findByNameContainingIgnoreCaseAndCategoryId(name, categoryId, pageable);
	}
	
	public Product saveProduct(Product product) {
	    return productRepository.save(product);
	}
}