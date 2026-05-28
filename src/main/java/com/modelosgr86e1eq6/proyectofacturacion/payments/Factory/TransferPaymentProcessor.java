package com.modelosgr86e1eq6.proyectofacturacion.payments.Factory;

import com.modelosgr86e1eq6.proyectofacturacion.payments.dto.CreatePaymentRequest;
import com.modelosgr86e1eq6.proyectofacturacion.payments.entities.Payment;
import com.modelosgr86e1eq6.proyectofacturacion.payments.entities.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("TRANSFER")
public class TransferPaymentProcessor implements PaymentProcessor {

    @Override
    public Payment processPayment(CreatePaymentRequest request) {

        return Payment.builder()
                .status(PaymentStatus.PENDIENTE)
                .externalReference("TRANSFER-" + UUID.randomUUID())
                .build();
    }
}
