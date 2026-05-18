package com.modelosgr86e1eq6.proyectofacturacion.sales.dto;

import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.SaleStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Vista resumida de venta para listados paginados */
@Data
public class SaleSummaryResponse {
    private Integer       id;
    private Integer       clientId;
    private String        clientName;
    private Integer       userId;
    private SaleStatus    state;
    private BigDecimal    total;
    private LocalDateTime saleDate;
}