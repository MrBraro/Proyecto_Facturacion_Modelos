package com.modelosgr86e1eq6.proyectofacturacion.payments.Factory;

import com.modelosgr86e1eq6.proyectofacturacion.payments.dto.CreatePaymentRequest;
import com.modelosgr86e1eq6.proyectofacturacion.payments.entities.Payment;
import com.modelosgr86e1eq6.proyectofacturacion.payments.entities.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component("QR")
@RequiredArgsConstructor
public class DigitalPaymentProcessor implements PaymentProcessor {

    private final PaymentGatewayMock gateway;

    @Override
    public Payment processPayment(CreatePaymentRequest request) {
        String reference = "DIGITAL-" + UUID.randomUUID();

        // Use invoiceId as seed for reproducible results in tests
        boolean approved = gateway.process(reference, request.getInvoiceId());

        PaymentStatus status = approved
                ? PaymentStatus.APROBADO
                : PaymentStatus.RECHAZADO;

        log.info("Digital payment for invoice {}: {}",
                request.getInvoiceId(), status);

        return Payment.builder()
                .status(status)
                .externalReference(reference)
                .paymentGateway("PSE/NEQUI")
                .build();
    }
}