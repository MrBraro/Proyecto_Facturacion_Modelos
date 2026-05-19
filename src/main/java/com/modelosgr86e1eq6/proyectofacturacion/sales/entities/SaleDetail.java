package com.modelosgr86e1eq6.proyectofacturacion.sales.entities;

import com.modelosgr86e1eq6.proyectofacturacion.products.entities.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entidad JPA que representa una línea de detalle dentro de una venta.
 *
 * <p>Cada {@code SaleDetail} corresponde a un producto vendido con su
 * cantidad, precio unitario al momento de la venta y el total de línea.
 * El precio unitario se captura en el momento de la venta para preservar
 * el historial aunque el precio del producto cambie posteriormente.</p>
 *
 * @author MrBraro
 * @see Sale
 */
@Entity
@Table(name = "sale_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleDetail {

    /**
     * Identificador único de la línea de detalle (PK autogenerada).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sale_detail")
    private Integer idSaleDetail;

    /**
     * Venta a la que pertenece este detalle.
     * Lado propietario de la relación {@code Sale -> SaleDetail}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sale", nullable = false)
    private Sale sale;

    /**
     * Producto incluido en esta línea de detalle.
     * Cargado de forma lazy; usar join fetch cuando se necesite el nombre
     * del producto para generar la factura.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_product", nullable = false)
    private Product product;

    /**
     * Cantidad de unidades vendidas del producto.
     */
    @Column(nullable = false)
    private int quantity;

    /**
     * Precio unitario del producto en el momento de la venta.
     * No se actualiza si el precio del producto cambia después.
     */
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    /**
     * Total de la línea ({@code quantity × unitPrice}).
     * Calculado en el módulo de ventas; reutilizado en la generación de facturas.
     */
    @Column(name = "line_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;
}
