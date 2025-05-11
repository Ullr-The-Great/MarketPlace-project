package es.ullr.proyecto.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateQuantityRequest {
    private int quantity;
}