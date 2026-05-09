package com.modelosgr86e1eq6.proyectofacturacion.audits.entities;


import jakarta.persistence.*;
import com.modelosgr86e1eq6.proyectofacturacion.users.entities.User;
import com.modelosgr86e1eq6.proyectofacturacion.auth.entities.Session;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
import java.time.LocalDateTime;
 
@Entity
@Table(name = "audit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEntity {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_audit")
    private Integer idAudit;
 
    // Nullable: LOGIN_FAIL puede no tener usuario autenticado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private User user;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_session")
    private Session session;
 
    @Column(nullable = false, length = 50)
    private String action;
 
    @Column(name = "table_name", length = 50)
    private String tableName;
 
    // No es FK real — apunta a tablas distintas según la acción
    @Column(name = "id_record")
    private Integer idRecord;
 
    @Column(columnDefinition = "TEXT")
    private String detail;
 
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
 
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
 
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}