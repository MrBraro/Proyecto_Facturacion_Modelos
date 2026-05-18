package com.modelosgr86e1eq6.proyectofacturacion.sales.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SaleItemResponse {
    private Integer    id;
    private Integer    productId;
    private String     productName;
    private int        quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineSubtotal;
}