package com.modelosgr86e1eq6.proyectofacturacion.invoices.dto;

import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoicePayStatus;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoiceType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de salida con la información completa de una factura.
 *
 * <p>Retornado por todos los endpoints del módulo de facturas.
 * Agrega datos del cliente (desde la venta) y las líneas de detalle
 * de productos sin exponer entidades JPA directamente.</p>
 *
 * <p>El campo {@code pdfPath} será {@code null} hasta que el servicio
 * de generación PDF esté implementado. Los campos {@code hasQr} y
 * {@code hasWatermark} permiten al frontend saber qué funcionalidades
 * están activas en el documento.</p>
 *
 * @author MrBraro
 */
@Data
public class InvoiceResponse {

    /** PK interna de la factura. */
    private Integer id;

    /**
     * Número de factura generado por el trigger PostgreSQL.
     * Formato: {@code INV-YYYY-NNNNNN}.
     */
    private String invoiceNumber;

    /** ID de la venta origen. */
    private Integer saleId;

    // ── Datos del cliente (desnormalizados desde Sale.client) ─────────────────

    /** Nombre completo del cliente. */
    private String clientName;

    /** Email del cliente. */
    private String clientEmail;

    // ── Clasificación y estado ────────────────────────────────────────────────

    /** Tipo de factura: {@code SIMPLE} o {@code DETAILED}. */
    private InvoiceType type;

    /** Estado de pago: {@code PENDING}, {@code PAID} o {@code REJECTED}. */
    private InvoicePayStatus payStatus;

    // ── Totales ───────────────────────────────────────────────────────────────

    /** Subtotal antes de impuestos. */
    private BigDecimal subtotal;

    /** Monto de impuestos (IVA). */
    private BigDecimal tax;

    /** Total final ({@code subtotal + tax}). */
    private BigDecimal total;

    // ── Líneas de detalle ─────────────────────────────────────────────────────

    /** Lista de productos incluidos en la factura con cantidades y precios. */
    private List<InvoiceLineItemResponse> lineItems;

    // ── Metadatos de exportación ──────────────────────────────────────────────

    /** Ruta del archivo PDF. {@code null} si aún no se ha generado. */
    private String pdfPath;

    /** {@code true} si la factura incluye código QR. */
    private boolean hasQr;

    /** {@code true} si la factura incluye marca de agua. */
    private boolean hasWatermark;

    /** Texto de la marca de agua. {@code null} si {@code hasWatermark = false}. */
    private String watermarkText;

    // ── Fechas ────────────────────────────────────────────────────────────────

    /** Fecha de emisión de la factura. */
    private LocalDate issueDate;

    /** Fecha y hora de creación del registro en BD. */
    private LocalDateTime createdAt;
}
