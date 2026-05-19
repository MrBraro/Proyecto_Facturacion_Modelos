package com.modelosgr86e1eq6.proyectofacturacion.invoices.entities;

/**
 * Estado de pago de una factura.
 *
 * <p>Mapeado al enum SQL {@code invoice_pay_status} con {@code EnumType.STRING}.
 * Permite filtrar facturas por estado y actualizar el ciclo de pago
 * sin modificar el esquema de base de datos.</p>
 *
 * <ul>
 *   <li>{@link #PENDING}  – Factura emitida, pago pendiente.</li>
 *   <li>{@link #PAID}     – Pago confirmado.</li>
 *   <li>{@link #REJECTED} – Pago rechazado o factura anulada.</li>
 * </ul>
 *
 * @author MrBraro
 */
public enum InvoicePayStatus {

    /** Pago aún no registrado. Estado inicial al emitir la factura. */
    PENDING,

    /** El pago fue procesado y confirmado correctamente. */
    PAID,

    /** El pago fue rechazado o la factura fue anulada. */
    REJECTED
}
