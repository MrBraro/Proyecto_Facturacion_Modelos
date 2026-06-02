package com.modelosgr86e1eq6.proyectofacturacion.payments.Factory;

import com.modelosgr86e1eq6.proyectofacturacion.payments.dto.CreatePaymentRequest;
import com.modelosgr86e1eq6.proyectofacturacion.payments.entities.Payment;

public interface PaymentProcessor {

    Payment processPayment(CreatePaymentRequest request);
}
