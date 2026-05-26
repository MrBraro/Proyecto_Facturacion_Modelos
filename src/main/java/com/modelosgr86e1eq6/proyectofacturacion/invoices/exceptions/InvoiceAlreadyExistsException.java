package com.modelosgr86e1eq6.proyectofacturacion.invoices.exceptions;

import com.modelosgr86e1eq6.proyectofacturacion.common.exception.BusinessException;

/**
 * Excepción lanzada cuando se intenta generar una factura para una venta
 * que ya tiene una factura asociada.
 *
 * <p>Extiende {@link BusinessException}, por lo que el {@code GlobalExceptionHandler}
 * existente la captura automáticamente y retorna HTTP 409 Conflict sin necesidad
 * de modificar ninguna clase de infraestructura.</p>
 *
 * <p>La restricción 1:1 entre {@code Sale} e {@code Invoice} se valida
 * a nivel de aplicación (aquí) antes del INSERT, complementando la
 * restricción {@code UNIQUE} a nivel de base de datos.</p>
 *
 * @author MrBraro
 * @see com.modelosgr86e1eq6.proyectofacturacion.invoices.services.InvoiceService
 */
public class InvoiceAlreadyExistsException extends BusinessException {

    /**
     * Construye la excepción indicando la venta duplicada.
     *
     * @param saleId identificador de la venta que ya tiene factura
     */
    public InvoiceAlreadyExistsException(Integer saleId) {
        super("An invoice already exists for sale ID: " + saleId);
    }
}
