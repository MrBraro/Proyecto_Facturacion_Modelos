package com.modelosgr86e1eq6.proyectofacturacion.products.exceptions;

import com.modelosgr86e1eq6.proyectofacturacion.common.exception.BusinessException;

/**
 * Excepción lanzada cuando el stock disponible de un producto es insuficiente
 * para satisfacer la cantidad requerida (RF-06).
 *
 * <p>Extiende {@link BusinessException}, por lo que el {@code GlobalExceptionHandler}
 * existente la captura automáticamente y retorna HTTP 409 Conflict sin necesidad
 * de modificar ninguna clase.</p>
 *
 * @author MrBraro
 * @see com.modelosgr86e1eq6.proyectofacturacion.products.services.ProductService
 */
public class InsufficientStockException extends BusinessException {

    /**
     * Construye la excepción con un mensaje descriptivo que incluye el stock
     * disponible y la cantidad requerida para facilitar el diagnóstico.
     *
     * @param productId        identificador del producto con stock insuficiente
     * @param availableStock   cantidad actualmente disponible
     * @param requiredQuantity cantidad solicitada que supera el stock
     */
    public InsufficientStockException(Integer productId, int availableStock, int requiredQuantity) {
        super(String.format(
                "Stock insuficiente para el producto %d: disponible %d, requerido %d",
                productId, availableStock, requiredQuantity));
    }
}
