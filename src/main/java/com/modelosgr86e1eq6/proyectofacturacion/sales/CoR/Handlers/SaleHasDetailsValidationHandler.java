package com.modelosgr86e1eq6.proyectofacturacion.sales.CoR.Handlers;

import com.modelosgr86e1eq6.proyectofacturacion.common.exception.BusinessException;
import com.modelosgr86e1eq6.proyectofacturacion.sales.CoR.context.SaleValidationContext;
import org.springframework.stereotype.Component;

@Component
public class SaleHasDetailsValidationHandler extends BaseValidationHandler {

    @Override
    protected void handle(
            SaleValidationContext context
    ) {

        if (context.getDetails().isEmpty()) {

            throw new BusinessException(
                    "Cannot confirm a sale with no products"
            );
        }
    }
}
