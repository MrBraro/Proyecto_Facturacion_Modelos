package com.modelosgr86e1eq6.proyectofacturacion.sales.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSaleDetailRequest {

    @NotNull(message = "El ID del producto es obligatorio")
    private Integer productId;

    @NotNull(message = "La cantidad no puede ser nula")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer quantity;
}
