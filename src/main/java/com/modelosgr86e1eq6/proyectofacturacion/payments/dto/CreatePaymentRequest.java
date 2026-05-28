package com.modelosgr86e1eq6.proyectofacturacion.payments.dto;

import com.modelosgr86e1eq6.proyectofacturacion.payments.entities.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentRequest {

    @NotNull(message = "the invoiceId cannot be null")
    private Integer invoiceId;

    @NotNull(message = "the pay method cannot be null")
    private PaymentMethod method;
}
