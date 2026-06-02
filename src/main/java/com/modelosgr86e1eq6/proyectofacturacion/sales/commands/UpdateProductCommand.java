package com.modelosgr86e1eq6.proyectofacturacion.sales.commands;

import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.SaleItemResponse;
import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.UpdateSaleDetailRequest;
import com.modelosgr86e1eq6.proyectofacturacion.sales.services.SaleDetailService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateProductCommand implements SaleCommand<SaleItemResponse> {

    private final SaleDetailService       receiver;
    private final Integer                 detailId;
    private final UpdateSaleDetailRequest request;

    @Override
    public SaleItemResponse execute() {
        return receiver.updateDetail(detailId, request);
    }
}