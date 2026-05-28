package com.modelosgr86e1eq6.proyectofacturacion.notifications.pattern.decorator;

import com.modelosgr86e1eq6.proyectofacturacion.notifications.dto.NotificationContext;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.entities.Notification;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.enums.NotificationStatus;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
 
// ─────────────────────────────────────────────────────────────────────────────
//  INTERFAZ DEL COMPONENTE
//  Contrato que implementan tanto el componente base como todos los decoradores
// ─────────────────────────────────────────────────────────────────────────────
interface Notifier {
    /**
     * Ejecuta el envío de la notificación para el contexto dado.
     * Cada decorador llama primero a su wrapped.send() y luego
     * añade su propio canal de envío.
     */
    void send(NotificationContext context);
}
 
 
// ─────────────────────────────────────────────────────────────────────────────
//  COMPONENTE CONCRETO BASE
//  Persiste el registro de notificación en BD con estado PENDING.
//  No envía por ningún canal. Es el núcleo que todos los decoradores envuelven.
// ─────────────────────────────────────────────────────────────────────────────
@RequiredArgsConstructor
@Slf4j
class BaseNotifier implements Notifier {
 
    private final NotificationRepository notificationRepository;
 
    @Override
    public void send(NotificationContext context) {
        log.debug("[BaseNotifier] Persistiendo notificación para factura: {}", context.getInvoiceId());
 
        Notification notification = Notification.builder()
                .invoiceId(context.getInvoiceId())
                .clientId(context.getClientId())
                .type(context.getType())
                .event(context.getEvent())
                .recipient(resolveRecipient(context))
                .subject(context.getSubject())
                .message(context.getMessage())
                .status(NotificationStatus.PENDING)
                .build();
 
        notificationRepository.save(notification);
    }
 
    // Para EMAIL_SMS se guarda email como recipient principal;
    // el decorador SMS usa clientPhone directamente del context
    private String resolveRecipient(NotificationContext context) {
        return context.getClientEmail() != null
                ? context.getClientEmail()
                : context.getClientPhone();
    }
}
 
 
// ─────────────────────────────────────────────────────────────────────────────
//  DECORADOR ABSTRACTO
//  Todos los decoradores concretos extienden esta clase.
//  Mantiene la referencia al Notifier envuelto (composición).
// ─────────────────────────────────────────────────────────────────────────────
abstract class NotificationDecorator implements Notifier {
 
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