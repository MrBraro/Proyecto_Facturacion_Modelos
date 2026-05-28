package com.modelosgr86e1eq6.proyectofacturacion.notifications.repositories;

import com.modelosgr86e1eq6.proyectofacturacion.notifications.entities.Notification;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.enums.NotificationStatus;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
import java.util.Optional;
 
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
 
    // Usado por el decorador abstracto para actualizar el estado del último registro
    Optional<Notification> findTopByInvoiceIdAndTypeOrderByCreatedAtDesc(
            Integer invoiceId, NotificationType type);
 
    // Consulta de notificaciones fallidas (para reintento por scheduler)
    List<Notification> findAllByStatus(NotificationStatus status);
 
    // Historial de notificaciones de una factura específica
    List<Notification> findAllByInvoiceIdOrderByCreatedAtDesc(Integer invoiceId);
}