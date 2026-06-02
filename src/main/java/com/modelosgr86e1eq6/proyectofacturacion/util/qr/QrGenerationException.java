package com.modelosgr86e1eq6.proyectofacturacion.util.qr;

/**
 * Unchecked exception thrown when ZXing fails to generate a QR code.
 *
 * <p>Extends {@link RuntimeException} so callers are not forced to handle
 * it at every call site. The {@link com.modelosgr86e1eq6.proyectofacturacion
 * .common.exception.GlobalExceptionHandler} will catch this as a generic
 * error and return HTTP 500.</p>
 *
 * @author MrBraro
 */
public class QrGenerationException extends RuntimeException {

    public QrGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
