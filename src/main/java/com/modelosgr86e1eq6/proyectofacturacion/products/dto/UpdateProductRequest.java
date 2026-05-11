package com.modelosgr86e1eq6.proyectofacturacion.products.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO de entrada para la actualización de un producto existente (RF-04).
 *
 * <p>El código puede cambiarse siempre que no esté en uso por otro producto activo.</p>
 *
 * @author MrBraro
 */
@Data
public class UpdateProductRequest {

    /** Nuevo código semántico del producto. Debe ser único entre productos activos. */
    @NotBlank(message = "El código del producto es obligatorio")
    private String code;

    /** Nuevo nombre descriptivo del producto. */
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String name;

    /** Nuevo precio unitario. Debe ser mayor a cero. */
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    private BigDecimal price;

    /** Nueva descripción. Si es {@code null}, se limpia la descripción existente. */
    private String description;

    /** Nueva cantidad en inventario. No puede ser negativa. */
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
}
