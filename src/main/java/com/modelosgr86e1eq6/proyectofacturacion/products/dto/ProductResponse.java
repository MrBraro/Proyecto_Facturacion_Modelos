package com.modelosgr86e1eq6.proyectofacturacion.products.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de salida con la información pública de un producto.
 *
 * <p>Retornado por todos los endpoints del CRUD (RF-01 a RF-04).</p>
 *
 * @author MrBraro
 */
@Data
public class ProductResponse {

    /** PK interna del producto. */
    private Integer id;

    /** Código semántico del producto. */
    private String code;

    /** Nombre descriptivo del producto. */
    private String name;

    /** Precio unitario con precisión de dos decimales. */
    private BigDecimal price;

    /** Descripción del producto. Puede ser {@code null}. */
    private String description;

    /** Cantidad disponible en inventario. */
    private int stock;

    /** {@code true} si el producto está activo; {@code false} si fue eliminado. */
    private boolean active;

    /** Fecha y hora en que fue registrado el producto. */
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última modificación.
     * {@code null} si el producto nunca fue actualizado.
     */
    private LocalDateTime updatedAt;
}
