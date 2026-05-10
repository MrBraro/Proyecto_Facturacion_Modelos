package com.modelosgr86e1eq6.proyectofacturacion.products.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de salida para exponer la información pública de un producto.
 *
 * <p>Retornado por todos los endpoints del CRUD (RF-01 a RF-04).</p>
 *
 * @author MrBraro
 */
@Data
public class ProductoResponse {

    /** PK interna del producto. Usada como referencia en PUT y DELETE. */
    private Integer id;

    /** Código semántico del producto. Usado en GET /productos/{codigo}. */
    private String codigo;

    /** Nombre descriptivo del producto. */
    private String nombre;

    /** Precio unitario con precisión de dos decimales. */
    private BigDecimal precio;

    /** Descripción del producto. Puede ser nula. */
    private String descripcion;

    /** Cantidad disponible en inventario. */
    private int stock;

    /** {@code true} si el producto está activo; {@code false} si fue eliminado (soft delete). */
    private boolean activo;

    /** Fecha y hora en que fue registrado el producto. */
    private LocalDateTime createdAt;
}
