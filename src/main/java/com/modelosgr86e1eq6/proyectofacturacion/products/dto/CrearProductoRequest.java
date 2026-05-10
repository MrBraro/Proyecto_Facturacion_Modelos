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
 * <p>Las validaciones garantizan que la entidad persista en un estado
 * coherente sin necesidad de validar en la capa de servicio.</p>
 *
 * @author MrBraro
 */
@Data
public class CrearProductoRequest {

    /**
     * Código semántico único del producto.
     * Ejemplo: "P001", "LAP-15".
     */
    @NotBlank(message = "El código del producto es obligatorio")
    private String codigo;

    /** Nombre descriptivo del producto. */
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;

    /**
     * Precio unitario. Debe ser un valor positivo.
     * La capa de persistencia lo almacenará con precisión (10, 2).
     */
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    private BigDecimal precio;

    /** Descripción opcional del producto. */
    private String descripcion;

    /**
     * Cantidad inicial en inventario.
     * Se acepta 0 (producto sin stock al momento del registro).
     */
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
}
