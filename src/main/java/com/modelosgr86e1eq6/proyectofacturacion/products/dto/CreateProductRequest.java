package com.modelosgr86e1eq6.proyectofacturacion.products.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO de entrada para el registro de un nuevo producto (RF-01).
 *
 * @author MrBraro
 */
@Data
public class CreateProductRequest {

    /** Código semántico único del producto (ej. "P001"). */
    @NotBlank(message = "El código del producto es obligatorio")
    private String code;

    /** Nombre descriptivo del producto. */
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String name;

    /** Precio unitario. Debe ser mayor a cero. */
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    private BigDecimal price;

    /** Descripción opcional del producto. */
    private String description;

    /**
     * Cantidad inicial en inventario.
     * Se acepta 0 (producto sin stock al momento del registro).
     */
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
}
