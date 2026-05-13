package com.modelosgr86e1eq6.proyectofacturacion.users.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
 
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
 
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Integer idUser;
 
    @Column(name = "name", nullable = false, length = 100)
    private String name;
 
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
 
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;
 
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
 
    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;
 
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
 
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
 
    // ── UserDetails implementation ──────────────────────────────────────────
 
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
 
    @Override
    public String getPassword() {
        return passwordHash;
    }
 
    @Override
    public String getUsername() {
        return email;
    }
 
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
 
    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }
 
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
 
    @Override
    public boolean isEnabled() {
        return isActive;
    }
}