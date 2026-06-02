package com.modelosgr86e1eq6.proyectofacturacion.audits.repositories;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.modelosgr86e1eq6.proyectofacturacion.audits.entities.AuditEntity;
 
public interface AuditRepository extends JpaRepository<AuditEntity, Integer> {
 
    /*
     * Ejemplo de query JPQL con filtros dinámicos.
     * Usamos COALESCE para evitar problemas de tipado en Postgres
     * cuando los parametros opcionales llegan null.
     */
    @Query("""
        SELECT a FROM AuditEntity a
        WHERE a.user.idUser = COALESCE(:userId, a.user.idUser)
          AND a.action      = COALESCE(:action, a.action)
          AND a.createdAt  >= COALESCE(:from, a.createdAt)
          AND a.createdAt  <= COALESCE(:to, a.createdAt)
        ORDER BY a.createdAt DESC
    """)
    Page<AuditEntity> findByFilters(
            @Param("userId") Integer userId,
            @Param("action") String  action,
            @Param("from")   LocalDateTime from,
            @Param("to")     LocalDateTime to,
            Pageable pageable
    );
}