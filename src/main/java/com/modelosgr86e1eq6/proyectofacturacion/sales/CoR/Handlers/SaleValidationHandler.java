package com.modelosgr86e1eq6.proyectofacturacion.sales.CoR.Handlers;

import com.modelosgr86e1eq6.proyectofacturacion.sales.CoR.context.SaleValidationContext;

public interface SaleValidationHandler {
    void setNext(SaleValidationHandler next);
    void validate(SaleValidationContext context);
}