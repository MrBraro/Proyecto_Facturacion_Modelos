package com.modelosgr86e1eq6.proyectofacturacion.notifications.entities;

import com.modelosgr86e1eq6.proyectofacturacion.notifications.enums.NotificationEvent;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.enums.NotificationStatus;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
 
import java.time.LocalDateTime;
 
@Entity
@Table(name = "notificaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
 
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_id_seq")
    @SequenceGenerator(name = "notification_id_seq", sequenceName = "notification_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;
 
    // Referencia a la factura que originó la notificación (polimórfica, no FK)
    @Column(name = "invoice_id", nullable = false)
    private Integer invoiceId;
 
    // Referencia al cliente receptor
    @Column(name = "client_id", nullable = false)
    private Integer clientId;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private NotificationType type;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "event", nullable = false, length = 30)
    private NotificationEvent event;
 
    // Email o número de teléfono según el canal
    @Column(name = "recipient", nullable = false, length = 200)
    private String recipient;
 
    @Column(name = "subject", length = 200)
    private String subject;
 
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
 
    @Column(name = "attempts", nullable = false)
    @Builder.Default
    private int attempts = 0;
 
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
 
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
 
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}