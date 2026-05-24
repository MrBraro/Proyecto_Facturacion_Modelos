package com.modelosgr86e1eq6.proyectofacturacion.util.pdf.watermark;

import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoicePayStatus;
import lombok.extern.slf4j.Slf4j;

/**
 * Extensible no-op placeholder for logo-based watermark rendering.
 *
 * <h3>Current behaviour</h3>
 * <p>Returns an empty string (no watermark text) because image-based watermarks
 * are rendered differently from text watermarks and require a separate rendering
 * pipeline in {@code PdfGeneratorUtil}. This class acts as a stub that signals
 * "logo watermark requested" without breaking the current text-based flow.</p>
 *
 * <h3>How to extend</h3>
 * <p>When logo watermark support is implemented:</p>
 * <ol>
 *   <li>Add an {@code isLogoWatermark()} method to {@link WatermarkStrategy}
 *       with a default returning {@code false}; override to {@code true} here.</li>
 *   <li>In {@code PdfGeneratorUtil}, check the flag and render the logo image
 *       via iText's {@code Image} API instead of the diagonal text canvas.</li>
 * </ol>
 *
 * @author MrBraro
 */
@Slf4j
public class LogoWatermarkStrategy implements WatermarkStrategy {

    @Override
    public String resolveText(InvoicePayStatus status) {
        log.debug("LogoWatermarkStrategy invoked — logo rendering not yet implemented, returning empty text");
        return "";
    }
}
