package com.modelosgr86e1eq6.proyectofacturacion.audits.mappers;

import org.springframework.stereotype.Component;
import com.modelosgr86e1eq6.proyectofacturacion.audits.dto.AuditDTO;
import com.modelosgr86e1eq6.proyectofacturacion.audits.entities.AuditEntity;


/**
 * Mapper para convertir AuditEntity a AuditDTO.
 * Excluye campos sensibles como Session (que contiene el token JWT) y la entidad User completa.
 */
@Component
public class AuditMapper {

    /**
     * Convierte una AuditEntity a un AuditDTO seguro para serialización.
     *
     * @param audit la entidad de auditoría
     * @return un DTO con solo información pública/segura
     */
    public AuditDTO toDTO(AuditEntity audit) {
        if (audit == null) {
            return null;
        }

        return AuditDTO.builder()
                .idAudit(audit.getIdAudit())
                .userId(audit.getUser() != null ? audit.getUser().getIdUser() : null)
                .userName(audit.getUser() != null ? audit.getUser().getName() : null)
                .action(audit.getAction())
                .tableName(audit.getTableName())
                .idRecord(audit.getIdRecord())
                .detail(audit.getDetail())
                .ipAddress(audit.getIpAddress())
                .createdAt(audit.getCreatedAt())
                .build();
    }
}
