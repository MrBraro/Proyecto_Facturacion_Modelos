package com.modelosgr86e1eq6.proyectofacturacion.payments.Factory;

import com.modelosgr86e1eq6.proyectofacturacion.common.exception.BusinessException;
import com.modelosgr86e1eq6.proyectofacturacion.payments.entities.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentProcessorFactory {

    private final Map<String, PaymentProcessor> processors;

    public PaymentProcessor getProcessor(PaymentMethod method) {
        PaymentProcessor processor = processors.get(method.name());
        if (processor == null) {
            throw new BusinessException("No processor found for payment method: " + method);
        }
        return processor;
    }
}