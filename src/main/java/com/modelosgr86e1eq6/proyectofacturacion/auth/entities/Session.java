package com.modelosgr86e1eq6.proyectofacturacion.auth.entities;

import jakarta.persistence.*;
import lombok.*;
import com.modelosgr86e1eq6.proyectofacturacion.users.entities.User;
import com.modelosgr86e1eq6.proyectofacturacion.auth.pattern.state.SessionStateFactory;
import com.modelosgr86e1eq6.proyectofacturacion.auth.pattern.state.SessionState;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_session")
    private Integer idSession;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;
 
    @Column(nullable = false, unique = true, length = 512)
    private String token;
 
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
 
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Campo de estado explícito — reemplaza la representación implícita
     * que antes requería combinar isActive + revokedAt + expiresAt
     * para inferir en qué estado estaba la sesión.
     *
     * Se persiste como String en la columna "status" para legibilidad en BD.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private SessionStatus status = SessionStatus.ACTIVE;
 
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
 
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;
 
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Punto de entrada al patrón State.
     * Retorna la implementación de SessionState correspondiente al estado actual.
     * Cada llamada resuelve el estado en tiempo de ejecución desde la factory,
     * sin necesidad de almacenar el objeto de estado (no es serializable).
     */
    @Transient
    public SessionState getState() {
        return SessionStateFactory.resolve(this.status);
    }
}