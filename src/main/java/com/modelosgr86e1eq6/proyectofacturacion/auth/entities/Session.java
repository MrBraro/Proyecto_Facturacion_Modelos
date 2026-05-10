package com.modelosgr86e1eq6.proyectofacturacion.auth.entities;

import jakarta.persistence.*;
import lombok.*;
import com.modelosgr86e1eq6.proyectofacturacion.users.entities.User;
 
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
 
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
 
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;
 
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}