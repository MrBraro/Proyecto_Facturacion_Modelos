package com.modelosgr86e1eq6.proyectofacturacion.sales.CoR.Config;

import com.modelosgr86e1eq6.proyectofacturacion.sales.CoR.Handlers.SaleHasDetailsValidationHandler;
import com.modelosgr86e1eq6.proyectofacturacion.sales.CoR.Handlers.SaleStatusValidationHandler;
import com.modelosgr86e1eq6.proyectofacturacion.sales.CoR.Handlers.SaleValidationHandler;
import com.modelosgr86e1eq6.proyectofacturacion.sales.CoR.Handlers.StockValidationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SaleValidationChainConfig {

    @Bean
    public SaleValidationHandler saleValidationChain(
            SaleStatusValidationHandler saleStatusValidation,
            SaleHasDetailsValidationHandler saleHasDetailsValidation,
            StockValidationHandler stockValidation) {

        saleStatusValidation.setNext(
                saleHasDetailsValidation
        );

        saleHasDetailsValidation.setNext(
                stockValidation
        );

        return saleStatusValidation;
    }
}
