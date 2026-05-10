package com.modelosgr86e1eq6.proyectofacturacion.audits.services;

import com.modelosgr86e1eq6.proyectofacturacion.audits.entities.AuditEntity;
import com.modelosgr86e1eq6.proyectofacturacion.auth.entities.Session;
import com.modelosgr86e1eq6.proyectofacturacion.users.entities.User;
import com.modelosgr86e1eq6.proyectofacturacion.audits.repositories.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
 
@Service
@RequiredArgsConstructor
public class AuditService {
 
    private final AuditRepository auditRepository;
 
    // Catálogo de acciones — constantes para evitar strings sueltos
    public static final String LOGIN_OK          = "LOGIN_OK";
    public static final String LOGIN_FAIL        = "LOGIN_FAIL";
    public static final String LOGOUT            = "LOGOUT";
    public static final String PASSWORD_CHANGED  = "PASSWORD_CHANGED";
    public static final String USER_CREATED      = "USER_CREATED";
    public static final String USER_UPDATED      = "USER_UPDATED";
    public static final String USER_DEACTIVATED  = "USER_DEACTIVATED";
    public static final String USER_ACTIVATED    = "USER_ACTIVATED";
 
    /**
     * Registra un evento de auditoría de forma asíncrona para no bloquear
     * el hilo principal de la request.
     */
    @Async
    public void log(String action,
                    User user,
                    Session session,
                    String ipAddress,
                    String detail) {
 
        AuditEntity audit = AuditEntity.builder()
                .action(action)
                .user(user)
                .session(session)
                .ipAddress(ipAddress)
                .detail(detail)
                .build();
 
        auditRepository.save(audit);
    }
 
    /** Versión simplificada sin sesión (para LOGIN_FAIL) */
    @Async
    public void log(String action, String ipAddress, String detail) {
        log(action, null, null, ipAddress, detail);
    }
 
    // ── Consulta ──────────────────────────────────────────────────────────────
 
    public Page<AuditEntity> findByFilters(Integer userId, String action,
                                           LocalDateTime from, LocalDateTime to,
                                           Pageable pageable) {
        return auditRepository.findByFilters(userId, action, from, to, pageable);
    }
}