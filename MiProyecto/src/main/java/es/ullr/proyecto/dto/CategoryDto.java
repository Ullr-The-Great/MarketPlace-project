package es.ullr.proyecto.dto;

import es.ullr.proyecto.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
    
    public CategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }

}