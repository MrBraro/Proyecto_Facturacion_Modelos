package com.modelosgr86e1eq6.proyectofacturacion.sales.commands;

import com.modelosgr86e1eq6.proyectofacturacion.sales.services.SaleDetailService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RemoveProductCommand implements SaleCommand<Void> {

    private final SaleDetailService receiver;
    private final Integer           detailId;

    @Override
    public Void execute() {
        receiver.deleteDetail(detailId);
        return null;
    }
}