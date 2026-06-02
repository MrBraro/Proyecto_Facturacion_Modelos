package com.modelosgr86e1eq6.proyectofacturacion.sales.CoR.Handlers;

import com.modelosgr86e1eq6.proyectofacturacion.common.exception.BusinessException;
import com.modelosgr86e1eq6.proyectofacturacion.sales.CoR.context.SaleValidationContext;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.SaleStatus;
import org.springframework.stereotype.Component;

@Component
public class SaleStatusValidationHandler extends BaseValidationHandler {

    @Override
    protected void handle(SaleValidationContext context) {
        if (context.getSale().getState() != SaleStatus.ABIERTA) {

            throw new BusinessException(
                    "Sale with id "
                            + context.getSale().getId()
                            + " cannot be confirmed. Current status: "
                            + context.getSale().getState()
            );
        }
    }
}