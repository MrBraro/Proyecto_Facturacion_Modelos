package com.modelosgr86e1eq6.proyectofacturacion.util.pdf.watermark;

import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoicePayStatus;

/**
 * Strategy interface for resolving the watermark text applied to invoice PDFs.
 *
 * <h3>Design</h3>
 * <p>Follows the Strategy pattern (GoF) to decouple watermark behaviour from
 * the builder and PDF generator. Adding a new watermark type (e.g. logo-based,
 * gateway-branded) only requires a new implementation — no existing classes
 * need to change.</p>
 *
 * <h3>Known implementations</h3>
 * <ul>
 *   <li>{@link StatusWatermarkStrategy}  — text driven by {@link InvoicePayStatus}.</li>
 *   <li>{@link CustomWatermarkStrategy}  — arbitrary free text.</li>
 *   <li>{@link LogoWatermarkStrategy}    — extensible placeholder for image watermarks.</li>
 * </ul>
 *
 * @author MrBraro
 */
public interface WatermarkStrategy {

    /**
     * Resolves the watermark text to be stamped on the invoice PDF.
     *
     * @param status current payment status of the invoice; never {@code null}
     * @return watermark text; never {@code null} or blank
     */
    String resolveText(InvoicePayStatus status);
}
