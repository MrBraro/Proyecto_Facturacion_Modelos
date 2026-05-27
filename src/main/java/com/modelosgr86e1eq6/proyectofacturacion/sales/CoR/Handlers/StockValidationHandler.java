package com.modelosgr86e1eq6.proyectofacturacion.sales.CoR.Handlers;

import com.modelosgr86e1eq6.proyectofacturacion.common.exception.BusinessException;
import com.modelosgr86e1eq6.proyectofacturacion.sales.CoR.context.SaleValidationContext;
import org.springframework.stereotype.Component;

@Component
public class StockValidationHandler extends BaseValidationHandler {

    @Override
    protected void handle(SaleValidationContext context) {

        context.getDetails().forEach(detail -> {

            var product = detail.getProduct();

            if (product.getStock()
                    < detail.getQuantity()) {

                throw new BusinessException(
                        "Insufficient stock for product "
                                + product.getName()
                                + ". Available: "
                                + product.getStock()
                                + ", required: "
                                + detail.getQuantity()
                );
            }
        });
    }
}