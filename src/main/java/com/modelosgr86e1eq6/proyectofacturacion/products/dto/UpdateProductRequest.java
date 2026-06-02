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
 * <p>Según RF-04: No permite modificar el código identificador ni el stock directamente.</p>
 *
 * @author MrBraro
 */
@Data
public class UpdateProductRequest {

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

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
}
