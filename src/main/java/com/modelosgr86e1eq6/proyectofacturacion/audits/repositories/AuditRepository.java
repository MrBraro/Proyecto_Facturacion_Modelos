package com.modelosgr86e1eq6.proyectofacturacion.audits.repositories;

import com.modelosgr86e1eq6.proyectofacturacion.audits.entities.AuditEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
 
import java.time.LocalDateTime;
 
public interface AuditRepository extends JpaRepository<AuditEntity, Integer> {
 
    /*
     * Ejemplo de query JPQL con filtros dinámicos.
     * Este es el patrón que sustituye a JDBC para consultas con múltiples
     * parámetros opcionales: se usan IS NULL como cortocircuito —
     * si el parámetro llega null, esa condición siempre es true y se ignora.
     */
    @Query("""
        SELECT a FROM AuditEntity a
        WHERE (:userId   IS NULL OR a.user.idUser = :userId)
          AND (:action   IS NULL OR a.action      = :action)
          AND (:from     IS NULL OR a.createdAt  >= :from)
          AND (:to       IS NULL OR a.createdAt  <= :to)
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