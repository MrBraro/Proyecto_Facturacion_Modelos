package com.modelosgr86e1eq6.proyectofacturacion.sales.dto;

import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.SaleStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Vista completa de una venta incluyendo desglose de totales */
@Data
public class SaleDetailResponse {
    private Integer       id;
    private Integer       clientId;
    private String        clientName;
    private Integer       userId;
    private SaleStatus    state;
    private BigDecimal    subtotal;
    private BigDecimal    iva;
    private BigDecimal    total;
    private LocalDateTime saleDate;
    private LocalDateTime createdAt;
    private List<SaleItemResponse> items;
}