package com.modelosgr86e1eq6.proyectofacturacion.sales.mappers;

import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.SaleItemResponse;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.SaleDetail;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre la entidad {@link SaleDetail} y sus DTOs.
 */
@Component
public class SaleDetailMapper {

    public SaleItemResponse toResponse(SaleDetail detail) {
        SaleItemResponse dto = new SaleItemResponse();
        dto.setId(detail.getId());
        dto.setProductId(detail.getProduct().getIdProduct());
        dto.setProductName(detail.getProduct().getName());
        dto.setQuantity(detail.getQuantity());
        dto.setUnitPrice(detail.getUnitPrice());
        dto.setLineSubtotal(detail.getLineSubtotal());
        return dto;
    }
}