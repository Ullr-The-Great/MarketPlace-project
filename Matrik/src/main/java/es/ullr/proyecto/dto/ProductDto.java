package es.ullr.proyecto.dto;

import java.util.List;
import java.util.stream.Collectors;

import es.ullr.proyecto.model.Product;
import es.ullr.proyecto.model.ProductImage;
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
	private List<String> imageUrls;

	public ProductDto(Product product) {
		this.id = product.getId();
		this.name = product.getName();
		this.price = product.getPrice();
		this.description = product.getDescription();
		this.stock = product.getStock();
		this.createdAt = product.getCreatedAt() != null ? product.getCreatedAt().toString() : null;
		this.category = new CategoryDto(product.getCategory());
		
		
		if (product.getImages() != null) {
			this.imageUrls = product.getImages().stream()
				.map(ProductImage::getImageUrl)
				.collect(Collectors.toList());
		}
	}
}
