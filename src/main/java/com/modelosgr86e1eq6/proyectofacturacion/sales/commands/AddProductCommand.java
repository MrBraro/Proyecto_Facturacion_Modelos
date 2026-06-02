package com.modelosgr86e1eq6.proyectofacturacion.sales.commands;

import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.CreateSaleDetailRequest;
import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.SaleItemResponse;
import com.modelosgr86e1eq6.proyectofacturacion.sales.services.SaleDetailService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AddProductCommand implements SaleCommand<SaleItemResponse> {

    private final SaleDetailService       receiver;
    private final Integer                 saleId;
    private final CreateSaleDetailRequest request;

    @Override
    public SaleItemResponse execute() {
        return receiver.addDetail(saleId, request);
    }
}