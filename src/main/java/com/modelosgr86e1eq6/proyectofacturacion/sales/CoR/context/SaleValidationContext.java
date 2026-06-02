package com.modelosgr86e1eq6.proyectofacturacion.sales.CoR.context;

import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.Sale;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.SaleDetail;
import lombok.Getter;

import java.util.List;

@Getter
public class SaleValidationContext {

    private final Sale sale;
    private final List<SaleDetail> details;

    public SaleValidationContext(Sale sale, List<SaleDetail> details) {
        this.sale = sale;
        this.details = details;
    }

}