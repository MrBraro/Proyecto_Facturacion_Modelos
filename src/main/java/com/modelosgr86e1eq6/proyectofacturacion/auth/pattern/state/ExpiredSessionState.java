package com.modelosgr86e1eq6.proyectofacturacion.auth.pattern.state;
import com.modelosgr86e1eq6.proyectofacturacion.auth.entities.Session;
import com.modelosgr86e1eq6.proyectofacturacion.common.exception.BusinessException;
// ─────────────────────────────────────────────────────────────────────────────
//  ESTADO: EXPIRADA
//  Estado terminal. No acepta ninguna transición.
// ─────────────────────────────────────────────────────────────────────────────
public class ExpiredSessionState implements SessionState {
 
    @Override
    public boolean isValid(Session session) {
        return false;
    }
 
    @Override
    public void revoke(Session session) {
        throw new BusinessException("No se puede revocar una sesión ya expirada");
    }
 
    @Override
    public void validate(Session session) {
        throw new BusinessException("El token ha expirado");
    }
}