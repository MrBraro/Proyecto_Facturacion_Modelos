package com.modelosgr86e1eq6.proyectofacturacion.sales.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** POST /api/v1/sales - crear venta */
@Data
public class CreateSaleRequest {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Integer clientId;
}