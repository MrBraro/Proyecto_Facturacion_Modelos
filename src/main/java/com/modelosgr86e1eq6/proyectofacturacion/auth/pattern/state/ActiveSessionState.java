package com.modelosgr86e1eq6.proyectofacturacion.auth.pattern.state;

import com.modelosgr86e1eq6.proyectofacturacion.auth.entities.Session;
import com.modelosgr86e1eq6.proyectofacturacion.auth.entities.SessionStatus;
import java.time.LocalDateTime;
import com.modelosgr86e1eq6.proyectofacturacion.common.exception.BusinessException;
// ─────────────────────────────────────────────────────────────────────────────
//  ESTADO: ACTIVA
//  Única desde la cual todas las transiciones son válidas.
// ─────────────────────────────────────────────────────────────────────────────
public class ActiveSessionState implements SessionState {
 
    @Override
    public boolean isValid(Session session) {
        // Activa pero puede haber expirado por tiempo sin que nadie la haya marcado aún
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            // Transicionar a EXPIRED de forma lazy al detectarlo
            session.setStatus(SessionStatus.EXPIRED);
            session.setActive(false);
            return false;
        }
        return true;
    }
 
    @Override
    public void revoke(Session session) {
        session.setActive(false);
        session.setRevokedAt(LocalDateTime.now());
        session.setStatus(SessionStatus.REVOKED);
    }
 
    @Override
    public void validate(Session session) {
        // Verificar expiración antes de considerar válida
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            session.setStatus(SessionStatus.EXPIRED);
            session.setActive(false);
            throw new BusinessException("El token ha expirado");
        }
        // Si está activa y no expirada: válida, no lanza excepción
    }
}
