package com.modelosgr86e1eq6.proyectofacturacion.util.pdf;

/**
 * Unchecked exception thrown when iText 8 fails to generate a PDF document.
 *
 * <p>Consistent with {@link com.modelosgr86e1eq6.proyectofacturacion.util.qr
 * .QrGenerationException}: both wrap checked I/O failures as unchecked
 * exceptions so the service layer stays clean of try/catch blocks for
 * infrastructure concerns. The {@link com.modelosgr86e1eq6.proyectofacturacion
 * .common.exception.GlobalExceptionHandler} will catch this and return HTTP 500.</p>
 *
 * @author MrBraro
 */
public class PdfGenerationException extends RuntimeException {

    public PdfGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
