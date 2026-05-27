package com.modelosgr86e1eq6.proyectofacturacion.sales.CoR.Handlers;

import com.modelosgr86e1eq6.proyectofacturacion.sales.CoR.context.SaleValidationContext;

public abstract class BaseValidationHandler implements  SaleValidationHandler {

    private SaleValidationHandler next;

    @Override
    public void setNext(SaleValidationHandler next) {
        this.next = next;
    }

    @Override
    public void validate(SaleValidationContext context) {

        handle(context);

        if (next != null) {
            next.validate(context);
        }
    }

    protected abstract void handle(SaleValidationContext context);
}
