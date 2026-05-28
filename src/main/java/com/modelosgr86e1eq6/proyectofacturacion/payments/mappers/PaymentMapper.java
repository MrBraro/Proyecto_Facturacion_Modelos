package com.modelosgr86e1eq6.proyectofacturacion.payments.mappers;

import com.modelosgr86e1eq6.proyectofacturacion.payments.dto.PaymentResponse;
import com.modelosgr86e1eq6.proyectofacturacion.payments.entities.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .invoiceId(payment.getInvoice().getIdInvoice())
                .invoicePayStatus(payment.getInvoice().getPayStatus())
                .userId(payment.getUser().getIdUser())
                .type(payment.getType())
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .externalReference(payment.getExternalReference())
                .paymentGateway(payment.getPaymentGateway())
                .paymentDate(payment.getPaymentDate())
                .build();
    }
}