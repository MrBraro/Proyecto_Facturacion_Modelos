package com.modelosgr86e1eq6.proyectofacturacion.payments.dto;

import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoicePayStatus;
import com.modelosgr86e1eq6.proyectofacturacion.payments.entities.PaymentMethod;
import com.modelosgr86e1eq6.proyectofacturacion.payments.entities.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
public class PaymentResponse {
    private Long           id;
    private Integer        invoiceId;
    private InvoicePayStatus invoicePayStatus;
    private Integer        userId;
    private PaymentMethod  type;
    private PaymentStatus  status;
    private BigDecimal     amount;
    private String         externalReference;
    private String         paymentGateway;
    private LocalDateTime  paymentDate;
}