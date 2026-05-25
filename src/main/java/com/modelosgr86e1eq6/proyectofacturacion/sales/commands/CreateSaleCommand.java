package com.modelosgr86e1eq6.proyectofacturacion.sales.commands;

import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.CreateSaleRequest;
import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.SaleDetailResponse;
import com.modelosgr86e1eq6.proyectofacturacion.sales.services.SaleService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateSaleCommand implements SaleCommand<SaleDetailResponse> {

    private final SaleService       receiver;
    private final CreateSaleRequest request;

    @Override
    public SaleDetailResponse execute() {
        return receiver.create(request);
    }
}