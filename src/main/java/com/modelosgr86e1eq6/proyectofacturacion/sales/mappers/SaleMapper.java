package com.modelosgr86e1eq6.proyectofacturacion.sales.mappers;

import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.SaleDetail;
import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.SaleDetailResponse;
import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.SaleSummaryResponse;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.Sale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper para convertir entre la entidad {@link Sale} y sus DTOs.
 */
@Component
@RequiredArgsConstructor
public class SaleMapper {

    private final SaleDetailMapper saleDetailMapper;

    public SaleSummaryResponse toSummary(Sale sale) {
        SaleSummaryResponse dto = new SaleSummaryResponse();
        dto.setId(sale.getId());
        dto.setClientId(sale.getClient().getIdClient());
        dto.setClientName(sale.getClient().getName());
        dto.setUserId(sale.getUser().getIdUser());
        dto.setState(sale.getState());
        dto.setTotal(sale.getTotal());
        dto.setSaleDate(sale.getSaleDate());
        return dto;
    }

    public SaleDetailResponse toDetail(Sale sale, List<SaleDetail> details) {
        SaleDetailResponse dto = new SaleDetailResponse();
        dto.setId(sale.getId());
        dto.setClientId(sale.getClient().getIdClient());
        dto.setClientName(sale.getClient().getName());
        dto.setUserId(sale.getUser().getIdUser());
        dto.setState(sale.getState());
        dto.setSubtotal(sale.getSubtotal());
        dto.setIva(sale.getIva());
        dto.setTotal(sale.getTotal());
        dto.setSaleDate(sale.getSaleDate());
        dto.setCreatedAt(sale.getCreatedAt());
        dto.setItems(details.stream().map(saleDetailMapper::toResponse).toList());
        return dto;
    }
}