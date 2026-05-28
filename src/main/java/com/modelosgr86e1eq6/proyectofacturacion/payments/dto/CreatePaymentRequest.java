package com.modelosgr86e1eq6.proyectofacturacion.payments.dto;

import com.modelosgr86e1eq6.proyectofacturacion.payments.entities.PaymentMethod;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentRequest {

    private Integer invoiceId;

    private PaymentMethod method;
}
