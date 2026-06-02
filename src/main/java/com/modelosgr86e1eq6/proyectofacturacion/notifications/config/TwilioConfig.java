package com.modelosgr86e1eq6.proyectofacturacion.notifications.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import com.twilio.Twilio;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * Inicializa el SDK de Twilio con las credenciales del proyecto.
 * Se ejecuta una sola vez al levantar el contexto de Spring.
 *
 * Credenciales requeridas en application.properties (o variables de entorno):
 *   twilio.account-sid
 *   twilio.auth-token
 *   twilio.phone-number
 */
@Configuration
@ConditionalOnProperty(prefix = "twilio", name = {"account-sid", "auth-token"})
@Slf4j
public class TwilioConfig {
 
    @Value("${twilio.account-sid}")
    private String accountSid;
 
    @Value("${twilio.auth-token}")
    private String authToken;

    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
        log.info("[TwilioConfig] SDK de Twilio inicializado correctamente.");
    }
}