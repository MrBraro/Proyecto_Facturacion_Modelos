package com.modelosgr86e1eq6.proyectofacturacion.sales.commands;

import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.SaleDetailResponse;
import com.modelosgr86e1eq6.proyectofacturacion.sales.services.SaleService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CancelSaleCommand implements SaleCommand<SaleDetailResponse> {

    private final SaleService receiver;
    private final Integer     saleId;

    @Override
    public SaleDetailResponse execute() {
        return receiver.cancelSale(saleId);
    }
}