package es.ullr.proyecto.controller;

import es.ullr.proyecto.dto.ProductDto;
import es.ullr.proyecto.dto.ProductWithImagesDTO;
import es.ullr.proyecto.model.Category;
import es.ullr.proyecto.model.Product;
import es.ullr.proyecto.model.ProductImage;
import es.ullr.proyecto.service.CategoryService;
import es.ullr.proyecto.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController 
{

    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;

    // Crear un producto
   /* @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) 
    {
        Product savedProduct = productService.createProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }
*/
    // Obtener todos los productos
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = productService.findAllProducts().stream()
            .map(ProductDto::new) // Convertir cada producto a un DTO
            .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }
    
    // Obtener un producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        Product product = productService.findProductById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return ResponseEntity.ok(new ProductDto(product));
    }

    // Obtener productos por categoría
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductDto>> getProductsByCategoryPaginated(
        @PathVariable Long categoryId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "3") int size
    ) {
        Page<Product> productPage = productService.findProductsByCategoryIdPaginated(categoryId, page, size);
        Page<ProductDto> productDtoPage = productPage.map(ProductDto::new); // Convertir a DTO
        return ResponseEntity.ok(productDtoPage);
    }
    // Buscar productos por nombre
    @GetMapping("/search-by-name")
    public ResponseEntity<List<Product>> searchProductsByName(@RequestParam String name) 
    {
        List<Product> products = productService.findProductsByNameContaining(name);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
    
    //Imagenes productos
    
	    @PostMapping
	    public ResponseEntity<Product> createProduct(@RequestBody ProductWithImagesDTO productDTO) {
	    	Product product = new Product();
	        product.setName(productDTO.getName());
	        product.setDescription(productDTO.getDescription());
	        product.setPrice(productDTO.getPrice());
	        product.setStock(productDTO.getStock());

	        // Asignar la categoría
	        Category category = categoryService.findCategoryById(productDTO.getCategoryId())
	            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
	        product.setCategory(category);

	        // Crear producto con imágenes
	        Product savedProduct = productService.createProductWithImages(product, productDTO.getImageUrls());
	        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
	    }

	    @PostMapping("/{id}/images")
	    public ResponseEntity<Product> addImageToProduct(@PathVariable Long id, @RequestBody String imageUrl) {
	        // Limpiar caracteres adicionales como comillas y barras invertidas
	        String cleanedImageUrl = imageUrl.trim()
	                                         .replaceAll("^\"|\"$", "") // Elimina comillas al inicio y al final
	                                         .replace("\\", "");       // Elimina barras invertidas

	        Product updatedProduct = productService.addImageToProduct(id, cleanedImageUrl);
	        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
	    }
	    
	    @GetMapping("/{id}/images")
	    public ResponseEntity<List<String>> getProductImages(@PathVariable Long id) {
	        Product product = productService.findProductById(id)
	            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

	        List<String> imageUrls = product.getImages().stream()
	            .map(ProductImage::getImageUrl)
	            .collect(Collectors.toList());

	        return ResponseEntity.ok(imageUrls);
	    }
    
	    @GetMapping("/paginated")
	    public ResponseEntity<Page<ProductDto>> getPaginatedProducts(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "5") int size
	    ) {
	        Page<Product> productPage = productService.findPaginatedProducts(page, size);
	        Page<ProductDto> productDtoPage = productPage.map(ProductDto::new); // Convertir a DTO
	        return ResponseEntity.ok(productDtoPage);
	    }
	    
	    //Paginado
	    @GetMapping("/search")
	    public ResponseEntity<Page<ProductDto>> searchProducts(
	        @RequestParam String name,
	        @RequestParam(required = false) Long categoryId,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "5") int size
	    ) {
	        Page<Product> products;

	        if (categoryId != null) {
	            products = productService.findProductsByNameAndCategoryPaginated(name, categoryId, page, size);
	        } else {
	            products = productService.findProductsByNamePaginated(name, page, size);
	        }

	        Page<ProductDto> productDtos = products.map(ProductDto::new);
	        return ResponseEntity.ok(productDtos);
	    }
    
}
