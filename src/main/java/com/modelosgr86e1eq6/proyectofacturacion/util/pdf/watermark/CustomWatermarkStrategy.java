package com.modelosgr86e1eq6.proyectofacturacion.util.pdf.watermark;

import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoicePayStatus;

/**
 * Watermark strategy that uses a caller-supplied free-text string,
 * regardless of payment status.
 *
 * <p>Useful when the API consumer wants to stamp a custom label
 * (e.g., "DRAFT", "COPY", "CONFIDENTIAL") independent of the invoice
 * lifecycle state.</p>
 *
 * @author MrBraro
 */
public class CustomWatermarkStrategy implements WatermarkStrategy {

    private final String text;

    /**
     * Creates a custom watermark strategy with the given text.
     *
     * @param text watermark label to apply; must not be blank
     */
    public CustomWatermarkStrategy(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Watermark text must not be blank");
        }
        this.text = text;
    }

    @Override
    public String resolveText(InvoicePayStatus status) {
        return text;
    }
}
