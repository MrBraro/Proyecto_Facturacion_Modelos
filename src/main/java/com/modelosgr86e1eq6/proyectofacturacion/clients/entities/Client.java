package com.modelosgr86e1eq6.proyectofacturacion.clients.entities;

 
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
import java.time.LocalDateTime;

 
@Entity
@Table(name = "clients", uniqueConstraints = {
        @UniqueConstraint(name = "uq_clientes_email", columnNames = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_client", updatable = false, nullable = false)
    private Integer idClient;
 
    @Column(name = "name", nullable = false, length = 120)
    private String name;
 
    @Column(name = "email", nullable = false, length = 120)
    private String email;
 
    @Column(name = "telephone", length = 20)
    private String telephone;
 
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
 
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
 
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
 
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
 
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
 
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
