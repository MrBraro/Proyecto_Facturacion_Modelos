package com.modelosgr86e1eq6.proyectofacturacion.products.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un producto del catálogo.
 *
 * <p>Implementa soft delete mediante el campo {@code isActive}: un producto
 * eliminado (RF-05) conserva su registro en base de datos con {@code isActive = false}.
 * Todas las queries del repositorio filtran por este campo para mantener
 * consistencia total del soft delete.</p>
 *
 * @author MrBraro
 * @see com.modelosgr86e1eq6.proyectofacturacion.products.repositories.ProductRepository
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
    private Integer idProduct;

    /**
     * Código semántico único del producto (ej. "P001").
     * Usado en la búsqueda pública RF-03. Puede reutilizarse tras un soft
     * delete porque {@code existsByCodeAndIsActiveTrue} solo valida activos.
     */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    /** Nombre descriptivo del producto. */
    @Column(nullable = false, length = 150)
    private String name;

    /**
     * Precio unitario del producto.
     * Precisión (10, 2) para evitar errores silenciosos de redondeo con dinero.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /** Descripción opcional del producto. */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Cantidad disponible en inventario. */
    @Column(nullable = false)
    private int stock;

    /**
     * Indicador de estado lógico. {@code false} indica que el producto fue
     * eliminado mediante soft delete (RF-05).
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    /** Fecha y hora de creación del registro. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última modificación del registro.
     * Permite tener trazabilidad de cambios como actualizaciones de precio
     * o descripción sin depender del módulo de auditoría.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
