package com.modelosgr86e1eq6.proyectofacturacion.sales.entities;

import com.modelosgr86e1eq6.proyectofacturacion.clients.entities.Client;
import com.modelosgr86e1eq6.proyectofacturacion.users.entities.User;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>Una venta está asociada a un {@link Client} y a un {@link User} que la registra.
 * Los totales (subtotal, iva, total) son recalculados automáticamente por el trigger
 * {@code trg_recalcular_venta} en base de datos cada vez que se modifican los
 * {@code SaleDetail} asociados.</p>
 */
@Entity
@Table(name = "ventas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private SaleStatus state = SaleStatus.ABIERTA;

    @Column(nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal iva = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "fecha_venta", nullable = false, updatable = false)
    private LocalDateTime saleDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.saleDate = LocalDateTime.now();
        this.createdAt  = LocalDateTime.now();
    }
}
