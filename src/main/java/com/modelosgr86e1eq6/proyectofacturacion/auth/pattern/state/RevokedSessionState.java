package com.modelosgr86e1eq6.proyectofacturacion.auth.pattern.state;
import com.modelosgr86e1eq6.proyectofacturacion.auth.entities.Session;
import com.modelosgr86e1eq6.proyectofacturacion.common.exception.BusinessException;

// ─────────────────────────────────────────────────────────────────────────────
//  ESTADO: REVOCADA
//  Estado terminal. No acepta ninguna transición.
// ─────────────────────────────────────────────────────────────────────────────
public class RevokedSessionState implements SessionState {
 
    @Override
    public boolean isValid(Session session) {
        return false;
    }
 
    @Override
    public void revoke(Session session) {
        throw new BusinessException("La sesión ya fue revocada");
    }
 
    @Override
    public void validate(Session session) {
        throw new BusinessException("Token inválido: la sesión fue revocada");
    }
}