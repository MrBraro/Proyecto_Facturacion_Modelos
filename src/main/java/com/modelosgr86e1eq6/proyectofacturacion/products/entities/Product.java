package com.modelosgr86e1eq6.proyectofacturacion.products.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un producto del catálogo.
 *
 * <p>Implementa soft delete mediante el campo {@code activo}: un producto
 * eliminado (RF-05) conserva su registro en base de datos con {@code activo = false}.
 * Todas las queries del repositorio filtran por este campo para mantener
 * consistencia total del soft delete.</p>
 *
 * @author MrBraro
 * @see com.modelosgr86e1eq6.proyectofacturacion.products.repositories.ProductoRepository
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    /**
     * Identificador único interno del producto (PK autogenerada).
     * Usado en operaciones de escritura: actualizar (RF-04), eliminar (RF-05)
     * y validar stock (RF-06).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_product")
    private Integer idProducto;

    /**
     * Código semántico único del producto (ej. "P001").
     * Usado en la búsqueda pública RF-03. El código puede reutilizarse
     * después de un soft delete porque {@code existsByCodigoAndActivoTrue}
     * solo valida contra productos activos.
     */
    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    /** Nombre descriptivo del producto. */
    @Column(nullable = false, length = 150)
    private String nombre;

    /**
     * Precio unitario del producto.
     * Se define precisión (10, 2) para evitar errores silenciosos de
     * redondeo en la capa SQL con dinero.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    /** Descripción opcional del producto. */
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /** Cantidad disponible en inventario. */
    @Column(nullable = false)
    private int stock;

    /**
     * Indicador de estado lógico. {@code false} significa que el producto
     * fue eliminado (soft delete). Toda operación de lectura y escritura
     * filtra por este campo para evitar el "zombie corporativo".
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    /** Fecha y hora de creación del registro, asignada automáticamente. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Asigna la fecha de creación justo antes de persistir por primera vez. */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

