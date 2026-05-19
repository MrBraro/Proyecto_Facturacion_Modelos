package com.modelosgr86e1eq6.proyectofacturacion.sales.entities;

import com.modelosgr86e1eq6.proyectofacturacion.clients.entities.Client;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.Invoice;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa una venta registrada en el sistema.
 *
 * <p>Es el origen de toda factura: la relación {@code Sale -> Invoice}
 * es 1:1 (una venta genera exactamente una factura). Los totales
 * {@code subtotal}, {@code tax} y {@code total} son calculados por el
 * módulo de ventas y reutilizados directamente por el módulo de facturas,
 * evitando duplicación de lógica.</p>
 *
 * @author MrBraro
 * @see com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.Invoice
 */
@Entity
@Table(name = "sales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale {

    /**
     * Identificador único de la venta (PK autogenerada).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sale")
    private Integer idSale;

    /**
     * Cliente al que pertenece esta venta.
     * Cargado de forma lazy para evitar joins innecesarios.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    /**
     * Subtotal de la venta antes de impuestos.
     * Calculado en el módulo de ventas; reutilizado por la factura.
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    /**
     * Monto de impuestos (IVA u otro) calculado sobre el subtotal.
     * Calculado en el módulo de ventas; reutilizado por la factura.
     */
    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal tax = BigDecimal.ZERO;

    /**
     * Total final de la venta ({@code subtotal + tax}).
     * Calculado en el módulo de ventas; reutilizado por la factura.
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    /** Fecha y hora en que se registró la venta. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Líneas de detalle de la venta (productos, cantidades, precios).
     * Mapeadas con cascade PERSIST/MERGE para guardar los detalles
     * junto con la venta en una sola operación.
     */
    @OneToMany(mappedBy = "sale", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
               fetch = FetchType.LAZY)
    @Builder.Default
    private List<SaleDetail> details = new ArrayList<>();

    /**
     * Factura asociada a esta venta.
     * La relación es 1:1; {@code Invoice} es el lado propietario.
     * Este campo permite navegación bidireccional sin duplicar el JOIN.
     */
    @OneToOne(mappedBy = "sale", fetch = FetchType.LAZY)
    private Invoice invoice;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
