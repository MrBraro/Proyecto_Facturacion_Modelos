package com.modelosgr86e1eq6.proyectofacturacion.notifications.pattern.decorator;

import com.modelosgr86e1eq6.proyectofacturacion.notifications.dto.NotificationContext;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.enums.NotificationStatus;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.repositories.NotificationRepository;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.entities.Notification;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.enums.NotificationType;

// ─────────────────────────────────────────────────────────────────────────────
//  DECORADOR ABSTRACTO
//  Todos los decoradores concretos extienden esta clase.
//  Mantiene la referencia al Notifier envuelto (composición).
// ─────────────────────────────────────────────────────────────────────────────
// ─────────────────────────────────────────────────────────────────────────────
//  DECORADOR ABSTRACTO
// ─────────────────────────────────────────────────────────────────────────────
public abstract class NotificationDecorator implements Notifier {
 
    protected final Notifier                wrapped;
    protected final NotificationRepository  notificationRepository;
 
    protected NotificationDecorator(Notifier wrapped,
                                    NotificationRepository notificationRepository) {
        this.wrapped                = wrapped;
        this.notificationRepository = notificationRepository;
    }
 
    @Override
    public final void send(NotificationContext context) {
        wrapped.send(context);  // delega hacia el interior de la cadena primero
        doSend(context);        // luego ejecuta este canal
    }
 
    protected abstract void doSend(NotificationContext context);
 
    /**
     * Cada decorador concreto crea su propio registro PENDING antes de enviar
     * y lo actualiza a SENT o FAILED según el resultado.
     * El registro se identifica por el ID devuelto al persistir, no por búsqueda,
     * eliminando la ambigüedad entre canales del mismo invoice.
     */
    protected Notification createPendingRecord(NotificationContext context,
                                               NotificationType channel,
                                               String recipient) {
        Notification record = Notification.builder()
                .invoiceId(context.getInvoiceId())
                .clientId(context.getClientId())
                .type(channel)                      // EMAIL o SMS — nunca una combinación
                .event(context.getEvent())
                .recipient(recipient)
                .subject(context.getSubject())
                .message(context.getMessage())
                .status(NotificationStatus.PENDING)
                .attempts(0)
                .build();
 
        return notificationRepository.save(record);
    }
 
    protected void markSent(Notification record) {
        record.setStatus(NotificationStatus.SENT);
        record.setSentAt(java.time.LocalDateTime.now());
        record.setAttempts(record.getAttempts() + 1);
        notificationRepository.save(record);
    }
 
    protected void markFailed(Notification record) {
        record.setStatus(NotificationStatus.FAILED);
        record.setAttempts(record.getAttempts() + 1);
        notificationRepository.save(record);
    }
}