package com.modelosgr86e1eq6.proyectofacturacion.notifications.pattern.decorator;

import com.modelosgr86e1eq6.proyectofacturacion.notifications.dto.NotificationContext;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.enums.NotificationStatus;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.repositories.NotificationRepository;

// ─────────────────────────────────────────────────────────────────────────────
//  DECORADOR ABSTRACTO
//  Todos los decoradores concretos extienden esta clase.
//  Mantiene la referencia al Notifier envuelto (composición).
// ─────────────────────────────────────────────────────────────────────────────
public abstract class NotificationDecorator implements Notifier {
 
    protected final Notifier             wrapped;
    protected final NotificationRepository notificationRepository;
 
    protected NotificationDecorator(Notifier wrapped,
                                    NotificationRepository notificationRepository) {
        this.wrapped                = wrapped;
        this.notificationRepository = notificationRepository;
    }
 
    @Override
    public void send(NotificationContext context) {
        // Primero delega hacia adentro de la cadena
        wrapped.send(context);
        // Luego ejecuta el comportamiento propio del canal
        doSend(context);
    }
 
    /**
     * Cada decorador concreto implementa aquí su lógica de envío.
     * Debe actualizar el estado del registro en BD (SENT / FAILED).
     */
    protected abstract void doSend(NotificationContext context);
 
    // Helper: actualiza el estado del último registro persistido para este invoice+type
    protected void updateStatus(NotificationContext context, NotificationStatus status) {
        notificationRepository
                .findTopByInvoiceIdAndTypeOrderByCreatedAtDesc(
                        context.getInvoiceId(), context.getType())
                .ifPresent(n -> {
                    n.setStatus(status);
                    if (status == NotificationStatus.SENT) {
                        n.setSentAt(java.time.LocalDateTime.now());
                    }
                    n.setAttempts(n.getAttempts() + 1);
                    notificationRepository.save(n);
                });
    }
}