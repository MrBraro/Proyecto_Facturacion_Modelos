package com.modelosgr86e1eq6.proyectofacturacion.auth.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.modelosgr86e1eq6.proyectofacturacion.auth.entities.Session;
 
public interface SessionRepository extends JpaRepository<Session, Integer> {
 
    Optional<Session> findByToken(String token);
 
    /**
     * Revoca todas las sesiones activas de un usuario.
     * Actualiza TANTO isActive COMO status para mantener consistencia
     * entre el campo legacy (isActive) y el nuevo campo del patrón State (status).
     */
    @Modifying
    @Query("""
        UPDATE Session s
        SET s.isActive = false,
            s.status   = 'REVOKED',
            s.revokedAt = CURRENT_TIMESTAMP
        WHERE s.user.idUser = :userId
          AND s.isActive = true
        """)
    void revokeAllByUserId(@Param("userId") Integer userId);
 
    /**
     * Revoca todas las sesiones activas de un usuario excepto la sesión actual.
     * Permite al usuario continuar en el dispositivo actual pero desconecta otros.
     */
    @Modifying
    @Query("""
        UPDATE Session s
        SET s.isActive = false,
            s.status   = 'REVOKED',
            s.revokedAt = CURRENT_TIMESTAMP
        WHERE s.user.idUser  = :userId
          AND s.isActive     = true
          AND s.token       != :currentToken
        """)
    void revokeAllByUserIdExceptToken(@Param("userId")       Integer userId,
                                      @Param("currentToken") String  currentToken);
}