package com.modelosgr86e1eq6.proyectofacturacion.invoices.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO que representa una línea de producto dentro de la respuesta de una factura.
 *
 * <p>Proyecta los datos de {@code SaleDetail} y su {@code Product} asociado
 * en una estructura plana adecuada para consumo por el cliente del API,
 * sin exponer las entidades JPA directamente.</p>
 *
 * @author MrBraro
 */
@Data
public class InvoiceLineItemResponse {

    /** Código semántico del producto (ej. {@code P001}). */
    private String productCode;

    /** Nombre descriptivo del producto. */
    private String productName;

    /** Cantidad de unidades vendidas. */
    private int quantity;

    /** Precio unitario capturado en el momento de la venta. */
    private BigDecimal unitPrice;

    /** Total de la línea ({@code quantity × unitPrice}). */
    private BigDecimal lineTotal;
}
