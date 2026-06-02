package com.modelosgr86e1eq6.proyectofacturacion.auth.entities;

/**
 * Estados posibles de una sesión.
 * Persiste en la columna "status" de la tabla sessions.
 * Reemplaza la representación implícita de estado que antes
 * se infería combinando isActive + revokedAt + expiresAt.
 */
public enum SessionStatus {
    ACTIVE,
    REVOKED,
    EXPIRED
}
 