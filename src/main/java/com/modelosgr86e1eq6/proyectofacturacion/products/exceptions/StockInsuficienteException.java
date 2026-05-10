package com.modelosgr86e1eq6.proyectofacturacion.products.exceptions;

import com.modelosgr86e1eq6.proyectofacturacion.common.exception.BusinessException;

/**
 * Excepción lanzada cuando el stock disponible de un producto es insuficiente
 * para satisfacer la cantidad requerida (RF-06).
 *
 * <p>Extiende {@link BusinessException}, por lo que el
 * {@code GlobalExceptionHandler} existente ya la captura automáticamente
 * y retorna HTTP 409 Conflict sin necesidad de modificar ninguna clase.</p>
 *
 * <p>Uso esperado desde {@code ProductoService.validarStock} y reutilizado
 * por {@code VentaService} al descontar stock en una venta.</p>
 *
 * @author MrBraro
 * @see com.modelosgr86e1eq6.proyectofacturacion.common.exception.BusinessException
 * @see com.modelosgr86e1eq6.proyectofacturacion.products.services.ProductoService
 */
public class StockInsuficienteException extends BusinessException {

    /**
     * Construye la excepción con un mensaje descriptivo que incluye los valores
     * de stock actual y cantidad requerida para facilitar el diagnóstico.
     *
     * @param productoId       identificador del producto con stock insuficiente
     * @param stockActual      cantidad actualmente disponible
     * @param cantidadRequerida cantidad solicitada que supera el stock
     */
    public StockInsuficienteException(Integer productoId, int stockActual, int cantidadRequerida) {
        super(String.format(
                "Stock insuficiente para el producto %d: disponible %d, requerido %d",
                productoId, stockActual, cantidadRequerida));
    }
}
