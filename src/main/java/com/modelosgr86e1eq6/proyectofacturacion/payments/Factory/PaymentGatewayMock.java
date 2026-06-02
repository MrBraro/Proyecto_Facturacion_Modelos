package com.modelosgr86e1eq6.proyectofacturacion.payments.Factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class PaymentGatewayMock {

    private static final double SUCCESS_RATE = 0.80; // 80% success rate

    public boolean process(String reference, long seed) {
        Random random = new Random(seed);
        boolean approved = random.nextDouble() < SUCCESS_RATE;
        log.info("Gateway simulation for reference {}: {}", reference, approved ? "APPROVED" : "REJECTED");
        return approved;
    }
}