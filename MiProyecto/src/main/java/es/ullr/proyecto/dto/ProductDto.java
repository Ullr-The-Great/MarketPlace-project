package es.ullr.proyecto.dto;

import es.ullr.proyecto.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private double price;
    private String description;
    private CategoryDto category;
    private int stock;
    private String createdAt;
    
    public ProductDto(Product product) {
        // Mapear todos los campos necesarios
        this.category = new CategoryDto(product.getCategory());
    }

}