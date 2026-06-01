package com.modelosgr86e1eq6.proyectofacturacion.auth.pattern.state;

import com.modelosgr86e1eq6.proyectofacturacion.auth.entities.Session;
// ─────────────────────────────────────────────────────────────────────────────
//  INTERFAZ DEL ESTADO
//  Define las tres operaciones cuyo comportamiento varía según el estado.
//
//  - isValid()  : usada por JwtAuthFilter para decidir si autenticar
//  - revoke()   : usada por AuthService.logout() y resetPassword()
//  - validate() : usada por AuthService.resetPassword() para validar token de reset
// ─────────────────────────────────────────────────────────────────────────────
public interface SessionState {
    boolean isValid(Session session);
    void    revoke(Session session);
    void    validate(Session session);
}