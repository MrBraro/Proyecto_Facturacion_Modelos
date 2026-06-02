package com.modelosgr86e1eq6.proyectofacturacion.invoices.entities;

/**
 * Tipo de factura generada por el sistema.
 *
 * <p>Mapeado al enum SQL {@code invoice_type} con {@code EnumType.STRING}
 * para garantizar compatibilidad ante migraciones de esquema.</p>
 *
 * <ul>
 *   <li>{@link #SIMPLE}   – Factura básica sin QR ni marca de agua.</li>
 *   <li>{@link #DETAILED} – Factura detallada con QR y soporte de marca de agua.</li>
 * </ul>
 *
 * @author MrBraro
 */
public enum InvoiceType {

    /** Factura básica: encabezado, productos, totales. Sin QR ni watermark. */
    SIMPLE,

    /** Factura detallada: incluye QR y soporte de watermark configurable. */
    DETAILED
}
