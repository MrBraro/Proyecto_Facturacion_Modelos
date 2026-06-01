package com.modelosgr86e1eq6.proyectofacturacion.auth.pattern.state;

import com.modelosgr86e1eq6.proyectofacturacion.auth.entities.SessionStatus;
import org.springframework.stereotype.Component;
 
/**
 * Resuelve la implementación de SessionState correcta para un SessionStatus dado.
 * Centraliza la lógica de instanciación para que ningún otro componente
 * tenga que hacer switch/if sobre el enum directamente.
 *
 * Usado por: Session.getState(), AuthService, JwtAuthFilter.
 */
@Component
public class SessionStateFactory {
 
    private static final SessionState ACTIVE  = new ActiveSessionState();
    private static final SessionState REVOKED = new RevokedSessionState();
    private static final SessionState EXPIRED = new ExpiredSessionState();
 
    public static SessionState resolve(SessionStatus status) {
        return switch (status) {
            case ACTIVE  -> ACTIVE;
            case REVOKED -> REVOKED;
            case EXPIRED -> EXPIRED;
        };
    }
}