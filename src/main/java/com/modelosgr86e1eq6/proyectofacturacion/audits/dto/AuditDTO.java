package com.modelosgr86e1eq6.proyectofacturacion.audits.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO seguro para AuditEntity que excluye información sensible como tokens y sesiones completas.
 * Solo expone campos públicos seguros: userId, action, detail, ipAddress, createdAt, etc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditDTO {

    private Integer idAudit;

    /** ID del usuario que realizó la acción (puede ser null si es LOGIN_FAIL sin autenticación) */
    private Integer userId;

    /** Nombre del usuario (informativo, puede ser null) */
    private String userName;

    /** Tipo de acción realizada (LOGIN, LOGOUT, CREATE, UPDATE, DELETE, etc.) */
    private String action;

    /** Nombre de la tabla afectada (puede ser null) */
    private String tableName;

    /** ID del registro modificado (puede ser null) */
    private Integer idRecord;

    /** Detalles de la acción (cambios, errores, etc.) */
    private String detail;

    /** Dirección IP desde donde se realizó la acción */
    private String ipAddress;

    /** Timestamp de cuándo ocurrió la acción */
    private LocalDateTime createdAt;
}
